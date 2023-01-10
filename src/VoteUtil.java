import java.util.Vector;

public class VoteUtil {
    private volatile int proposalNum = 0;
    private Vector<Integer> acceptorIds = new Vector<>();
    public boolean ifEnd = false;

    public synchronized int getProposalNum() {
        proposalNum++;
        return proposalNum;
    }

    public Vector<Integer> getAcceptorIds() {
        return acceptorIds;
    }

    public void setAcceptorIds(int acceptorId) {
        acceptorIds.add(acceptorId);
    }

    public boolean check(Vector<Acceptor> acceptors){
        int[] record = new int[10];
        for (int i = 0; i < 10; i++){
            record[i] = 0;
        }

        for(Acceptor acceptor : acceptors){
            record[acceptor.getAcceptValue()]++;
            if(record[acceptor.getAcceptValue()] > acceptors.size() / 2) {
                System.out.println("\nPaxos get proposal " + acceptor.getAcceptNum() + " with value " + acceptor.getAcceptValue() + " success");
                return true;
            }
        }
        System.out.println("\nPaxos fail");
        return false;
    }
}
