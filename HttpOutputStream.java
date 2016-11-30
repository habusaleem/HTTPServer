import java.io.*;
import java.net.*;
import java.util.*;

public class HttpOutputStream extends BufferedOutputStream {
	
	protected int code;
	protected boolean sendHeaders, sendBody;
	protected Hashtable headers = new Hashtable();
	
	public HttpOutputStream (OutputStream out, HttpInputStream in) {
		super (out);
		code = HTTP.STATUS_OKAY;
		setHeader ("Server", HTTP.SERVER_INFO);
		setHeader ("Date", new Date().toString());
		sendHeaders = (in.getVersion() >= 1.0);
		sendBody = !HTTP.METHOD_HEAD.equals(in.getMethod());
	}
	
	public void setCode (int code) {
		this.code = code;
	}
	
	public void setHeader (String attr, String value) {
		headers.put(attr, value);
	}
	
	public boolean sendHeaders () throws IOException {
		if (sendHeaders) {
			write ("HTTP/1.0 " + code + " " + HTTP.getCodeMessage(code) + "\r\n");
			Enumeration attrs = headers.keys();
			while (attrs.hasMoreElements()) {
				String attr = (String) attrs.nextElement();
				write (attr + ": " + headers.get(attr) + "\r\n");
			}
			write ('\n');
		}
		return sendBody;
	}
	
	public void write (String msg) throws IOException {
		write (msg.getBytes("latin1"));
	}
	
	public void write (InputStream in) throws IOException {
		int n, length = buf.length;
		while ((n = in.read (buf, count, length - count)) >= 0) {
			if ((count += n) >= length) {
				out.write(buf, count = 0, length);
			}
		}
	}

}
