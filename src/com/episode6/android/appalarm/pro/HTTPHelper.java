package com.episode6.android.appalarm.pro;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HTTPHelper {

	public static boolean isNetworkActive(String urlString) {
        int response = -1;
               
        try {
	        URL url = new URL(urlString); 
	        URLConnection conn = url.openConnection();
	                 
	        if (!(conn instanceof HttpURLConnection))   {                   
	            throw new Exception("Not an HTTP connection");
	        }
	        
	        HttpURLConnection httpConn = (HttpURLConnection) conn;
	        httpConn.setAllowUserInteraction(false);
	        httpConn.setInstanceFollowRedirects(true);
	        httpConn.setRequestMethod("GET");
	        httpConn.connect(); 
	
	        response = httpConn.getResponseCode();
	        if (response != -1) {
	            return true;                                 
	        } else {
	          	return false;
	        }
            
        } catch (Exception e) {
        	return false;
        }
	}
	
	public static URLConnection OpenHttpConnection(String urlString) throws Exception {
        int response = -1;
               
        URL url = new URL(urlString); 
        URLConnection conn = url.openConnection();
                 
        if (!(conn instanceof HttpURLConnection))                     
            throw new IOException("Not an HTTP connection");
        
        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect(); 

            response = httpConn.getResponseCode();                 
            if (response == HttpURLConnection.HTTP_OK) {
                return httpConn;                                 
            }                     
        }
        catch (Exception ex)
        {
            throw new IOException("Error connecting");            
        }
        return null;     
	}
	
	
	public static String DownloadText(String URL) throws Exception {
		int BUFFER_SIZE = 2000;
	    InputStream in = null;
//	    try {
	        in = OpenHttpConnection(URL).getInputStream();
//	    } catch (IOException e1) {
//	        // TODO Auto-generated catch block
//	        e1.printStackTrace();
//	        return "";
//	    }
	        
	    InputStreamReader isr = new InputStreamReader(in);
	    int charRead;
	    String str = "";
	    char[] inputBuffer = new char[BUFFER_SIZE];          
//	    try {
	        while ((charRead = isr.read(inputBuffer))>0)
	        {                    
	            //---convert the chars to a String---
	            String readString = String.copyValueOf(inputBuffer, 0, charRead);                    
	            str += readString;
	            inputBuffer = new char[BUFFER_SIZE];
	        }
	        in.close();
//	    } catch (IOException e) {
//	        // TODO Auto-generated catch block
//	        e.printStackTrace();
//	        return "";
//	    }    
	    return str;        
	}
}