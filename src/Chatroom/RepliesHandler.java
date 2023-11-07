package Chatroom;


import com.sleepycat.bind.EntityBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.je.Environment;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import java.io.File;
import java.nio.charset.*;
import java.util.*;

public class RepliesHandler implements RepliesHandle {

    DbAccess myDab = null;	
    Database myDatabase = null;
    Cursor myCursor = null;
    DatabaseEntry theKey;  // Lookup Pos in Messages
    DatabaseEntry theMessage;
    String nextMessageKey;
    String messageKey; //Lookup Pos in Message
    LockMode lockMode;
    String myBlog;
    final String MESSAGE_TABLE="messages";
    final String LAST_WRITTEN="lastwrite";
    
// Produces a Replies Handler via open Handler
    protected RepliesHandler(){
    	lockMode = LockMode.DEFAULT;
    }

// Cleans up and closes active database items
    public void closeHandler(){
    	if (myCursor!=null){ myDab.closeCursor(myCursor); }
    	if (myDatabase!=null){ myDab.closeDatabase(myDatabase); }
    	if (myDab!=null){ myDab.closeAccess(myBlog,"messages") ; }
    }

 

/* Returns true, if a message at the ID string
// Also moves forward through the iD
// General used
 * Message id = "Something";
 * myHandle=myDbAccess.OpenHandler("myBlog",id);
 * While(myHandle.moreReplies(id)){
 *   port.println(myHandle.getMessage());
 * };
// myHandle.closeHandler();
*/
// Set key presets the key and message for replies
    public void setKey(String messageID){
    	theKey = new DatabaseEntry(messageID.getBytes(myDab.charset));
        theMessage = new DatabaseEntry();
    }
 
// This version by Name Logic    
    public boolean moreReplies(String messageID){
      theKey = new DatabaseEntry(messageID.getBytes(myDab.charset));
      theMessage = new DatabaseEntry(); 
      try {
        OperationStatus retVal = myCursor.getNext(theKey, theMessage, lockMode);
        if (retVal!=OperationStatus.SUCCESS){ return false; }
      } catch (DatabaseException e){
    	return false;
      }
      String messageKey = new String(theKey.getData(),myDab.charset);
      int ml = messageKey.length();
      if (!messageKey.substring(0,ml).equals(messageKey)){
    	  return false; // Different Item ID hex
      }
      return true;
    }
    
    public CRMessage getMessage(String messageID){
        DatabaseEntry lKey = new DatabaseEntry(messageID.getBytes(myDab.charset));
        DatabaseEntry lMessage = new DatabaseEntry(); 
        try {
          myCursor.getSearchKey(lKey,lMessage,lockMode);
          OperationStatus retVal = myCursor.getNext(theKey, theMessage, lockMode);
          if (retVal!=OperationStatus.SUCCESS){ return null; }
        } catch (DatabaseException e){
      	  Logging.warning("Get Messgae failed",e);
      	  return null;
        }
        String messageKey = new String(theKey.getData(),myDab.charset);
        EntryBinding eb = 
    		myDab.getObjectBinding(ChatMessage.class);
    	ChatMessage cm = (ChatMessage)
    		eb.entryToObject(lMessage);
    	CRMessage crm = CRMessage.wrap(cm);
    	return crm;
    }
   
//     
    public boolean moreReplies(){
    	if (theKey==null || theMessage==null){
    	  Logging.warning("key and message must not be null set before moreReplies()");
    	  throw new NullPointerException("key="+theKey+", mess="+theMessage);
    	}	
        try {
            OperationStatus retVal = myCursor.getNext(theKey, theMessage, lockMode);
            if (retVal!=OperationStatus.SUCCESS){ return false; }
          } catch (DatabaseException e){
        	  Logging.warning("More Replies myCursor.getNext failed ",e);
        	  throw new RuntimeException("More Replies myCursor.getNext failed "+e);
          }
          String messageKey = new String(theKey.getData(),myDab.charset);
          int ml = messageKey.length();
          if (!messageKey.substring(0,ml).equals(messageKey)){
        	  return false; // Different Item ID hex
          }
          return true;
    }
    
    
    public CRMessage getMessage(){
    	if (theMessage==null){
    		return null;
    	}
    	EntryBinding eb = 
    		myDab.getObjectBinding(ChatMessage.class);
    	ChatMessage cm = (ChatMessage)
    		eb.entryToObject(theMessage);
    	CRMessage crm = CRMessage.wrap(cm);
    	return crm;
    }
    
