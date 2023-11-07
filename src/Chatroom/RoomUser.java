package Chatroom;

import java.io.Serializable;
import java.util.Random;
import Chatroom.ChatConfig;
import Distiller.UserEx;
import Distiller.DbBean;

public class RoomUser implements Serializable {

	static final long SerialVersionUID = 0xBD0A0CAA0010000L;
	
	static String locallock = "locallock";
	
	private String username; // Anon or match with Distiler.User
	private int uid; // 
	private String email;
	private String url;
	private int modpoints;
	private String icon = ChatConfig.defaultAvatar;
	private static DbBean dbBean;
	
// This version should match existing login	
	public RoomUser(String username,int uid){
		this.username = username;
		this.uid = uid;
		this.email = "";
		this.url = "";
		this.modpoints = 0;
	}
// This version for anonymous login	
	public RoomUser(){
	  long t= System.currentTimeMillis();
	  username = Chatroom.Utilities.compressLongString(t);	  
	  uid = -((int) (t & 0x7ffffff));  
	}

	
	
// This 	
	public RoomUser(String username){
	  Distiller.UserEx user = null;
	  if (dbBean==null){ dbBean = new DbBean(); }
	  try {
		user = (Distiller.UserEx) dbBean.getBean(
				Distiller.User.class,  
				"SELECT "+user.fields
				+" from users where username=\""
				+username  +"\";");
	  } catch (Exception e){
		  Logging.warning("User lookup failed ",e);
	  }
	  this.username = username;
	  this.uid = user.getUid();
	  this.email = user.getEmail();
	}
	
	public RoomUser(Distiller.User usex){
		this.username = usex.getUsername();
		this.email = usex.getEmail();
		this.uid = usex.getUid();
	}
	
	public Distiller.User getUserBean(String username){
		Distiller.User usex = new Distiller.User();
		usex.setEmail(this.email);
		usex.setUsername(this.username);
		usex.setEmail(email);
		return usex;
	}
	
	public String getUsername(){
		return username;
	}
	
	public void setUsername( String newname){
		this.username = newname;
	}
	
	public int getUid(){
		return uid;
	}
	
	public void setUid(int uid){
		this.uid = uid;
	}
	
	public String getEmail(){
		return email;
	}
	
	public void setEmail(String email){
		this.email = email;
	}
	
	public int getModPoints(){
		return modpoints;
	}
	
	public boolean useModPoint(){
		if (modpoints>0){ modpoints--; return true; }
		return false;
	}

	public void addModPoint(int points){
		modpoints+=points;
	}
	
	
}
