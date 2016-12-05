import java.io.*;

public interface HttpProcessor {
// returns byte length of file (for logging)
   public long processRequest (HttpOutputStream out) throws IOException;
}
