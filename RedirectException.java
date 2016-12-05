import java.io.*;

public class RedirectException extends HttpException {

	protected String location;
	
	public RedirectException (int code, String location) {
		super (code, "The document has moved <A HREF=\"" + location + "\">here</A>.");
		this.location = location;
	}
	
	public long processRequest (HttpOutputStream out) throws IOException {
		out.setHeader("Location", location);
		super.processRequest(out);
      return 0; // not sure what to return
	}
}
