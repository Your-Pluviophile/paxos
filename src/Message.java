public class Message {
    // static type
    public static String prepareT = "prepare";
    public static String promiseT = "promise";
    public static String acceptT = "accept";
    public static String accepttedT = "acceptted";


    // 0-prepare 1-promise 2-accept 3-acceptted
    public String type;
    public int num;
    public int value;
    public int status;

    Message(String mes){
        String[] message = mes.split(":");
        type = message[0];
        num = Integer.parseInt(message[1]);
        value = Integer.parseInt(message[2]);
        if(type.equals(promiseT) || type.equals(accepttedT)){
            status = value >= 0 ? 1 : 0;
        }else {
            status = 1;
        }
    }

    public static String prepare(int proposalNum){
        return prepareT + ":" + proposalNum + ":0";
    }

    public static String promise(int promiseNum, int status){
        return promiseT + ":" + promiseNum + ":" + status;
    }

    public static String accept(int proposalNum, int proposalValue){
        return acceptT + ":" + proposalNum + ":" + proposalValue;
    }

    public static String acceptted(int proposalNum, int proposalValue){
        return accepttedT + ":" + proposalNum + ":" + proposalValue;
    }
}
