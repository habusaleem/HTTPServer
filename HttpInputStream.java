import java.io.*;
import java.net.*;
import java.util.*;

public class HttpInputStream extends BufferedInputStream {
	protected String method, path, queryString;
	protected float version;
	protected Hashtable headers = new Hashtable();
	protected StringBuffer body = new StringBuffer();
	
	public HttpInputStream (InputStream in) {
		super (in);
	}
	
	public void readRequest () throws IOException {
		String request = readLine();
		if (request == null) {
			throw new HttpException (HTTP.STATUS_BAD_REQUEST, "Null Query");
		}
		StringTokenizer parts = new StringTokenizer (request);
		try {
			parseMethod (parts.nextToken());
			parseRequest (parts.nextToken());
		} catch (NoSuchElementException ex) {
			throw new HttpException (HTTP.STATUS_BAD_REQUEST, request);
		}
		if (parts.hasMoreTokens()) {
			parseVersion (parts.nextToken());
		}
		else {
			version = 0.9f;
		}
		if ((version < 1.0f) && (method == HTTP.METHOD_HEAD)) {
			throw new HttpException (HTTP.STATUS_NOT_ALLOWED, method);
		}
		if (version >= 1.0f) {
			readHeaders();
		}
		if (method == HTTP.METHOD_POST) {
			readBody();
		}
	}
	
	protected void parseMethod (String method) throws HttpException {
		if (method.equalsIgnoreCase(HTTP.METHOD_GET))
			this.method = HTTP.METHOD_GET;
		else if (method.equalsIgnoreCase(HTTP.METHOD_POST))
			this.method = HTTP.METHOD_POST;
		else if (method.equalsIgnoreCase(HTTP.METHOD_HEAD))
			this.method = HTTP.METHOD_HEAD;
		else 
			throw new HttpException (HTTP.STATUS_NOT_IMPLEMENTED, method);
	}
	
	protected void parseRequest (String request) throws HttpException {
		if (!request.startsWith("/"))
			throw new HttpException (HTTP.STATUS_BAD_REQUEST, request);
		int queryIdx = request.indexOf('?');
		if (queryIdx == -1) {
			path = HTTP.canonicalizePath(request);
			queryString = "";
		} else {
			path = HTTP.canonicalizePath(request.substring(0, queryIdx));
			queryString = request.substring(queryIdx + 1);
		}
	}
	
	protected void parseVersion (String verStr) throws HttpException {
		if (!verStr.startsWith("HTTP/"))
			throw new HttpException(HTTP.STATUS_BAD_REQUEST, verStr);
		try {
			version = Float.valueOf(verStr.substring(5)).floatValue();
		} catch (NumberFormatException ex) {
			throw new HttpException (HTTP.STATUS_BAD_REQUEST, verStr);
		}
	}
	
	protected void readHeaders () throws IOException {
		String header;
		while (((header = readLine()) != null) && !header.equals("")) {
			int colonIdx = header.indexOf(':');
			if (colonIdx != -1) {
				String name = header.substring(0, colonIdx);
				String value = header.substring(colonIdx + 1);
				headers.put(name.toLowerCase(), value.trim());
			}
		}
	}
	
	// a method that reads request body
	protected void readBody () throws IOException {
		int contentLength;
		try {
			// get content length from request header
			contentLength = Integer.parseInt(getHeader("content-length"));
		} catch (NumberFormatException e) {
			// if not number, through exception
			throw new HttpException (HTTP.STATUS_BAD_REQUEST, "Invalid content-length");
		}
		int c;
		// read as many chars as request length header
		for (int i = contentLength; i>0; i--) {
			c = read();
			if (c == -1)
				break;
			else
				// append each char to body variable
				this.body.append((char) c);
		}
		
	}
	
	
	public String readLine () throws IOException {
		StringBuffer line = new StringBuffer();
		int c;
		while (((c = read()) != -1) && (c != '\n') && (c != '\r'))
			line.append((char) c);
		if ((c == '\r') && ((c = read()) != '\n') && (c != -1) )
			-- pos;
		return ((c == -1) && (line.length() == 0)) ? null : line.toString();
	}
	
	public String getMethod() {
		return method;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getQueryString() {
		return queryString;
	}
	
	public float getVersion() {
		return version;
	}
	
	public String getHeader (String name) {
		return (String) headers.get(name.toLowerCase());
	}
	
	public Enumeration getHeaderNames() {
		return headers.keys();
	}
	
	// a method that return request body variable
	public String getBody() {
		return this.body.toString();
	}

}
