package Chatroom;
/**
 * 
 * Superceded
 * 
import java.io.File;
import java.util.Hashtable;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import java.nio.charset.Charset;

public class ChatDataBaseOld implements ChatBase {

	private Charset charset=null;
    private String statsBase = null;
    private Environment env = null;
	
	public ChatDataBaseOld() {
		if (statsBase==null){
			statsBase = getStatsBase();
		}
		
	}
*/
/* READ the config to find dbmBase 
 * also sets the charset for the DB
 */
/*
 * 
	public String getStatsBase(){
		ChatConfig cconf = ChatConfig.getConfig();
		try {
			charset = Charset.forName(ChatConfig.dbmCharset);
		} catch (Exception e){
			Logging.severe( "Charset config failed",e);
		}
		return ChatConfig.dbmBase;
	}

	
	public Database makeDB(String blog,String dbname) throws Exception {
	     EnvironmentConfig envConfig = new EnvironmentConfig();
	     envConfig.setAllowCreate(true);
//	     envConfig.setReadOnly(false);
	     envConfig.setCacheSize(1000000);
	     File home = new File(getStatsBase()+'/'+escape(blog));
	     if (!home.exists()){
	    	 boolean suc = home.mkdirs();
	    	 if (!suc){
	    		 Logging.severe("Can't make database at"+home);
	    		 return null; // Need to send exception really
	    	 } else{
	    		 Logging.info("Create new db at "+home);
	    	 }
	     }
	     
	     env = new Environment(home,envConfig);
	     DatabaseConfig dbConfig = new DatabaseConfig();
	     dbConfig.setAllowCreate(true);
	     dbConfig.setReadOnly(false);
	     Database db = env.openDatabase(null,dbname,dbConfig);
	     return db;
	  }

	  public  Database getDb(String blog,String dbname) throws Exception {
		     EnvironmentConfig envConfig = new EnvironmentConfig();
		     envConfig.setTransactional(true);
		     envConfig.setAllowCreate(true);
//		     envConfig.setReadOnly(true);
		     envConfig.setCacheSize(1000000);
		     File home = new File(getStatsBase()+'/'+escape(blog));
		     if (!home.exists() || home.isDirectory()){
		    	 try {
		    		 home.mkdirs();
		    	 } catch (Exception e){
		    		 Logging.severe("Can't make db at "+home,e);
		    	 }
		     }
		     env = new Environment(home,envConfig);
		     DatabaseConfig dbConfig = new DatabaseConfig();
//		     dbConfig.setReadOnly(true);
		     Database db = env.openDatabase(null,dbname,dbConfig);
		     return db;
		  }	
	  
	  public Hashtable readStringHash(String blog,String name) throws 		
		Exception {
        Database db = getDb(blog,name);
        Hashtable hash = new Hashtable();
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();
        Cursor cursor = db.openCursor(null,null);
        while( cursor.getNext(key,data,LockMode.DEFAULT) == OperationStatus.SUCCESS){
          String keyS = new String(key.getData(),charset);
          String dataS = new String( data.getData(),charset);
          hash.put(keyS,dataS);
        }
        db.close();
        return hash;
       }

	  public void close(){
	    if (env!=null){ env.close(); }
	  }
	  
      public static String escape(String s){
	    StringBuffer sb = new StringBuffer();
	    char ch[] = new char[1];
	    char con[] = new char[3];
	    con[0] = '%';
	    for(int i=0;i<s.length();i++){
	      char c = ch[0] = s.charAt(i);
	      if (c==' ' || c=='%'){
	        int a = c/16;
	        int b = c-a*16;
	        con[1] = Character.forDigit(a,16);
	        con[2] = Character.forDigit(b,16);
	        sb.append(con);
	      } else {
	        sb.append(ch);
	      }
	    }
	    return sb.toString();
	  }

      public static void main(String[] argv){
    	  ChatDataBaseOld cdb = new ChatDataBaseOld();
    	  try {
    	    cdb.makeDB("neutrinos","DB1");
    	  } catch (Exception e){
    		  Logging.severe("Failed making DB: ",e);
    	  }
    	  cdb.close();
    	  
      }

	
}
**/