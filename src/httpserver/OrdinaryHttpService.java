package httpserver;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

/**
 * Class handling responses and requests for single connection
 * @author      Lukas, Sanjeev, Justas
 * @version     1.0                
 * @since       2011-10-07
 */
public class OrdinaryHttpService extends HttpServiceAdapter {

    /**
     * Enumeration for methods
     */
    public static enum Method {

        /**
         * GET method
         */
        GET,
        /**
         * POST method
         */
        POST,
        /**
         * HEAD method
         */
        HEAD
    };

    private Socket connectionSocket;
    private HttpServerReader reader = null;
    private Request request = null;
    private Response response = null;

    /**
     * @param connectionSocket
     */
    public OrdinaryHttpService(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    /**
     * Connection socket getter
     * @return connection socket
     */
    public Socket getConnectionSocket() {
        return connectionSocket;
    }

    /**
     * Connection socket setter
     * @param connectionSocket a socket for client connection
     */
    public void setConnectionSocket(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run() {

        try {
            reader = new HttpServerReader(connectionSocket);
            String clientRequest = reader.read();

            response = new Response(connectionSocket);

            ServerLogs.getLoggerInstance().log(Level.INFO, "Request from client (" + getClientInfo() + ")");
            
            ServerLogs.getLoggerInstance().log(Level.INFO, "Client message: " + clientRequest);

            request = createRequest(clientRequest);
                       
            switch (request.getMethod()) {
                case GET: {
                    doGet();
                    break;
                }
                case POST: {
                    doPost();
                    break;
                }
                case HEAD: {
                    doHead();
                    break;
                }
            }
            
            ServerLogs.getLoggerInstance().log(Level.INFO, "Response to client (" + getClientInfo() + ")");
            closeConnection();
        } catch (Exception e) {
            ServerLogs.getLoggerInstance().log(Level.WARNING, e.toString());
        }
    }

    /**
     * Builds a client request from request string
     * @param clientSentence
     * @return an instance of request
     */
    public Request createRequest(String clientSentence) {

        Method method = null;
        String uri = "";
        String protocol = "";

        String[] lines = clientSentence.split(" ", 3);


        try {
            method = checkMethod(lines[0]);
            if (method == null) {
                response.writeLine("HTTP/1.0 501 Not implemented: " + lines[0]);
                closeConnection();
                ServerLogs.getLoggerInstance().log(Level.INFO,
                        "Response to client (" + getClientInfo() + ")");
            }

            if (lines.length < 3) {
                response.writeLine("HTTP/1.0 400 Illegal request");
                closeConnection();
                ServerLogs.getLoggerInstance().log(Level.INFO,
                        "Response to client (" + getClientInfo() + ")");
            }

            protocol = lines[2];

            if (!checkProtocol(protocol)) {
                response.writeLine("HTTP/1.0 400 Illegal protocol: " + protocol);
                closeConnection();
                ServerLogs.getLoggerInstance().log(Level.INFO,
                        "Response to client (" + getClientInfo() + ")");
            }

            uri = URLDecoder.decode(lines[1], "utf-8");

            if (isIndexFile(uri)) {
                uri = "/" + getDefaultFileName();
            }
        } catch (IOException e) {
            ServerLogs.getLoggerInstance().severe(e.getMessage());
        }
        return new Request(method, uri, protocol);
    }

    /**
     * Checks if file exists in the root catalog
     * @param uri file path
     * @return true, if file exists
     */
    public boolean checkFile(String uri) {
        File f = new File(PropertiesReader.getPropertiesReader().getProperty("ROOT_CATALOG") + uri);
        return (f.exists() && f.isFile());
    }

    /**
     * Checks if directory exists in the root catalog
     * @param uri directory path
     * @return true, if directory exists
     */
    public boolean ckeckDirectory(String uri) {
        File f = new File(PropertiesReader.getPropertiesReader().getProperty("ROOT_CATALOG") + uri);
        if (f.exists() && f.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if file is an index file
     * @param uri file path of the file
     * @return true, if file is index
     */
    public boolean isIndexFile(String uri) {
        return (uri.equalsIgnoreCase("/"));
    }

    /**
     * Checks if protocol is in correct format
     * @param protocol protocol string
     * @return true, if protocol is in correct format
     */
    public boolean checkProtocol(String protocol) {
        if (protocol.equalsIgnoreCase("HTTP/1.0") || protocol.equalsIgnoreCase("HTTP/1.1")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if method is in correct format
     * @param method a string with the method name
     * @return true, if method is in correct format
     */
    public Method checkMethod(String method) {
        for (Method m : Method.values()) {
            if (m.name().equalsIgnoreCase(method)) {
                return m;
            }
        }
        return null;
    }

    /**
     * closes client connection
     */
    public void closeConnection() {
        try {
            this.connectionSocket.close();
        } catch (IOException ex) {
        }
    }

    /**
     * Gets information about the client form the client socket
     * @return IP address and port number in string format
     */
    public String getClientInfo() {
        return connectionSocket.getLocalSocketAddress().toString();
    }

    /**
     * Gets a file name for index file
     * @return file name
     */
    public String getDefaultFileName() {
        String namesLine = PropertiesReader.getPropertiesReader().getProperty("WELCOME_FILES");
        String[] names = namesLine.split(" ");
        for (String name : names) {
            File file = new File(PropertiesReader.getPropertiesReader().getProperty("ROOT_CATALOG") + "/" + name);
            if (file.exists() && file.isFile()) {
                return name;
            }
        }
        return PropertiesReader.getPropertiesReader().getProperty("FILE_NOT_FOUND");
    }

    /**
     * Creates a list in html format representing files in the catalog
     * @param uri catalog path
     * @return bytes of the html
     */
    public byte[] createFilesListHTML(String uri) {
        String html = "";
        html += "<html><head><title>Files list</title></head>";
                html += "<style type='text/css'>" +
                "A:link {text-decoration: none}" +
                "A:visited {text-decoration: none}" +
                "A:active {text-decoration: none}" +
                "A:hover {text-decoration: underline; color: red;}" +
                "</style>";
        html += "<body>";

        String root = PropertiesReader.getPropertiesReader().getProperty("ROOT_CATALOG");
        
        String[] fileNames = getFileNames(uri);
        if (fileNames.length != 0) {
            html += "<h4>Files list in " + uri + " catalog:</h4>";
            html += "<a href=\"" + uri + "/..\">..</a></br>";
            for (String name : fileNames) {
                //String filePath = "/" + name;
                html += "<a href=\"" + uri + "/" + name + "\">" + name + "</a>";
                html += "</br>";
            }
        } else {
            html += "<h4>No files in " + PropertiesReader.getPropertiesReader().getProperty("ROOT_CATALOG") + uri + "</h4>";
        }

        html += "</body>";
        html += "</html>";
        return html.getBytes();
    }

    /**
     * Gets all file names of in the catalog
     * @param uri catalog path
     * @return a string array of file names
     */
    public String[] getFileNames(String uri) {
        File file = new File(PropertiesReader.getPropertiesReader().getProperty("ROOT_CATALOG") + uri);
        return file.list();
    }

    @Override
    public void doGet() {
        try {
            String uri = request.getUri();

            if (checkFile(uri)) {           // Normally file exist
                String filePath = PropertiesReader.getPropertiesReader().getProperty("ROOT_CATALOG") + uri;

                String message = "HTTP/1.0 200 OK";
                byte[] content = Files.readAllBytes(Paths.get(filePath));
                String contentType = response.getContentType(uri);
                response.createDefaultResponseHeader(message, content.length, contentType);
                response.writeContent(content);

                ServerLogs.getLoggerInstance().log(ServerLogs.getLevel(), "Server message: " + message + " "
                        + "File Path: " + filePath + " "
                        + "Content-Type: " + contentType + " " + "Content-Length: " + content.length);

            } else {                        // File does'n exist
                if (ckeckDirectory(uri)) {          // Directory exist
                    String filePath = PropertiesReader.getPropertiesReader().getProperty("ROOT_CATALOG") + uri;
                    if (uri.endsWith("/"))
                        uri = uri.substring(0, uri.length()-1);

                    String message = "HTTP/1.0 200 OK";
                    byte[] content = createFilesListHTML(uri);
                    String contentType = "text/html";
                    response.createDefaultResponseHeader(message, content.length, contentType);
                    response.writeContent(content);

                    ServerLogs.getLoggerInstance().log(ServerLogs.getLevel(), "Server message: " + message + " "
                            + "File Path: " + filePath + " "
                            + "Content-Type: " + contentType + " " + "Content-Length: " + content.length);

                } else {                    // File not found
                    String filePath = PropertiesReader.getPropertiesReader().getProperty("ROOT_CATALOG") + "/"
                            + PropertiesReader.getPropertiesReader().getProperty("FILE_NOT_FOUND");

                    String message = "HTTP/1.0 404 Not found";
                    byte[] content = Files.readAllBytes(Paths.get(filePath));
                    String contentType = "text/html";
                    response.createDefaultResponseHeader(message, content.length, contentType);
                    response.writeContent(content);

                    ServerLogs.getLoggerInstance().log(ServerLogs.getLevel(), "Server message: " + message + " "
                            + "File Path: " + filePath + " "
                            + "Content-Type: " + contentType + " " + "Content-Length: " + content.length);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void doHead() {
        try {
            String uri = request.getUri();

            if (checkFile(uri)) {
                String filePath = PropertiesReader.getPropertiesReader().getProperty("ROOT_CATALOG") + uri;

                String message = "HTTP/1.0 200 OK";
                byte[] content = Files.readAllBytes(Paths.get(filePath));
                String contentType = response.getContentType(uri);
                response.createDefaultResponseHeader(message, content.length, contentType);

                ServerLogs.getLoggerInstance().log(ServerLogs.getLevel(), "Server message: " + message + " "
                        + "File Path: " + filePath + " "
                        + "Content-Type: " + contentType + " " + "Content-Length: " + content.length);

            } else {
                if (ckeckDirectory(uri)) {
                    String filePath = PropertiesReader.getPropertiesReader().getProperty("ROOT_CATALOG") + uri;

                    String message = "HTTP/1.0 200 OK";
                    byte[] content = createFilesListHTML(uri);
                    String contentType = "text/html";
                    response.createDefaultResponseHeader(message, content.length, contentType);

                    ServerLogs.getLoggerInstance().log(ServerLogs.getLevel(), "Server message: " + message + " "
                            + "File Path: " + filePath + " "
                            + "Content-Type: " + contentType + " " + "Content-Length: " + content.length);

                } else {
                    String filePath = PropertiesReader.getPropertiesReader().getProperty("ROOT_CATALOG") + "/"
                            + PropertiesReader.getPropertiesReader().getProperty("FILE_NOT_FOUND");

                    String message = "HTTP/1.0 404 Not found";
                    byte[] content = Files.readAllBytes(Paths.get(filePath));
                    String contentType = "text/html";
                    response.createDefaultResponseHeader(message, content.length, contentType);

                    ServerLogs.getLoggerInstance().log(ServerLogs.getLevel(), "Server message: " + message + " "
                            + "File Path: " + filePath + " "
                            + "Content-Type: " + contentType + " " + "Content-Length: " + content.length);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
