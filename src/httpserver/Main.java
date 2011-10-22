package httpserver;

public class Main {
    
    public static void main(String argv[]) {
        HttpServer server = new HttpServer(8888);
        server.start();  
    }
}
