package acc.com.geolearning_app.util;

public class Server_Status {

    private static Server_Status mInstance= null;

    private static boolean BUSY_SERVER = false;

    protected Server_Status(){}

    public static synchronized Server_Status getInstance(){
        if(null == mInstance){
            mInstance = new Server_Status();
        }
        return mInstance;
    }

    public boolean isBUSY_SERVER() {
        return BUSY_SERVER;
    }

    public void setBUSY_SERVER(boolean BUSY_SERVER) {
        this.BUSY_SERVER = BUSY_SERVER;
    }
}
