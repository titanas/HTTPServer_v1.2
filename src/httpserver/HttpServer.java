package httpserver;

import java.io.IOException;
import java.net.Socket;

public class HttpServer extends AbstractServer {

    public HttpServer(int serverPort) {
        super(serverPort);
    }

    @Override
    public void start() {
        super.start();
        
        ServerLogs.getLoggerInstance().log(ServerLogs.getLevel(), "Server started.");
        
        createShutdownService();

        while (!isStopped) {
            System.out.println("Waiting for the clients...");
            Runnable clientService = createService();
            pool.submit(clientService);
        }
        pool.shutdown();
    }

    private void createShutdownService () {
        int port = Integer.parseInt(PropertiesReader.getPropertiesReader().getProperty("SHUTDOWN_PORT")); 
        ShutdownSocket shutdownSocket = new ShutdownSocket(port, this);   
        pool.submit(shutdownSocket);
    }
    
    @Override
    protected Runnable createService() {
        Socket clientSocket = null;
        Runnable runnable = null;
        try {
            clientSocket = serverSocket.accept();
            runnable = new OrdinaryHttpService(clientSocket);
        } catch (IOException e) {
            if(isStopped) {
                System.exit(0);
            }
            System.out.println(e.getMessage());
        }
        return runnable;
    }
}
