package Chatroom;

// Helpers e.g. used by Random Room user

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.text.*;

public class Utilities {

	static Charset charset = Charset.forName("UTF-8");
	
// Implement later if needed to much to go wrong	
	public static byte[] compressString( String s){
		ByteBuffer buffy = charset.encode(s);
		int len = buffy.position();
		byte buf1[] = new byte[len];
		buffy.get(buf1);
        return buf1;
	}  // Store Java string as UTF-8

	public static String unCompressString(byte b[]){
		char cb[]=new char[b.length];
		for(int i=0;i<b.length;i++){
		    cb[i]= (char) b[i];
		}
		return new String(cb);
	}
	
	
// Want to convert a long number to a 
// readable string, similar to base 64
	public static byte[] compressLong( long l){
		byte[] ret= new byte[11];
		long last =0;
		long q=l;
		ret[10]= (byte) ( (q&0x0f)+48);
		for(int i=0;i<10;i++){
			q = l;
			if (q<0){ q=-q;}
			long x = l & 0xFC00000000000000l;
			last = l &0x3FFFFFFFFFFFFFFFl;
			l = last <<6;
			byte b = (byte) (x>>58);
			if (x>=0){
			  ret[i] = (byte) (b+48);
			} else {
			  ret[i]= (byte) (0x70+b);
			}
//			System.err.println(x+" "+ret[i]);
		}
		return ret;
	}
	
	public static String compressLongString( long l){
		byte b[] = compressLong(l);
		return unCompressString(b);
	}
	
	public static long uncompressStringLong(String s){
		byte b[] = compressString(s);
		return uncompressLong(b);
	}
	
	public static long uncompressLong(byte[] b){
		long ret=0;
		int shift=58;
		for(int i=0;i<10;i++){
			long l = (b[i]-48)&0x3F;
			long k = (l<<shift);
			ret+=k;
//			System.err.println(k+":");
			shift-=6;
		}
		long k = (b[10]-48) &0x0f;
		ret +=k;
		return ret;
	}
	
	public static void main(String[] argv){
		Random r = new Random();
		long l = r.nextLong();
		byte b[] = compressLong(l);
		long ll = uncompressLong(b);
		String s = unCompressString(b);
	    System.out.println(l);
	    System.out.println("'"+s+"'");
	    System.out.println(ll);
	}
	
	public static String escape(String s){
	    StringBuffer sb = new StringBuffer();
	    char ch[] = new char[1];
	    char con[] = new char[3];
	    con[0] = '%';
	    for(int i=0;i<s.length();i++){
	      char c = ch[0] = s.charAt(i);
	      if (c==' ' || c=='%'){
	        int a = c/16;
	        int b = c-a*16;
	        con[1] = Character.forDigit(a,16);
	        con[2] = Character.forDigit(b,16);
	        sb.append(con);
	      } else {
	        sb.append(ch);
	      }
	    }
	    return sb.toString();
	  }
	
	  public static String unescape(String s){
		    StringBuffer sb = new StringBuffer();
		    char ch[] = new char[1];
		    for(int i=0;i<s.length();i++){
		      char c = ch[0] = s.charAt(i);
		      if (i<s.length()-2 && c=='%'){
		        int a = Character.digit(s.charAt(i+1),16);
		        int b = Character.digit(s.charAt(i+2),16);
		        if (a==-1 || b==-1){ sb.append(ch); } else {
		          ch[0] = (char) (a*16+b);
		          sb.append(ch);
		          i+=2;
		        }
		      } else {
		        sb.append(ch);
		      }
		    }
		    return sb.toString();
		  }


	public static HashMap inverseMap(HashMap hm){
	  HashMap inv = new HashMap();
      Set keys = hm.keySet();
	  for(Iterator i =keys.iterator(); i.hasNext();){
		Object ob = i.next();
	  	Object val = hm.get(ob);
	   	inv.put(val, ob);	    
	  }
	  return inv;
	}
	
	public static String DateDo(long time){
		ChatConfig cc = ChatConfig.getConfig();
		DateFormat sdf = new SimpleDateFormat(cc.dateFormat);
		return sdf.format(new Date(time));
	}
	

	
	
}