    public String getNextMessageId(){
    	if (theKey==null) return "";
    	return new String(theKey.getData(),myDab.charset);
    }
    
    public String firstFreeMessageID(String prevMess){
        return MessNameLogic.inc16(prevMess);
/*    	boolean found = false;
    	String messageID = prevMess;
    	while (found){ 
    		found = moreReplies(messageID);
    		messageID = new String(theKey.getData(),myDab.charset);
    		Logging.fine("Passing: "+messageID);
    	}
    	Logging.finer("incrementing "+prevMess);
    	int level = MessNameLogic.getLevel(prevMess);
    	String ff=MessNameLogic.incrementAt(messageID, level);
    	return ff;
 */   }
  
    
    
// needs to be used once ideally each time a
// new blog or blog message is made.
 
// Write a message in reply to     
//   Except firstfreemessage First    vi
    public boolean writeReplyToGivenPosition(String myPutPos, CRMessage message){
    	ChatMessage cm = message.getDBChatMess();
    	DatabaseEntry key = new DatabaseEntry(myPutPos.getBytes(myDab.charset));
    	cm.setMessageID(myPutPos);
    	EntryBinding obbind = myDab.getObjectBinding(ChatMessage.class);
    	DatabaseEntry cmtar = new DatabaseEntry();
    	obbind.objectToEntry(cm,cmtar);
    	try {
    	  OperationStatus retVal = myCursor.put(key,cmtar);
          myCursor.close();
    	  return retVal == OperationStatus.SUCCESS;
    	} catch (DatabaseException de){
    		Logging.warning("failed writing reply (mycursor.put) ", de);
    		throw new RuntimeException("write Reply myCursor.put failed");
    	}
    }
//  
    public boolean writeReply(String prevID,CRMessage message){
      if (prevID==null || prevID.equals("")){   	  
    	  writeReplyToGivenPosition("@TOP@",message);
      }
      message.setPrevious(prevID);
      message.setMessageID(prevID);      
      int depth=messageTop(message);
      
      if (depth<=0){
    	  String messID = prevID+"00";
    	  message.setNext("@towrite@");
    	  writeReplyToGivenPosition(messID,message);
    	  linkingBack(prevID, messID); 
    	  return true;
      } else {
          String newID = firstFreeMessageID(prevID);
          message.setMessageID(newID);
          boolean done = writeReplyToGivenPosition(newID,message);
          if (!done) return false;
          linkingBack(prevID, newID);
          return true;
      }
    }
    
    
// linking marks the nextID of old item with new item entered
    public boolean linkingBack(String prevID, String newID){
        try {
        	DatabaseEntry thekey = new DatabaseEntry(prevID.getBytes(myDab.charset));
        	DatabaseEntry theData = new DatabaseEntry();
        	Cursor localCursor = myDatabase.openCursor(null,null);
            OperationStatus retVal = localCursor.getSearchKey(theKey, theMessage, lockMode);
            if (retVal!=OperationStatus.SUCCESS){ 
            	Logging.warning("failed linking backwards: "+retVal);
            	localCursor.close();
            	return false;
            } //  Sucessful here
            EntryBinding eb = 
        		myDab.getObjectBinding(ChatMessage.class);
        	    ChatMessage cm = (ChatMessage) eb.entryToObject(theMessage);
        		cm.setNext(newID);
        	    eb.objectToEntry(cm, theData);
        	    localCursor.put(theKey, theData);
        	    localCursor.close();
                return true;
          } catch (DatabaseException e){
        	  Logging.warning("Linking back myCursor.getNext failed ",e);
        	  throw new RuntimeException("More Replies myCursor.getNext failed "+e);
          }
    }
//     links the newID to the item
    public boolean linkingForward(String newId, String nextID){
        try {
        	DatabaseEntry thekey = new DatabaseEntry(nextID.getBytes(myDab.charset));
        	DatabaseEntry theData = new DatabaseEntry();
        	Cursor localCursor = myDatabase.openCursor(null,null);
            OperationStatus retVal = myCursor.getSearchKey(theKey, theMessage, lockMode);
            if (retVal!=OperationStatus.SUCCESS){
            	Logging.warning("Failed linking forward: "+retVal);
            	localCursor.close(); return false; 
            }
              EntryBinding eb = 
        		myDab.getObjectBinding(ChatMessage.class);
        	    ChatMessage cm = (ChatMessage) eb.entryToObject(theMessage);
        		cm.setPrevious(newId);
        	    eb.objectToEntry(cm, theData);
        	    localCursor.put(theKey, theData);
//        	    localCursor.putCurrent(theData);
        	    localCursor.close();
                return true;
          } catch (DatabaseException e){
        	  Logging.warning("Linking back myCursor.getNext failed ",e);
        	  throw new RuntimeException("More Replies myCursor.getNext failed "+e);
          }
             	
    }
    
