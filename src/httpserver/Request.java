package httpserver;

import httpserver.OrdinaryHttpService.Method;



public class Request {
    
    private Method method;
    private String uri = "";
    private String protocol = "";

    public Request(Method method, String uri, String protocol) {
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
    }

    public Method getMethod() {
        return method;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getUri() {
        return uri;
    }
}
