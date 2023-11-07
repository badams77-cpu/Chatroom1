package Chatroom;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Handler;
import java.util.logging.Level;

import Chatroom.Logging;

import java.util.logging.*;
// A general file reading configuration class

public class GenConfig {

	public static GenConfig config; // For static access
	
	public static String configFile="/etc/chatroom.conf"; // Default config location
	public static String logFile;
	public static String logLevel="FINE";
	public static String loggerName="Chatroom log";
    private static Logger myLogger;
    private static Handler myHandler;
	
    
	public GenConfig(String configFile){
      GenConfig.configFile = configFile;
      readConfig();
      setLogging();
    }
	

	
	public void setLogging(){
       Level myLevel = Level.ALL;
       myLogger = Logger.getLogger(loggerName);
       myLogger.setUseParentHandlers(false);
       myHandler = new ConsoleHandler();
       Handler myCon = myHandler;
       myLogger.addHandler(myCon);
       try {
    	  myHandler = new java.util.logging.FileHandler(logFile,false);   
       } catch (Exception e){
    	  System.err.println("Can't write Logfile: "+logFile+e);
          System.exit(2);
       }
       try {
    	   myLevel = Level.parse(logLevel);
       } catch (Exception e){ 
    	   System.err.println("Failed setting Level: "+logLevel);
           System.exit(2);  
       }
       myLogger.setLevel(myLevel);
       myHandler.setLevel(myLevel);
       myLogger.removeHandler(myCon);
       myLogger.addHandler(myHandler);
	}
	
	
    public void readConfig(){
		    FileInputStream fis;
		    try {
		      fis = new FileInputStream(configFile);
		    } catch (FileNotFoundException e){
		      Logging.severe("FileNotFound logging the config "+e);
		      System.err.println(e);
		      return;
		    }
		    initalizeConfig(this,fis);
		  }

		  public static Hashtable readParameters(InputStream is){
		    Vector lines = new Vector();
		    Hashtable hash;
		    try {
		      InputStreamReader reader = new InputStreamReader(is);
		      BufferedReader ds = new BufferedReader(reader);
		      String line="Start";
		      while(line != null){
		        line = ds.readLine();
		        if (line == null){continue;}
		        if (line.length()==0){ continue;}
		        if (line.charAt(0)=='#'){ continue;}
		        lines.addElement(line);
		        System.err.println(line);
		      }
		      ds.close();
		      reader.close();
		      is.close();
		    } catch (Exception e){System.err.println(e); Logging.severe("Error reading configuration "+e);}
		    hash = new Hashtable(lines.size());
		    int i = 0;
		    for(Enumeration en=lines.elements();en.hasMoreElements();i++){
		      try {
		        StringTokenizer t = new StringTokenizer( (String) en.nextElement(),"=");
		        if (t.countTokens() >= 2){
			  String name = t.nextToken();
			  String value = t.nextToken();
			  hash.put(name,value);
//			  System.err.println(i+" "+name+" "+value);
//		      } else {
//		          System.err.println(i);
		        }
		      } catch (Exception e){System.err.println(e+" parsing Input"); Logging.severe("Error reading configuration "+e);}
		     }
		     return hash;
		   }

		   public void initalizeConfig(GenConfig config,InputStream is) {

		        Class metaclass = config.getClass();
		        Field[] fields = metaclass.getFields();
		        String param = null;
			Hashtable params = readParameters(is);
			if (params == null){
			  System.err.println("Parameters were not Read");
			  Logging.severe("Configuration parameters not found");
			  return;
		        }
			Hashtable fieldsHash = new Hashtable();
		        for (int i = 0; i < fields.length; i++) {
		            fieldsHash.put( fields[i].getName(),new Integer(i));
		            try {
				param = (String) params.get(fields[i].getName());
//				System.err.println(fields[i].getName() + " "+ param);
				if (param == null){
//		                  System.err.println("Warming Config variable "+param+" not found");
		                    continue;
		                }
		                if ( Modifier.isFinal(fields[i].getModifiers()) ) continue;

		                Class fieldType = fields[i].getType();

		                if (fieldType.equals(boolean.class)) {
		                    fields[i].setBoolean(config, Boolean.valueOf(param).booleanValue());
		                }

		                else if (fieldType.equals(byte.class)) {
		                    fields[i].setByte(config, Byte.valueOf(param).byteValue());
		                }
		                else if (fieldType.equals(char.class)) {
		                    fields[i].setChar(config, param.charAt(0));
		                }

		                else if (fieldType.equals(double.class)) {
		                    fields[i].setDouble(config, Double.valueOf(param).doubleValue());
		                }

		                else if (fieldType.equals(float.class)) {
		                    fields[i].setFloat(config, Float.valueOf(param).floatValue());
		                }

		                else if (fieldType.equals(int.class)) {
		                    fields[i].setInt(config, Integer.valueOf(param).intValue());
		                }

		                else if (fieldType.equals(long.class)) {
		                    fields[i].setLong(config, Long.valueOf(param).longValue());
		                }

		                else if (fieldType.equals(short.class)) {
		                    fields[i].setShort(config, Short.valueOf(param).shortValue());
		                }

		                else if (fieldType.equals(String.class)) {
		                    fields[i].set(config, param);
		                }
		            }
		            catch (Exception e) {
		                System.err.println(e + " while initializing " + fields[i]);
				Logging.severe(e+ " while initialzing "+fields[i]);
		            }
		        }
			for(Enumeration e = params.keys(); e.hasMoreElements();){
		          String s = (String) e.nextElement();
		          if (fieldsHash.get(s) == null){
		            System.err.println("warning Configuration parameter "+s+" is unknown");
			    Logging.warning("Warning Configuration parameter "+s+" is unknown");
		          }
		        }
		    }

		   public void log(int level, String msg){
			    myLogger.logp (getLevel(level),"","", msg);
			  }

			  public void log(int level, String msg, Throwable e){
			    myLogger.log( getLevel(level),msg,e);
			  }
		   
		   private Level getLevel(int levelcode){
//			
			    if (levelcode==Logging.WARNING){ return Level.WARNING; }
			    if (levelcode==Logging.INFO){ return Level.INFO; }
			    if (levelcode==Logging.CONFIG){ return Level.CONFIG; }
			    if (levelcode==Logging.FINE){ return Level.FINE; }
			    if (levelcode==Logging.FINER){ return Level.FINER; }
			    if (levelcode==Logging.FINEST){ return Level.FINEST; }
			    return Level.SEVERE; // Assume the worst for an unknown level
			  }
	
}
