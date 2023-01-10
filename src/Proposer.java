import java.io.IOException;
import java.util.Vector;


public class Proposer extends Thread {
    private final int id;
    private int proposalNum;
    private int proposalValue = 0;

    private int promiseNum;
    private volatile int responseOK;
    private volatile int responseCount;
    private Vector<Client> clients = new Vector<>();

    private final VoteUtil util;
    private boolean ifEnd = false;
    public boolean ifOffline = false;

    public Proposer(int id, VoteUtil util) {
        this.id = id;
        this.util = util;
        init();
    }

    private void init() {
        responseOK = 0;
        responseCount = 0;

        promiseNum = 0;
    }

    public void addServer(String ip, int port) {
        clients.add(new Client(id, ip, port));
    }

    public void addServers(String ip, Vector<Integer> ports) {
        for (int i = 0; i < ports.size(); i++) {
            addServer(ip, Common.serverPort + ports.get(i));
        }
    }

    /**
     * broadcast message to more than half of acceptors
     *
     * @param message
     */
    public void broadcast(String message) {
        init();
        for (Client client : clients) {
            new Thread(() -> {
                try {
                    client.connect();
                    client.send(message);
                    Message response = new Message(client.receive());
                    if (response.type.equals(Message.promiseT)) {
                        if (response.status == 1) {
                            responseOK++;
                            if (response.num > promiseNum) {
                                promiseNum = response.num;
                                proposalValue = response.value;
                            }
                        }
                        responseCount++;
                    } else if (response.type.equals(Message.accepttedT)) {
                        if (response.status == 1) {
                            responseOK++;
                        }
                        responseCount++;
                    }

                } catch (IOException e) {
//                    e.printStackTrace();
                    responseCount++;
                }
            }).start();
        }
    }

    public boolean prepare(int pNum) throws InterruptedException {
        System.out.println("Proposer M" + id + " prepare " + pNum);
        proposalNum = pNum;
        broadcast(Message.prepare(proposalNum));

        while (responseCount < clients.size() && responseOK <= clients.size() / 2) {
            Thread.sleep(100);
        }

        return responseOK > clients.size() / 2;
    }


    public boolean accept() throws InterruptedException {
        if (proposalValue == 0) {
            proposalValue = id;
        }

        System.out.println("Proposer M" + id + " send accept: " + "num - " + proposalNum + " value - " + proposalValue);
        broadcast(Message.accept(proposalNum, proposalValue));

        //propose and then go offline
        if (ifOffline && Common.getRandom(2) > 0) {
            end();
            System.out.println("Proposer M" + id + " propose and then go [offline] ");
            return false;
        }

        while (responseCount < clients.size() && responseOK <= clients.size() / 2) {
            Thread.sleep(100);
        }

        return responseOK > clients.size() / 2;
    }

    public synchronized void end() {
        ifEnd = true;
    }

    @Override
    public void run() {
        System.out.println("[ Proposer M" + id + " in ]");
        addServers(Common.IP, util.getAcceptorIds());

        while (!ifEnd) {
            try {
                if (prepare(util.getProposalNum()) && accept()) {
                    System.out.println("Proposer M" + id + " be acceptted: num - " + proposalNum + " value - " + proposalValue);
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("[ Proposer M" + id + " out ]");
    }
}
