package Chatroom;

public class MessNameLogic {

// Each String identifies a possible entry in
// the chat room
// Orignal Items are Identified by a 16 digit hex
// String, the MD5 of the message or item replied to
	
   private static final int minLength = 16;	
	
   public static int getMinLength(){ return minLength; }
   
   public static String getFirstAppend(){ return "00"; }
	
   public static String incrementAt(String s,int pos){
	 int start = s.length()-pos;
	 if (start<2){ return s+getFirstAppend(); }
	 if (s.length()<start+2){ return s+getFirstAppend(); }
	 String part = s.substring(start,s.length());  
	 String keep = s.substring(0,start);
	 int i = (part.charAt(0)-48) &0x3f;
	 int j = (part.charAt(1)-48) &0x3f;
	 int tot = i*64+j+1;
     char rep[] = new char[2];
     rep[0] = (char) (((tot&0x3fc0)>>6)+48);
     rep[1] = (char) ((tot&0x3f)+48);
     return keep+new String(rep);
   }
   
/* public static int  getLevel(String s){
	   int si = s.length()-minLength;
	   if (si<0){
		   Logging.fine("Bad level for message: "+s);
		   return 0;
	   }
	   return si/2;
   }
 */  
	public static String getIDfromURL(String url){
        MD5 md5 = new MD5();		
		byte md[] = md5.MDString8(url);
        return MD5.hexBytes(md);	
	}
	
	public static String getIDfromText(String text){
		MD5 md5 = new MD5();
		byte md[] = md5.MDString8(text);
		return MD5.hexBytes(md);
	}
   
	public static int getLevel(String messageid){
	    int level = messageid.length() -16;
		if (level<0) return 0;
		return level/2;
	}
   
	public static String inc16(String messageid){
		if (messageid.length()<18){
			return messageid+"00";
		} else {
			return incrementAt(messageid,2);
		}
	}
	
}

