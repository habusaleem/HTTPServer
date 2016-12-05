import java.io.*;

public class HttpException extends IOException implements HttpProcessor {
	protected int code;
	
	public HttpException (int code, String detail) {
		super (detail);
		this.code = code;
	}
     
   public int getCode() {
      return code;
   }
	
	public long processRequest (HttpOutputStream out) throws IOException {
		out.setCode(code);
		out.setHeader("Content-Type", "text/html");
		if (out.sendHeaders()) {
			String msg = HTTP.getCodeMessage(code);
			out.write("<HTML><HEAD><TITLE>" + code + " " + msg + "</TITLE></HEAD>\n" + 
			"<BODY><H1>" + msg + "</H1>\n" + getMessage() + "<P>\n</BODY></HTML>\n");
		}
      return -1; // error
	}

}
