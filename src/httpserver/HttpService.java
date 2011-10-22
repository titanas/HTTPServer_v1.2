package httpserver;

public interface HttpService extends Runnable {
    
    public void doGet();
    public void doPost();
    public void doPut();
    public void doHead();
    public void doTrace();
    public void doDelete();
}
