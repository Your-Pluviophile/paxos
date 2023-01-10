import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Client extends Thread{
    private final int id;
    private final String ip;
    private final int port;

    private boolean ifConnect = false;
    private Socket socket = null;

    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    /**
     * Create Client id, and set socket <host, port>
     *
     * @param h, host
     * @param p, port
     */
    public Client(int i, String h, int p) {
        id = i;
        ip = h;
        port = p;
    }

    public int getClientId() {
        return id;
    }

    /**
     * Connect to server
     */
    public void connect(){
        try {
            // create client socket
            socket = new Socket(ip, port);

            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            ifConnect = true;

        } catch (Exception e) {
//            if(Config.outputError) {
                e.printStackTrace();
                System.out.println("Client Create Error!");
//            }
        }
    }

    /**
     * close
     */
    public void close() {
//        if(Config.outputSocket)
        System.out.println("Close M" + id + " client socket");
        try {
            if(!socket.isClosed())
                socket.shutdownOutput();

            inputStream.close();
            outputStream.close();

            socket.close();
            ifConnect = false;
        } catch (IOException e) {
//            if(Config.outputError)
            e.printStackTrace();
        }
    }

    public void send(String message) throws IOException {
        outputStream.write(message.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * return null if error
     * @return
     * @throws IOException
     */
    public String receive() throws IOException {
        byte[] bytes = new byte[1024];

        int read = inputStream.read(bytes);
        if(read <= 0){
            return "null";
        }

        return new String(bytes, 0, read, Charset.defaultCharset());
    }


    /**
     * Run client
     */
    @Override
    public void run() {
        if(!ifConnect){
            System.out.println("Client didn't connect with ant server member");
            connect();
        }
        close();
    }

    public static void main(String[] args) {
//        new SocketClient(Config.IP, Config.ACCEPTOR_PORT).start();
    }
}
