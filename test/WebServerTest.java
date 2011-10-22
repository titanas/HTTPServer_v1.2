import org.junit.Test;
import java.net.*;
import java.io.*;
import static org.junit.Assert.*;

/**
 *
 * @author andersb
 */
public class WebServerTest {

  private static final String CRLF = "\r\n";

  @Test
  public void testResponseOK() throws IOException {
    final Socket client = new Socket("localhost", 8888);

    final OutputStream output = client.getOutputStream();
    output.write(("GET /file.html HTTP/1.0" + CRLF + CRLF).getBytes());
    output.flush();

    final BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    final String statusLine = input.readLine();
      System.out.println(statusLine);
    assertEquals("HTTP/1.0 200 OK", statusLine);
    client.close();
  }

  @Test
  public void testResponseNotOK() throws IOException {
    final Socket client = new Socket("localhost", 8888);

    final OutputStream output = client.getOutputStream();
    output.write(("GET /doesNotExist.html HTTP/1.0" + CRLF + CRLF).getBytes());
    output.flush();

    final BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    final String statusLine = input.readLine();
      System.out.println(statusLine);
    assertEquals("HTTP/1.0 404 Not found: /doesNotExist.html", statusLine);
    client.close();
  }

  @Test
  public void testIllegalProtocol() throws IOException {
    final Socket client = new Socket("localhost", 8888);

    final OutputStream output = client.getOutputStream();
    output.write(("GET /doesNotExist.html" + CRLF + CRLF).getBytes());
    output.flush();

    final BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    final String statusLine = input.readLine();
     
    assertEquals("HTTP/1.0 400 Illegal request", statusLine);
    client.close();
  }

  @Test
  public void testMissingProtocol() throws IOException {
    final Socket client = new Socket("localhost", 8888);

    final OutputStream output = client.getOutputStream();
    output.write(("GET /doesNotExist.html HTTP 1.0" + CRLF + CRLF).getBytes());
    output.flush();

    final BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    final String statusLine = input.readLine();
     System.out.println(statusLine);
    assertEquals("HTTP/1.0 400 Illegal protocol: HTTP 1.0", statusLine);
    client.close();
  }

  @Test
  public void testNotImplemented() throws IOException {
    final Socket client = new Socket("localhost", 8888);

    final OutputStream output = client.getOutputStream();
    output.write(("PUT /doesNotExist.html HTTP/1.0" + CRLF + CRLF).getBytes());
    output.flush();

    final BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    final String statusLine = input.readLine();
    assertEquals("HTTP/1.0 501 Not implemented: PUT", statusLine);
    client.close();
  }
  
  @Test
  public void testShutdownSocket() throws IOException {
    final Socket client = new Socket("localhost", 9990);
    
    final OutputStream output = client.getOutputStream();
    output.write(("PUT /doesNotExist.html HTTP/1.0" + CRLF + CRLF).getBytes());
    output.flush();

   
  }
}
