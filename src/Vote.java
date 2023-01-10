import java.util.Scanner;
import java.util.Vector;

public class Vote {
    private Vector<Acceptor> acceptors = new Vector<>();
    private Vector<Thread> acceptorThreads = new Vector<>();
    private Vector<Proposer> proposers = new Vector<>();
    private Vector<Thread> proposerThreads = new Vector<>();
    private VoteUtil util = new VoteUtil();


    public static void main(String[] args) throws InterruptedException {
        Vote vote = new Vote();
        vote.help();

        Scanner scanner = new Scanner(System.in);
        System.out.print("chosse a task(1 - 3): ");
        int task = scanner.nextInt();
        System.out.print("set acceptor number [1 - 9 default]: ");
        int acceptorNum = scanner.nextInt();
        int proposerNum;

        switch (task){
            case 1:
                System.out.println();
                vote.init(2, acceptorNum);
                vote.task1();
                break;
            case 2:
                System.out.print("set proposer number [1 - 3 default]: ");
                proposerNum = scanner.nextInt();
                System.out.println();

                vote.init(proposerNum, acceptorNum);
                vote.task2();
                break;
            case 3:
                System.out.println();
                vote.init(3, acceptorNum);
                vote.task3();
                break;
            default:
                System.out.println("error task number");
        }
    }

    public void help(){
        System.out.println("=====================================================================================");
        System.out.println("Task1: Two councillors send voting proposals at the sametime");
        System.out.println("     * Always have 2 proposers");
        System.out.println();
        System.out.println("Task2: All M1-M9 have immediate responsesto voting queries");
        System.out.println("     * You can choose proposers number");
        System.out.println();
        System.out.println("Task3: When M1 – M9 have responses to voting queriessuggested by the profiles above, \n" +
                           "       including when M2 or M3 propose and then go offline");
        System.out.println("     * Always have 3 proposers");
        System.out.println("=====================================================================================\n");
    }

    public void init(int proposerNum, int acceptorNum){
        if(proposerNum < 1 || proposerNum > 3){
            proposerNum = 3;
        }
        if(acceptorNum < 1 || acceptorNum > 9){
            acceptorNum = 9;
        }

        for(int i = 1; i <= acceptorNum; i++){
            Acceptor acceptor = new Acceptor(i);
            acceptors.add(acceptor);
            acceptorThreads.add(new Thread(acceptor));
            util.setAcceptorIds(i);
        }

        for(int i = 1; i <= proposerNum; i++){
            Proposer proposer = new Proposer(i, util);
            proposers.add(proposer);
            proposerThreads.add(new Thread(proposer));
        }

    }

    /**
     * Paxos implementation works when two councillors send voting proposals at the sametime
     */
    public void task1() throws InterruptedException {
        System.out.println("Paxos implementation works when two councillors send voting proposals at the sametime");
        System.out.println("* use " + proposers.size() + " proposers " + acceptors.size() + " acceptors");
        System.out.println();
        start();
    }

    /**
     * Paxos implementation works in the case where all M1-M9 have immediate responsesto voting queries
     */
    public void task2() throws InterruptedException {
        System.out.println("Paxos implementation works in the case where all M1-M9 have immediate responsesto voting queries");
        System.out.println("* use " + proposers.size() + " proposers " + acceptors.size() + " acceptors");
        System.out.println();
        start();
    }

    /**
     * Paxos implementation works when M1 – M9 have responses to voting queriessuggested by the profiles above,
     * including when M2 or M3 propose and then go offline
     */
    public void task3() throws InterruptedException {
        System.out.println( "Paxos implementation works when M1 – M9 have responses to voting queriessuggested by the profiles above,\n" +
                            "including when M2 or M3 propose and then go offline");
        System.out.println("* use " + proposers.size() + " proposers " + acceptors.size() + " acceptors");
        System.out.println();

        // set solution for m1 to m9
        // m2: it is unclear whether M2 has read/understood them all
        proposers.get(1).ifOffline = true;

        // m3: sometimes emails completely do not get to M3
        proposers.get(2).ifOffline = true;

        for(int i = 0; i < acceptors.size(); i++){
            if(i == 1){
                acceptors.get(i).solution = 2;
            }else if(i == 2){
                acceptors.get(i).solution = 3;
            }else {
                acceptors.get(i).resLate = true;
            }
        }

        for(Acceptor acceptor : acceptors){
            acceptor.resLate = true;
        }

        start();
    }

    private void start() throws InterruptedException {
        for(int i = 0; i < acceptorThreads.size(); i++){
            acceptorThreads.get(i).start();
        }

        for(int i = 0; i < proposerThreads.size(); i++){
            proposerThreads.get(i).start();
        }

        for(int i = 0; i < proposerThreads.size(); i++){
            proposerThreads.get(i).join();
        }

        for(Acceptor acceptor : acceptors){
            acceptor.end();
        }

        for(Thread thread : acceptorThreads){
            thread.join();
        }

        util.check(acceptors);
        System.exit(0);
    }

}
