package com.gvtv.android.cloud.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class CCompatibleUtils {
	public static byte[] longToBytes(long n) {
        byte[] b = new byte[8];
        b[7] = (byte) (n & 0xff);
        b[6] = (byte) (n >> 8  & 0xff);
        b[5] = (byte) (n >> 16 & 0xff);
        b[4] = (byte) (n >> 24 & 0xff);
        b[3] = (byte) (n >> 32 & 0xff);
        b[2] = (byte) (n >> 40 & 0xff);
        b[1] = (byte) (n >> 48 & 0xff);
        b[0] = (byte) (n >> 56 & 0xff);
        return b;
    }
    
    public static void longToBytes( long n, byte[] array, int offset ){
        array[7+offset] = (byte) (n & 0xff);
        array[6+offset] = (byte) (n >> 8 & 0xff);
        array[5+offset] = (byte) (n >> 16 & 0xff);
        array[4+offset] = (byte) (n >> 24 & 0xff);
        array[3+offset] = (byte) (n >> 32 & 0xff);
        array[2+offset] = (byte) (n >> 40 & 0xff);
        array[1+offset] = (byte) (n >> 48 & 0xff);
        array[0+offset] = (byte) (n >> 56 & 0xff);
    }
    
    public static long bytesToLong( byte[] array )
    {
        return ((((long) array[ 0] & 0xff) << 56)
              | (((long) array[ 1] & 0xff) << 48)
              | (((long) array[ 2] & 0xff) << 40)
              | (((long) array[ 3] & 0xff) << 32)
              | (((long) array[ 4] & 0xff) << 24)
              | (((long) array[ 5] & 0xff) << 16)
              | (((long) array[ 6] & 0xff) << 8) 
              | (((long) array[ 7] & 0xff) << 0));        
    }
    
    public static long bytesToLong( byte[] array, int offset )
    {
        return ((((long) array[offset + 0] & 0xff) << 56)
              | (((long) array[offset + 1] & 0xff) << 48)
              | (((long) array[offset + 2] & 0xff) << 40)
              | (((long) array[offset + 3] & 0xff) << 32)
              | (((long) array[offset + 4] & 0xff) << 24)
              | (((long) array[offset + 5] & 0xff) << 16)
              | (((long) array[offset + 6] & 0xff) << 8) 
              | (((long) array[offset + 7] & 0xff) << 0));            
    }
    
    public static byte[] intToBytes(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }
    
    public static byte[] intToBytesLittleEndian(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }
    
    public static void intToBytes( int n, byte[] array, int offset ){
        array[3+offset] = (byte) (n & 0xff);
        array[2+offset] = (byte) (n >> 8 & 0xff);
        array[1+offset] = (byte) (n >> 16 & 0xff);
        array[offset] = (byte) (n >> 24 & 0xff);
    }    

    public static int bytesToInt(byte b[]) {
        return    b[3] & 0xff 
               | (b[2] & 0xff) << 8 
               | (b[1] & 0xff) << 16
               | (b[0] & 0xff) << 24;
    }
    
    public static int bytesToIntLittleEndian(byte b[]) {
        return    b[0] & 0xff 
               | (b[1] & 0xff) << 8 
               | (b[2] & 0xff) << 16
               | (b[3] & 0xff) << 24;
    }

    public static int bytesToInt(byte b[], int offset) {
        return    b[offset+3] & 0xff 
               | (b[offset+2] & 0xff) << 8 
               | (b[offset+1] & 0xff) << 16
               | (b[offset] & 0xff) << 24;
    }

    public static byte[] uintToBytes( long n )
    {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        
        return b;
    }

    public static void uintToBytes( long n, byte[] array, int offset ){
        array[3+offset] = (byte) (n );
        array[2+offset] = (byte) (n >> 8 & 0xff);
        array[1+offset] = (byte) (n >> 16 & 0xff);
        array[offset]   = (byte) (n >> 24 & 0xff);
    }

    public static long bytesToUint(byte[] array) {  
        return ((long) (array[3] & 0xff))  
             | ((long) (array[2] & 0xff)) << 8  
             | ((long) (array[1] & 0xff)) << 16  
             | ((long) (array[0] & 0xff)) << 24;  
    }

    public static long bytesToUint(byte[] array, int offset) {   
        return ((long) (array[offset+3] & 0xff))  
              | ((long) (array[offset+2] & 0xff)) << 8  
             | ((long) (array[offset+1] & 0xff)) << 16  
             | ((long) (array[offset]   & 0xff)) << 24;  
    }

    public static byte[] shortToBytes(short n) {
        byte[] b = new byte[2];
        b[1] = (byte) ( n       & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }
    
    public static void shortToBytes(short n, byte[] array, int offset ) {        
        array[offset+1] = (byte) ( n       & 0xff);
        array[offset] = (byte) ((n >> 8) & 0xff);
    }
    
    public static short bytesToShort(byte[] b){
        return (short)( b[1] & 0xff
                      |(b[0] & 0xff) << 8 ); 
    }    

    public static short bytesToShort(byte[] b, int offset){
        return (short)( b[offset+1] & 0xff
                      |(b[offset]    & 0xff) << 8 ); 
    }

    public static byte[] ushortToBytes(int n) {
        byte[] b = new byte[2];
        b[1] = (byte) ( n       & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }    

    public static void ushortToBytes(int n, byte[] array, int offset ) {
        array[offset+1] = (byte) ( n       & 0xff);
        array[offset] = (byte)   ((n >> 8) & 0xff);
    }

    public static int bytesToUshort(byte b[]) {
        return    b[1] & 0xff 
               | (b[0] & 0xff) << 8;
    }    

    public static int bytesToUshort(byte b[], int offset) {
        return    b[offset+1] & 0xff 
               | (b[offset]   & 0xff) << 8;
    }    

    public static byte[] ubyteToBytes(int n ){
        byte[] b = new byte[1];
        b[0] = (byte) (n & 0xff);
        return b;
    }

    public static void ubyteToBytes( int n, byte[] array, int offset ){
        array[0] = (byte) (n & 0xff);
    }

    public static int bytesToUbyte( byte[] array ){            
        return array[0] & 0xff;
    }        

    public static int bytesToUbyte( byte[] array, int offset ){            
        return array[offset] & 0xff;
    }
    
    public static byte[] charsToBytes (char[] chars) {
    	   Charset cs = Charset.forName ("UTF-8");
    	   CharBuffer cb = CharBuffer.allocate (chars.length);
    	   cb.put (chars);
    	   cb.flip ();
    	   ByteBuffer bb = cs.encode (cb);
    	   return bb.array();
    }
    public static char[] bytesToChars (byte[] bytes) {
    	Charset cs = Charset.forName ("UTF-8");
    	ByteBuffer bb = ByteBuffer.allocate (bytes.length);
    	bb.put (bytes);
    	bb.flip ();
    	CharBuffer cb = cs.decode (bb);
    	return cb.array();
    }
    
    public static String printHexString(byte[] b) { 
    	  String result="";
    	     for (int i = 0; i < b.length; i++) { 
    	       String hex = Integer.toHexString(b[i] & 0xFF); 
    	       if (hex.length() == 1) { 
    	         hex = '0' + hex; 
    	       } 
    	       result=result+hex.toUpperCase(); 
    	     } 
    	     return result;
    	 }

    	public static byte[] hexStringToBytes(String hexString) {    
    	   if (hexString == null || hexString.equals("")) {    
    	       return null;    
    	   }    
    	   hexString = hexString.toUpperCase();    
    	   int length = hexString.length() / 2;    
    	   char[] hexChars = hexString.toCharArray();    
    	   byte[] d = new byte[length];    
    	   for (int i = 0; i < length; i++) {    
    	       int pos = i * 2;    
    	       d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));    
    	   }    
    	   return d;    
    	}  
    	
    	private static byte charToByte(char c) {    
    		   return (byte) "0123456789ABCDEF".indexOf(c);    
    		}   
}
