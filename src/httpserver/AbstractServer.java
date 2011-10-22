package httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class AbstractServer implements Server {
    
    protected int serverPort = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected ExecutorService pool = Executors.newCachedThreadPool();
    
    public AbstractServer(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void start() {
        try {
            this.serverSocket = new ServerSocket(serverPort);
        } catch (IOException ex) {
            Logger.getLogger(AbstractServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
     public synchronized void stop(){
        this.isStopped = true;
        try {
            this.pool.shutdown();
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    @Override
    public int getPort() {
        return serverPort;
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger("AbstractServer");
    }
    
    abstract protected Runnable createService(); 
}
