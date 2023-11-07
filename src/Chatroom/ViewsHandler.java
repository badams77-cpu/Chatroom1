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


public class ViewsHandler {
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
	    final String LAST_WRITEN="lastwrite";
	    
	
// For viewing Chatroom Entries
	ViewsHandler(String messageboard, String table){
	}
	
	
	
}
