import java.io.*;

public interface HttpProcessor {
	public long processRequest (HttpOutputStream out) throws IOException;
}
