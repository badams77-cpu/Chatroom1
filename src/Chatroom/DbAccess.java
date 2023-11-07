package Chatroom;


import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;


public class DbAccess {
//  Hold DbEnv for Berkeley DB, all transactions
// through here
	public Charset charset;
	private Environment env;
	
	private String chatBase = null;
	private String blogName = null;
    public final String MESSAGE_TABLE="messages";
	
	private HashMap<String,Database> openDbs;
	private HashMap<String,Environment> openEnvs;
	private HashMap<Cursor,Cursor> openCursors;
	
	private StoredClassCatalog javaCatalog;
	private Database catalogDB;
	
	
	private boolean catalogOpen = false;
	
	public DbAccess(){
	  openDbs = new HashMap<String,Database>();
	  openEnvs = new HashMap<String,Environment>();
	  openCursors = new HashMap<Cursor,Cursor>();
	  chatBase = getChatBase();
	  catalogOpen = false;
	}
	
	/*
	 * 
	 */
	
	// Opens the DB at the message ID
    public RepliesHandle openHandler(String blog, String messageID){
        RepliesHandler rh = new RepliesHandler();
    	rh.myDatabase = this.getAccess(blog,"messages",false);
    	rh.myDab = this;
    	rh.myBlog = blog;
    	try {
          rh.myCursor = rh.myDatabase.openCursor(null,null);
          registerCursor(rh.myCursor);
    	} catch (DatabaseException e){
    	  Logging.warning("failed openning cursor:" +blog+": "+messageID,e);	
    	}
    	Logging.info("openHandler "+blog+" : @"+messageID);
    	rh.setKey(messageID);
        return rh;     
    }
	
	/* READ the config to find dbmBase 
	 * also sets the charset for the DB
	 */		
	
	public String getChatBase(){
		ChatConfig cconf = ChatConfig.getConfig();
		try {
			charset = Charset.forName(ChatConfig.dbmCharset);
		} catch (Exception e){
			Logging.severe( "Charset config failed",e);
		}
		return ChatConfig.dbmBase;
	}
	
/*
 *  Database access, needs 
 * 
 */	
    public Database getAccess(String blogName, String table, boolean readOnly){
	  if (chatBase==null){
	    chatBase = getChatBase();
	  }
	  this.blogName= blogName;
	  File home = new File(chatBase+'/'+Utilities.escape(blogName));
	     if (!home.exists()){
	    	 boolean suc = home.mkdirs();
	    	 if (!suc){
	    		 Logging.severe("Can't make database at"+home);
	             throw new RuntimeException("Can't make db at"+home)
	             ; // Need to send exception really
	    	 } else{
	    		 // New Db
	    	   Logging.info("Creating new db at "+home);
	    	   try {
	             EnvironmentConfig envConfig = new EnvironmentConfig();
	             envConfig.setTransactional(true);
	             envConfig.setAllowCreate(true);
	             envConfig.setReadOnly(false);
	             envConfig.setCacheSize(1000000);
                 Environment env = new Environment(home,envConfig);
                 openEnvs.put(home.toString()+":"+table,env);
	             DatabaseConfig dbConfig = new DatabaseConfig();
	             dbConfig.setAllowCreate(true);
 	             Database db = env.openDatabase(null,table,dbConfig);
 		         openDbs.put(home.toString()+":"+table,db);
 		         return db;
	    	   } catch (DatabaseException ex){
	    		   Logging.severe("DatabaseException, DBAccess while openning DB",ex);
	    	   }
               throw new RuntimeException("No Database");  
	    	 }
	     }	 else {
	    	 EnvironmentConfig envConfig = new EnvironmentConfig();
	         envConfig.setTransactional(true);
	         envConfig.setAllowCreate(true);
	         envConfig.setReadOnly(readOnly);
	         envConfig.setCacheSize(1000000);
	         try {
		       Environment env = new Environment(home,envConfig);
		       openEnvs.put(home.toString()+":"+table,env);
		       DatabaseConfig dbConfig = new DatabaseConfig();
		       dbConfig.setAllowCreate(true);
		       dbConfig.setReadOnly(false);
	           Database db = env.openDatabase(null,table,dbConfig);
	           openDbs.put(home.toString()+":"+table,db);
		       return db;
	        } catch (DatabaseException ex){
	        	Logging.severe("DatabaseException (b) openning DB "+ex);
	        }
	     }
	     throw new RuntimeException("No Database");
    }
    
    private StoredClassCatalog getCatalog(Environment env) throws DatabaseException {
      	if (catalogOpen){ return javaCatalog; }
      	if (env==null){ Logging.warning("Create Env before openCatalog");
      		throw new RuntimeException("Create Env before openCatalog");}
      	DatabaseConfig dbConfig = new DatabaseConfig();
      	dbConfig.setTransactional(true);
      	dbConfig.setAllowCreate(true);
      	ChatConfig cc = ChatConfig.getConfig();
      	String CLASS_CATALOG = cc.classConfig;
      	Database catalogDb = env.openDatabase(null, CLASS_CATALOG, dbConfig);
      	javaCatalog = new StoredClassCatalog(catalogDb);
      	return javaCatalog;
    }
    
