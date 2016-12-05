import java.io.*;
public interface HttpClassProcessor extends HttpProcessor {
   public void initRequest (HttpInputStream in) throws IOException;
}
