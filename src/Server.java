import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Server
 * * for acceptor
 **/
public class Server extends Thread{
    private final String ip;
    private final int port;
    private ServerSocket serverSocket = null;
    private Queue<Socket> sockets = new LinkedList<>();

    private boolean ifEnd = false;

    public Server(String h, int p) {
        ip = h;
        port = p;
        try {
            // create server
            serverSocket = new ServerSocket(port, 10, InetAddress.getByName(ip));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void send(String message, Socket client) throws IOException {
        OutputStream outputStream = client.getOutputStream();
        outputStream.write(message.getBytes(StandardCharsets.UTF_8));
    }

    public synchronized String receive()  {
        try {
            if(sockets.size() == 0){
                return null;
            }
            InputStream inputStream = sockets.element().getInputStream();

            byte[] bytes = new byte[1024];
            int read = inputStream.read(bytes);
            return new String(bytes, 0, read, Charset.defaultCharset());
        }catch (Exception e){
//            if(Config.outputError)
            e.printStackTrace();
            return null;
        }
    }

    public int getSize(){
        return sockets.size();
    }

    public Socket getFirstClient(){
        return sockets.poll();
    }

    // no response if one >= client number
    public void setOneClient(int n){
        int size = sockets.size();
        for(int i = 0; i < size; i++){
            if(i == n){
                sockets.offer(sockets.poll());
            }else {
                sockets.poll();
            }
        }
    }




    /**
     * Listen for all clients
     */
    public void listen(){
        ifEnd = false;
        while (!ifEnd) {
            try {
                Socket socket = serverSocket.accept();
                sockets.offer(socket);
            } catch (Exception e) {
//                if(Config.outputError) {
                e.printStackTrace();
//                }
            }
        }
    }

    public void end(){
        ifEnd = false;
    }


    @Override
    public void run(){
        listen();
    }

    public static void main(String[] args) {
        new Server(Common.IP, Common.serverPort).listen();
    }

}