    public boolean writeChild(String parentID,CRMessage mess){
    	String childId = parentID+"00";
    	int incpos = childId.length();
    	try {
    		DatabaseEntry theData = new DatabaseEntry();
    		Cursor localCursor = myDatabase.openCursor(null,null);
            boolean foundEmpty = false;
            while (!foundEmpty){
    		  DatabaseEntry theKey = new DatabaseEntry(childId.getBytes(myDab.charset));
              OperationStatus retVal = localCursor.getSearchKey(theKey,theData,lockMode);
              foundEmpty = (retVal != OperationStatus.SUCCESS);
              if (!foundEmpty) childId =MessNameLogic.incrementAt(childId, 2);
            }
    	} catch (Exception e){
    	  Logging.warning("Exception find place for child:",e);	
    	}
//        	DatabaseEntry thekey = new DatabaseEntry(oldkey.getBytes(myDab.charset))
    	try {
    		DatabaseEntry theData = new DatabaseEntry();
    		EntryBinding crmBind = myDab.getObjectBinding(ChatMessage.class);
    		DatabaseEntry parentKey = new DatabaseEntry( parentID.getBytes(myDab.charset));
    		DatabaseEntry childKey = new DatabaseEntry(childId.getBytes(myDab.charset)); 
            OperationStatus retVal = myCursor.getSearchKey(parentKey,theData,lockMode);       	
        		ChatMessage cm = (ChatMessage) crmBind.entryToObject(theData);
        	    cm.addChildId(childId);
        	    crmBind.objectToEntry(cm, theData);
        	    myCursor.putNoDupData(parentKey, theData);
            crmBind.objectToEntry(mess,theData);	    
        	myCursor.put(childKey,theMessage);
        	myCursor.close();
            return true;
          } catch (DatabaseException e){
        	  Logging.warning("Linking back myCursor.getNext failed ",e);
        	  throw new RuntimeException("More Replies myCursor.getNext failed "+e);
          }
    }
    
