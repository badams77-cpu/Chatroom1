package Chatroom;

   import java.util.*;

// Transist object that tells the views handler
// different of each user, what thread are open or closed

public class OpenViews {

	public HashMap<String,HashMap> sessions;
	
	public HashMap<String,Boolean> openViews;
	
	public static OpenViews masterOpenViews = null;
	private static String myLock = "lockstring";
	
	public OpenViews(){
		sessions = new HashMap<String,HashMap>();
		openViews = new HashMap<String,Boolean>();
	}
	
	public static boolean isOpen(String messageId,String username){
		synchronized(myLock) {
 		  if (masterOpenViews==null){
 			  masterOpenViews = new OpenViews();
 		  }
		}
		HashMap<String,Boolean> userviews =
			masterOpenViews.sessions.get(username);
		if (userviews==null){
			HashMap<String,Boolean> ovs = new HashMap<String,Boolean>();
			masterOpenViews.sessions.put(username,ovs);
			return false;
		} else {
			Boolean isOpen = userviews.get(username);
			if (isOpen==null){ return false;}
			return isOpen.booleanValue();
		}
	}
	
	public static void ocViews(String username,String messageid,boolean what){
		synchronized(myLock) {
	 		  if (masterOpenViews==null){
	 			  masterOpenViews = new OpenViews();
	 		  }
	    }
		HashMap<String,Boolean> userviews = masterOpenViews.sessions.get(username);
		if (userviews == null){
			userviews = new HashMap<String,Boolean>();
			masterOpenViews.sessions.put(username,userviews);
		}
		userviews.put(messageid, new Boolean(what));
	}
	
	public static void closeView(String username, String messageid){
		ocViews(username,messageid,false);
	}

	public static void openView(String username, String messageid){
		ocViews(username,messageid,true);
	}
}
