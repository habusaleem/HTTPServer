import java.io.*;

public interface HttpProcessor {
	public void processRequest (HttpOutputStream out) throws IOException;
}
