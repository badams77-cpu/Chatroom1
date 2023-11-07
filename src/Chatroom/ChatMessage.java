package Chatroom;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;

public class ChatMessage implements Serializable {


	public static final long serialVersionUID = 0xBD0A0CAB0010000L;
	
	public String messId; //
	public String title; //
	public String text; //
	public String username; // Secondary
    public long date; //
	public String url=""; // Secondary
	public int modup=0;
	public int moddown=0;
	public double matchScore = 0;
	public String prevMess = null;
	public String nextMess = null;
	public HashSet<String> children;
	
	
	
    public ChatMessage(String messId,String text, String username){
    	date = System.currentTimeMillis();
    	this.text = text;
    	this.username = username;
    	this.messId = messId;
    	children = new HashSet<String>();
    }
    	
    public long getDate(){ return date; }
    public String getURL(){ return url; }
    public String getUsername(){ return username; }
    public String getMessId(){ return messId; }
    public int getModsup(){ return modup; }
    public int getModsdown(){ return moddown; }
    public double getScore(){ return matchScore; }
    public double getModValue(){
      if (modup+moddown==0){ return 0.0; }
      return (modup-moddown)/(modup+moddown);
    }
    public String getPrevious(){ return prevMess; }
    public String getNext(){ return nextMess; }
    
    public void addModPoint(){ modup++;}
    public void subModPoint(){ moddown++;}
    public void setMatchScore(double score){
    	this.matchScore = score;
    }
    public void setMessageID(String messId){
    	this.messId=messId;
    }
    public void setText(String text){
    	this.text=text;
    }
    public void setUsername(String username){
    	this.username = username;
    }
    public void setPrevious(String messId){
    	this.prevMess = messId;
    }
    public void setNext(String messId){
    	this.nextMess = messId;
    }
    public void setMessageId(String messId){
    	this.messId = messId;
    }
    public void setDate(long date){
    	this.date = date;
    }
    
    public void setTitle(String title){
    	this.title = title;
    }
    
    public String getTitle(){
    	return title;
    }
    
    public Iterator getChildren(){
    	return children.iterator();
    }
    
    public boolean isChildOf(String messageId){
    	return children.contains(messageId);
    }
    

    public void addChildId(String messageId){
    	children.add( messageId);
    }
}



