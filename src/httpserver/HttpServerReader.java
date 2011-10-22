package httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class HttpServerReader {

    private BufferedReader reader;

    public HttpServerReader(Socket connectionSocket) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
    }

    public String read() throws IOException {
        return reader.readLine();
    }
}
