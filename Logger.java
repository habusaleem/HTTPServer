import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class Logger {
	
   // instance data for this Logger
   String host;
   String request;
   int status;
   
   
   public Logger(Socket client){
      this.host = client.getLocalAddress().getHostAddress();
   }
   
   // modifiers
   
   public void setRequest(String request){
      this.request = request;
   }
   public void setStatus(int status){
      this.status = status;
   }
   
   // creates a log directory and log file for today (if didn't exist)
   // then writes the request info according to the Common Log Format
   // -- host ident authuser date request status bytes
   // (see https://en.wikipedia.org/wiki/Common_Log_Format)
   public void addLog(long bytes) throws IOException{
      if(request == null) 
         return; // don't add a log for a null request
      
      try{
         // create log directory and new log file if didn't exist
         String filename = getDate() + ".log";
         File myDir = new File("log");  
         myDir.mkdir();  // only created if didn't exist
         File myFile = new File(myDir, filename);  
         myFile.createNewFile();
      
         // write request information to log file
         FileOutputStream out = new FileOutputStream(myFile, true /* append = true */);
         PrintWriter writer = new PrintWriter(out);
         writer.println(this.host + " - - [" + // ident and authuser are always hyphens since we aren't authenticating
            getDatetime() + "] \"" + this.request + "\" " + this.status + " " + 
            ((bytes == -1) ? "-" : bytes));
         writer.close();
      } 
      catch (IOException e) {
         System.out.println(e);
      }	
   }
   
   // get the date, for log file name
   public static String getDate() {
      return new SimpleDateFormat("yyyy_MM_dd").format(new Date());
   }
   
   // get current datetime in strf format, for log entry
   public static String getDatetime(){
   //  10/Oct/2000:13:55:36 -0700
   // day/month/year:hour:minute:second zone
      return new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z").format(new Date());
   }

}