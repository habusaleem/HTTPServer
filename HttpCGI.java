import java.io.*;
import java.net.*;
import java.util.*;

public class HttpCGI implements HttpProcessor, Runnable {
	protected HttpInputStream in;
	protected String scriptName, pathInfo;
	protected File cgiScript;
	protected int contentLength;
	
	public HttpCGI (HttpInputStream in, InetAddress ip) throws IOException {
		this.in = in;
		extractScriptName();
		System.out.println(cgiScript.getAbsolutePath());
		if ((!cgiScript.exists()))
			throw new HttpException (HTTP.STATUS_NOT_FOUND, "CGI <TT>" + scriptName +"</TT> not found.");
		if ((!cgiScript.isFile()))
			throw new HttpException (HTTP.STATUS_FORBIDDEN, scriptName);
		if (!cgiScript.getName().startsWith("nph-"))
			throw new HttpException (HTTP.STATUS_NOT_IMPLEMENTED, "Parse-header CGI uniplemented");
		if (in.getMethod() == HTTP.METHOD_POST) {
			try {
				contentLength = Integer.parseInt(in.getHeader("Content-length"));
			} catch (NumberFormatException ex) {
				throw new HttpException (HTTP.STATUS_BAD_REQUEST, "Invalid content-length");
			}
		}
		initEnv (ip);
	}
	
	protected void extractScriptName () {
		String path = in.getPath();
		int pathIdx = path.indexOf('/', 1);
		if ((pathIdx >= 0) && ((pathIdx = path.indexOf('/',  1 + pathIdx)) >= 0)) {
			scriptName = path.substring(0, pathIdx);
			pathInfo = path.substring(pathIdx);
		} else {
			scriptName = path;
		}
		cgiScript = new File (HTTP.SERVER_LOCATION, HTTP.translateFilename(scriptName).substring(1));
	}
	
	protected String[] env;
	
	protected void initEnv (InetAddress ip) {
		Vector environment = (Vector) HTTP.environment.clone();
		environment.addElement("SERVER_PROTOCOL=" + "HTTP/" + in.getVersion());
		environment.addElement("REQUEST_METHOD=" + in.getMethod());
		if (pathInfo != null) {
			environment.addElement("PATH_INFO=" + pathInfo);
			environment.addElement("PATH_TRANSLATED=" + new File (HTTP.HTML_ROOT, HTTP.translateFilename(pathInfo)));
		}
		environment.addElement("SCRIPT_NAME=" + scriptName);
		environment.addElement("QUERY_STRING=" + in.getQueryString());
		environment.addElement("REMOTE_ADDR=" + ip.getHostAddress());
		environment.addElement("REMOTE_HOST=" + ip.getHostName());
		if (in.getMethod() == HTTP.METHOD_POST) {
			environment.addElement("CONTENT_LENGTH=" + contentLength);
			String type = in.getHeader("Content-type");
			if (type != null) {
				environment.addElement("CONTENT_TYPE=" + type);
			}
			
		}
		Enumeration headerNames = in.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = (String) headerNames.nextElement();
			environment.addElement("HTTP_" + name.toUpperCase().replace('-', '_') + "=" + in.getHeader(name));
		}
		env = new String [environment.size()];
		environment.copyInto(env);
	}
	
	protected static Runtime jvm = Runtime.getRuntime();
	protected Process cgi;
	
	public void processRequest (HttpOutputStream out) throws IOException {
		ReThread drain = null;
		try {
			if (in.getMethod() != HTTP.METHOD_POST) {
				cgi = jvm.exec(cgiScript.getPath(), env);
				cgi.getOutputStream().close();
				out.write(cgi.getInputStream());
			} else {
				cgi = jvm.exec(cgiScript.getPath(), env);
				drain = new ReThread (this);
				drain.start();
				out.write(cgi.getInputStream());
			}
		} catch (IOException ex) {
			StringWriter trace = new StringWriter();
			ex.printStackTrace(new PrintWriter (trace, true));
			HttpException httpEx = new HttpException (HTTP.STATUS_INTERNAL_ERROR, "<PRE>" + trace + "</PRE>");
			httpEx.processRequest(out);
		} finally {
			if (drain != null)
				drain.interrupt();
			if (cgi != null)
				cgi.destroy();
		}
	}
	
	public void run() {
		OutputStream out = cgi.getOutputStream();
		try {
			byte[] buffer = new byte[256];
			int len;
			while (!Thread.interrupted() && (contentLength > 0) && ((len = in.read(buffer)) != -1)) {
				out.write(buffer, 0, len);
				contentLength -= len;
			}
			out.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException ignored){
				
			}
		}
	}
}
