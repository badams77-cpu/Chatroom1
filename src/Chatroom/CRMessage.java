package Chatroom;

import java.util.Iterator;


// The CR is the public face of the hidden ChatMessage
// Database Item


public class CRMessage {

	protected ChatMessage crPointer;
	protected String blogName;
	protected String messageID;
	
	protected final static String nullID = "@NeedsAnID@";
	
	private CRMessage(){
        crPointer = null;
        blogName = nullID;
        messageID = nullID;
        // Never allow other has to this null Version
	}

	public CRMessage(String text, String username){
		crPointer = new ChatMessage(nullID,text, username);
		crPointer.setDate(System.currentTimeMillis());
	}
	
	public CRMessage(String title,String text,String username){
		crPointer = new ChatMessage(nullID,text,username);
		crPointer.setDate(System.currentTimeMillis());
		crPointer.title = title;
	}
	
// Wraps the database item into a CRM object	
	protected static CRMessage wrap(ChatMessage cm){
		CRMessage crm = new CRMessage();
		crm.crPointer = cm;
		crm.messageID = cm.getMessId();
		return crm;
	}
//  get the hidden message
	protected ChatMessage getDBChatMess(){
		if (crPointer==null){
			return crPointer = new ChatMessage(nullID,nullID,nullID);
		}
		return crPointer;
	}
		
	public String getMessage(){
		if (crPointer==null) throw new MissingMessageException();
		return crPointer.text;
	}
	
	public void setMessage(String message){
		if (crPointer== null) throw new MissingMessageException();
		crPointer.setText(message);
	}
	
	public String getUsername(){
		if (crPointer==null) throw new MissingMessageException();
		return crPointer.getUsername();
	}
	
	public void setUsername(String username){
		if (crPointer==null) throw new MissingMessageException();
		crPointer.setUsername(username);
	}
	
	public long getDate(){
		if (crPointer==null) throw new MissingMessageException();
		return crPointer.getDate();
	}
	
    public void setDate(long date){
		if (crPointer==null) throw new MissingMessageException();
		crPointer.setDate(date);
    }
	
	
	public void modUp(){
		if (crPointer==null) throw new MissingMessageException();
		crPointer.addModPoint();
	}
	
	public void modDown(){
		if (crPointer==null) throw new MissingMessageException();
	    crPointer.subModPoint();
	}
	
	public double getModValue(){
		if (crPointer==null) throw new MissingMessageException();
		return crPointer.getModValue();
	}
	public void setPrevious(String messId){
		if (crPointer==null) throw new MissingMessageException();
		crPointer.prevMess = messId;
	}
    public void setNext(String messId){
		if (crPointer==null) throw new MissingMessageException();
		crPointer.nextMess = messId;
    }
    public String getPrevious(){
    	if (crPointer==null) throw new MissingMessageException();
    	return crPointer.prevMess;
    }
    public String getNext(){
    	if (crPointer==null) throw new MissingMessageException();
    	return crPointer.nextMess;
    }
    	
    
    
    public void setURL(String url){
      if (crPointer==null) throw new MissingMessageException();
      crPointer.url = url;
    }

    public String getURL(){
    	if (crPointer==null) throw new MissingMessageException();
    	if (crPointer.url==null){ return ""; }
    	return crPointer.url;
    }
    
    public String getMessageID(){
    	if (crPointer==null) throw new MissingMessageException();
    	return crPointer.messId;
    }

    public void setMessageID(String id){
    	messageID = id;
    	if (crPointer==null){ throw new MissingMessageException(); }
        crPointer.messId=id;
    }
    
    public Iterator getChildren(){
    	if (crPointer==null){ throw new MissingMessageException(); }
    	return crPointer.children.iterator();
    }
    
    public boolean isChildOf(String messageId){
    	if (crPointer==null){ throw new MissingMessageException(); }
    	return crPointer.children.contains(messageId);    	
    }
    

    public void addChildId(String messageId){
    	if (crPointer==null){ throw new MissingMessageException() ; }
    	crPointer.children.add( messageId);
    }

    public String getTitle(){
    	if (crPointer==null){ throw new MissingMessageException(); }
    	return crPointer.getTitle();		
    }
    
    public void setTitle(String title){
    	if (crPointer==null){ throw new MissingMessageException(); }
        crPointer.setTitle(title);
    }
    	
}

