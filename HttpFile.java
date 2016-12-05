import java.io.*;


public class HttpFile implements HttpProcessor {
   protected File file;
	
<<<<<<< HEAD
   public HttpFile (HttpInputStream in) throws IOException {
      if (in.getMethod() == HTTP.METHOD_POST)
         throw new HttpException (HTTP.STATUS_NOT_ALLOWED, "<TT>" + in.getMethod() +
            	" " + in.getPath() + "</TT>");
      file = new File (HTTP.HTML_ROOT, HTTP.translateFilename(in.getPath()));
      if (in.getPath().endsWith("/"))
         file = new File (file, HTTP.DEFAULT_INDEX);
      if (!file.exists())
         throw new HttpException (HTTP.STATUS_NOT_FOUND, "File <TT>" + in.getPath() +
            	"<TT> not found.");
      if (file.isDirectory())
         throw new RedirectException (HTTP.STATUS_MOVED_PERMANENTLY, in.getPath() + "/");
      if (!file.isFile() || !file.canRead())
         throw new HttpException (HTTP.STATUS_FORBIDDEN, in.getPath());
   }
	
   // return size of file processed in bytes
   public long processRequest (HttpOutputStream out) throws IOException {
      out.setHeader("Content-type", HTTP.guessMimeType(file.getName()));
      out.setHeader("Content-length", String.valueOf(file.length()));
      if (out.sendHeaders()) {
         FileInputStream in = new FileInputStream (file);
         out.write(in);
         in.close();
      }
      return file.length(); // return file length
   }
=======
	public HttpFile (HttpInputStream in) throws IOException {
		if (in.getMethod() == HTTP.METHOD_POST)
			throw new HttpException (HTTP.STATUS_NOT_ALLOWED, "<TT>" + in.getMethod() +
					" " + in.getPath() + "</TT>");
		file = new File (HTTP.getHtmlRoot(in.getPath()), HTTP.translateFilename(in.getPath()));
		if (in.getPath().endsWith("/"))
			file = new File (file, HTTP.DEFAULT_INDEX);
		if (!file.exists())
			throw new HttpException (HTTP.STATUS_NOT_FOUND, "File <TT>" + in.getPath() +
					"<TT> not found.");
		if (file.isDirectory())
			throw new RedirectException (HTTP.STATUS_MOVED_PERMANENTLY, in.getPath() + "/");
		if (!file.isFile() || !file.canRead())
			throw new HttpException (HTTP.STATUS_FORBIDDEN, in.getPath());
	}
	
	public void processRequest (HttpOutputStream out) throws IOException {
		out.setHeader("Content-type", HTTP.guessMimeType(file.getName()));
		out.setHeader("Content-length", String.valueOf(file.length()));
		if (out.sendHeaders()) {
			FileInputStream in = new FileInputStream (file);
			out.write(in);
			in.close();
			
		}
	}
>>>>>>> master
}