    public boolean writeHead(String topId, CRMessage cm){
    	cm.setPrevious("@TOP@");
    	cm.setMessageID(topId);
    	cm.setNext("@nullid"); // HEAD have childern
    	DatabaseEntry key = new DatabaseEntry(topId.getBytes(myDab.charset));
    	try {
    		EntryBinding obbind = myDab.getObjectBinding(ChatMessage.class);
    		DatabaseEntry data = new DatabaseEntry();
    		obbind.objectToEntry(cm, data);
    		Cursor localCursor = myDatabase.openCursor(null,null);
    		obbind.objectToEntry(cm, data);
    		localCursor.put(key, data);
    		localCursor.close();
    		return true;
    	} catch (DatabaseException de){
    		Logging.warning("Failed writing head: ",de);
    		return false;
    	}
    }
    
    
    public boolean writeHead(String topId){
    	ChatMessage cm = new ChatMessage(topId,"@TOP@","@system@");
    	cm.setPrevious("@TOP@");
    	cm.setNext("@nullid");  // writeable
    	DatabaseEntry key = new DatabaseEntry(topId.getBytes(myDab.charset));
    	EntryBinding obbind = myDab.getObjectBinding(ChatMessage.class);
    	DatabaseEntry cmtar = new DatabaseEntry();
    	obbind.objectToEntry(cm,cmtar);
    	try {
    	  OperationStatus retVal = myCursor.put(key,cmtar);
    	  return retVal == OperationStatus.SUCCESS;
    	} catch (DatabaseException de){
    		Logging.warning("failed writing reply (mycursor.put) ",de);
    		throw new RuntimeException("write head mycursor.put failed");
    	}
    }
// This version inserts at last position!    
    public int messageTop(CRMessage cm){
      int level=0;
      String messageID = cm.getMessageID();
      if (messageID==null || messageID.equals("")
    		  || messageID.equals(CRMessage.nullID)){
    	String url = cm.getURL();
    	if (url!=null && !url.equals("")){
    		messageID = MessNameLogic.getIDfromURL(url);
    	} else {
    		String text = cm.getMessage();
    		if (text==null || text.equals("")){ return level; }
    		messageID = MessNameLogic.getIDfromText(text);
    	}
    	level = 0;
    	cm.setMessageID(messageID);
      } else {
    	  level = (messageID.length()-16)/2;
      }
      Logging.info("The level is: "+level);
      return level;
    }
   
    public boolean delete(String messId){
    	DatabaseEntry key = new DatabaseEntry(messId.getBytes(myDab.charset));
    	EntryBinding obbind = myDab.getObjectBinding(ChatMessage.class);
    	DatabaseEntry lmess = new DatabaseEntry();
    	Cursor localCursor=null;
    	try {
    	  localCursor = myDatabase.openCursor(null,null);
            localCursor.getSearchKey(key,lmess,lockMode);
            OperationStatus retVal = myCursor.getNext(theKey, theMessage, lockMode);
            if (retVal!=OperationStatus.SUCCESS){ myCursor.close(); return false; }
        } catch (DatabaseException e){
        	  Logging.warning("Delete Message failed: ",e);
        	  try {
        	    if (localCursor!=null) localCursor.close();
        	  } catch (DatabaseException f){
        		  Logging.warning("Close Cursor failed: "+f);
        	  }
        	  return false;  
        }
        EntryBinding eb = 
    		myDab.getObjectBinding(ChatMessage.class);
    	ChatMessage cm = (ChatMessage)
    		eb.entryToObject(lmess);
    	CRMessage crm = CRMessage.wrap(cm);
 //  Do the delete   	
        String deleteID = messId;
        String prevId = cm.getPrevious();
        String nextId = cm.getNext();
        HashSet children = cm.children;
        try {
          OperationStatus retVal = localCursor.delete();
          if (retVal != OperationStatus.SUCCESS){
        	Logging.warning("Delete: "+messId+", failed: "+retVal);
        	localCursor.close();
        	return false;
          }
          localCursor.close();
        } catch (DatabaseException e){
        	Logging.warning("Cursor close failed: "+e);
        }
        if (prevId!=null && !prevId.equals("") ){  linkingBack(prevId,nextId); }
        if (nextId!=null && !nextId.equals("") ){ 	linkingForward(prevId,nextId); }
    	return true;
        
    }
    
}

