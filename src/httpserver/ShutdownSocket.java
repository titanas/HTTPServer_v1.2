package httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Lukas
 */
public class ShutdownSocket implements Runnable {

    private AbstractServer abstractServer;
    private ServerSocket shutdownSocket; 
    
    /**
     * 
     * @param port - use to create socket to use to shutdown the server
     * @param abstractServer - server using to accepts client requests
     */
    public ShutdownSocket (int port, AbstractServer abstractServer) {
        try {
            this.shutdownSocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(ShutdownSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.abstractServer = abstractServer;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                Socket connectionSocket = shutdownSocket.accept();
                abstractServer.stop();
                ServerLogs.getLoggerInstance().log(ServerLogs.getLevel(), "Server shutdown.");
            } catch (IOException ex) {
                System.out.println("IOException" + ex.toString());
            }
        }
    } 
}
