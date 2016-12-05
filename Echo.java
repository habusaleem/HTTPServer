import java.io.*;

public class Echo implements HttpClassProcessor {

	protected String message;
	protected String body;
	
	public void initRequest (HttpInputStream in) throws IOException {
		/*if (in.getMethod() != HTTP.METHOD_GET)
			throw new HttpException (HTTP.STATUS_NOT_ALLOWED, "Request method <TT>" + in.getMethod()
			+ "</TT> not allowed.");*/
		// if get method, get query string
		if (in.getMethod() == HTTP.METHOD_GET) {
			message = HTTP.decodeString(in.getQueryString());
		}
		// if post method, get request body
		else if (in.getMethod() == HTTP.METHOD_POST) {
			body = in.getBody();
		}
		else {
			throw new HttpException (HTTP.STATUS_NOT_ALLOWED, "Request method <TT>" + in.getMethod()
			+ "</TT> not allowed.");
		}
	}
	
	public void processRequest (HttpOutputStream out) throws IOException {
		out.setHeader("Content-type", "text/plain");
		if (out.sendHeaders()) {
			out.write("Get query: " + message + "\r\n");
			out.write("Post body: " + body + "\r\n");
		}
	}
}
