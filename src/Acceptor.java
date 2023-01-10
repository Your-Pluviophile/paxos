import java.io.IOException;
import java.net.Socket;

public class Acceptor extends Thread{
    private final int id;
    private int promiseNum;
    private int acceptNum;
    private int acceptValue;

    private Server socket;

    private boolean ifEnd = false;
    public boolean resLate = false;
    public int solution = 0;

    public Acceptor(int id) {
        this.id = id;
        socket = new Server(Common.IP, Common.serverPort + id);

        // initialize
        promiseNum = 0;
        acceptNum = 0;
        acceptValue = 0;
    }

    public int getAcceptNum() {
        return acceptNum;
    }

    public int getAcceptValue() {
        return acceptValue;
    }

    public String promise(int proposalNum){
        int status = -1;
        if(proposalNum > promiseNum){
            promiseNum = proposalNum;
            status = acceptValue;
            System.out.println("    M" + id + " promise " + proposalNum + " OK");
        }else {
            System.out.println("    M" + id + " promise " + proposalNum + " Refuse");
        }

        return Message.promise(promiseNum, status);
    }

    public String acceptted(int proposalNum, int proposalValue){
        if(proposalNum >= promiseNum){
            promiseNum = proposalNum;
            acceptNum = proposalNum;
            acceptValue = proposalValue;
            System.out.println("    M" + id + " acceptted: num - " + acceptNum + " value - " + acceptValue);
            return Message.acceptted(acceptNum, acceptValue);
        }else {
            System.out.println("    M" + id + " acceptted: num - " + proposalNum + " refuse");
            return Message.acceptted(acceptNum, -1);
        }
    }

    public void doSolution(){
        if(solution == 2){
            int random = Common.getRandom(5);
            if(random < 4){
                int one = Common.getRandom(socket.getSize() * 2);
                // no response if one >= client number
                socket.setOneClient(one);
            }

        }else if(solution == 3){
            int random = Common.getRandom(5);
            if(random < 2){
                // no response
                socket.setOneClient(socket.getSize() + 1);
            }
        }
    }


    public void listen() {
        while (!ifEnd){
            doSolution();
            String request = socket.receive();
            if(request != null){
                Message message = new Message(request);
                Socket client = socket.getFirstClient();

                // response late
                if(resLate){
                    try {
                        if(solution == 3){
                            Thread.sleep(2000);
                        }else {
                            Thread.sleep(Common.getRandom(3000));
                        }
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                    }
                }

                new Thread(() -> {
                    // handle message from proposer
                    String response = "";
                    if(message.type.equals(Message.prepareT)){
                        response = promise(message.num);

                    }else if(message.type.equals(Message.acceptT)){
                        response = acceptted(message.num, message.value);
                    }

                    // response to proposer
                    try {
                        socket.send(response, client);
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }

    public void end(){
        ifEnd = true;
    }


    @Override
    public void run(){
        System.out.println("[ Acceptor M" + id + " in ]");

        // start listen
        socket.start();
        listen();

        // end
        socket.end();
        System.out.println("[ Acceptor M" + id + " out ]");
    }
}