    private void closeCatalog(){
        if (!catalogOpen){ return; }
        try {
        	javaCatalog.close();
        } catch (Exception e ){
        	Logging.warning("Error closing class catalog", e);
        }
    }

    public void closeAccess(String blogName,String table){
  	  File home = new File(chatBase+'/'+Utilities.escape(blogName));
  	  String where = home.toString()+":"+table;
  	  try {
  		closeCatalog();  
  	    Database db = openDbs.get(where);
  	    if (db!=null){ db.close(); openDbs.remove(where); }
  	    Environment env = openEnvs.get(where);
  	    if (env!=null){ env.close(); openEnvs.remove(where); }
  	  } catch (DatabaseException ex){
  		  Logging.severe("Failed closing DB",ex);
  	  }
  	}
  
    public void closeAccess(String blogName){
    	closeAccess(blogName, MESSAGE_TABLE);
    }
    
    
    public void closeDatabase(Database myDatabase){
    	try {
    		HashMap hm = Utilities.inverseMap(openDbs);
    		String where = (String) hm.get(myDatabase);
    		myDatabase.close();
    		Environment env = openEnvs.get(where);
    	} catch (DatabaseException ex){
          Logging.severe("Failed closing DB ",ex);
    	}
    }
	     
	public void registerCursor(Cursor cursor){
		openCursors.put(cursor, cursor);
	}
	
	public void closeCursor( Cursor cursor){
		openCursors.remove(cursor);
		try {
		  cursor.close();
		} catch (DatabaseException e){
			Logging.severe("Failed closing cursor",e);
		}
	}

	public void closeCursors(){	
	  for(Iterator<Cursor> cus = openCursors.keySet().iterator(); cus.hasNext(); ){
		  Cursor myCursor = cus.next();
		  try {
		    myCursor.close();
		  } catch (DatabaseException e){
			  Logging.severe("Failed closing cursor: "+e);
		  }
		  openCursors.remove(myCursor);
	  }
	}
	
	public void closeAll(){
		closeCatalog();
		Set<String> ks = openDbs.keySet();
		for(Iterator<String> ksi = ks.iterator(); ksi.hasNext();){
			String key = ksi.next();
			Database db = openDbs.get(key);
			try {
			  db.close();
			} catch (DatabaseException ex){
				Logging.warning("failed closing db: "+key,ex);
			}
			openDbs.remove(key);
		}
		ks = openEnvs.keySet();
		for(Iterator<String> ksi = ks.iterator(); ksi.hasNext();){
			String key = ksi.next();
			Environment env = openEnvs.get(key);
            try {
			  env.close();
            } catch (DatabaseException ev){
            	Logging.warning("failed closing env: "+key,ev);
            }
			openEnvs.remove(key);
		}
		Set<Cursor> cs = openCursors.keySet();
		for(Iterator<Cursor> csi = cs.iterator(); csi.hasNext();){
			Cursor cursor = csi.next();
			try {
			  cursor.close();
			} catch (DatabaseException ec){
				Logging.warning("failed close cursor: ",ec);
			}
			openCursors.remove(cursor);
		}
	}
	
// Check everything is closed at finalize time	
	
    public void finalize(){
    	// Should close All dbs at shutdown time
    	closeAll();
    }
	public EntryBinding getObjectBinding(Class myClass){
		EntryBinding dataBinding = null;
		try {
			 EnvironmentConfig envConfig = new EnvironmentConfig();
			 envConfig.setTransactional(true);
	         envConfig.setAllowCreate(true);
	         envConfig.setReadOnly(false);
	         envConfig.setCacheSize(1000000);
	         File configlives = new File(ChatConfig.dbmBase+
	        		 File.separator+ChatConfig.classConfig);
             if (!configlives.exists()){
            	 configlives.mkdirs();
             }
             Environment myDbEnv = new Environment(configlives,envConfig);
             // make dir

		    // Open the database that you will use to store your data
		    DatabaseConfig myDbConfig = new DatabaseConfig();
		    myDbConfig.setAllowCreate(true);
		    myDbConfig.setSortedDuplicates(true);
		    // The db used to store class information does not require duplicates
		    // support.
		    myDbConfig.setSortedDuplicates(false);
		    Database myClassDb = myDbEnv.openDatabase(null, "classDb",
		                                              myDbConfig);
		    // Instantiate the class catalog
		    StoredClassCatalog classCatalog = getCatalog(myDbEnv);
		    // Create the binding
		    dataBinding = new SerialBinding(classCatalog,myClass);
//		    myClassDb.close();
//		    myDbEnv.close();
		    // Create the DatabaseEntry for the key
		    // Database and environment close omitted for brevity
		} catch (Exception e) {
			Logging.warning(e.toString());
		    throw new RuntimeException(e);
		}
        return dataBinding;
	}
    
    
	 
}
