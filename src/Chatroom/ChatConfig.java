package Chatroom;

public class ChatConfig extends GenConfig {

	public static String dbmBase = "/usr/webchat/";
    public static String dbmCharset = "ISO-8859-1";
    public static int defaultModPoints = 5;
    public static String defaultAvatar="defAvatar";
    public static String dateFormat = "dd-MM-yy HH:mm";
	public static String classConfig = "java_class_catalog";
	 
    public ChatConfig(String configFile){
      super(configFile);    
	  config = this; 
    }

	public static synchronized ChatConfig getConfig(){
			// Return a singleton config object
		if (config!=null){ return (ChatConfig) config; }
		String oldConfigFile = configFile;
		config =new ChatConfig(configFile);
		// If /etc/config refs another config file read that too
		if (configFile!=oldConfigFile) config.readConfig();
		return (ChatConfig) config;
     }	  
	  
}
