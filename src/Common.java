import java.util.Random;

public class Common {
    public static String IP = "localhost";
    public static int serverPort = 8080;

    public static boolean debug = false;

    public static int getRandom(int n){
        if(n == 0) return 0;
        return new Random().nextInt(n);
    }
}
