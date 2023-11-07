package Chatroom;


public interface RepliesHandle {

	
//	Errorenous due to java policy, get Handler via DbAccess
//    public  RepliesHandle openHandler(String blog,String messageID);
//
	
// Does this the message have any replies	
	public boolean moreReplies(String messageID); // Does the step
// Are there Any replies to the current Message
	public boolean moreReplies();
// Next readable message repling to the article ID
	public String getNextMessageId(); // Next readable with same
// Return a ChatRoom Item
	public CRMessage getMessage();
// Next writable message ID
	public String firstFreeMessageID(String messageID);
// Handlers must be closed
	void closeHandler();
//  Deletes the given message
	public boolean delete(String messageID);
//  Adds the ChatMessage to the Database
    public boolean writeReply(String replyToID,CRMessage chatMessage);
//	  Adds a head message if none exist, standard messageID == blogName :ItemsIDHex
    public boolean writeHead(String messageID, CRMessage chatMessage);
//   Adds message before another message, 
//    public void writeBefore(String messageID,CRMessage chatMessage);
//   Adds message after another message
// ->*   public void writeAfter(String messageID, CRMessage chatMessage);
    public boolean writeChild(String messageID, CRMessage chatMessage);
    
    
}
