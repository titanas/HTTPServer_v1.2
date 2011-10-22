
package httpserver;

import java.util.logging.Logger;

public interface Server {
    
    public void start();
    public void stop();
    public int getPort();
    public Logger getLogger();
}
