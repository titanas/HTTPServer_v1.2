package httpserver;

import java.nio.file.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import javax.activation.MimetypesFileTypeMap;

public class Response {

    public final static String CRLF = "\r\n";
    private OutputStream writer;

    public Response(Socket connectionSocket) throws IOException {
        this.writer = connectionSocket.getOutputStream();
    }

    public void writeContent(byte[] line) throws IOException {
        writer.write(line);
        writer.flush();
    }
    
    public void writeLine(String line) throws IOException {
        line = line + CRLF;
        writer.write(line.getBytes());
        writer.flush();
    }
    
    public void createDefaultResponseHeader(String message, long contentLength, String contentType) throws IOException, FileNotFoundException {
        
        writeLine(message);
        writeLine("Server: Java HTTP Server 1.0");
        writeLine("Date: " + new Date());
        writeLine("Content-Type: " + contentType);
        writeLine("Content-Length: " + contentLength);
        writeLine("");
    }

    public String getContentType(String path) {
        return MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(path);
    }
}
