public class Main {

    public static void main(String[] args) {
	    double bigTime=0.0;
	    double endTime=100.0;
        double eventTime=0.0;
        double deltaTime;
        double balkTime=5.0;
        int balkID=0, myBalkCust;
        int numInQueue;
        int numInEvent;
        int totalThruSys=0, totalBalk=0;
        int totalThruLine=0;
        int totalThruFac=0;
        double totalTimeInLine=0.0, totalTimeInSys=0.0, totalTimeInServer=0.0, totalTimeInServer2=0.0,ttil,ttis;
        boolean busy1=false, busy2=false;//shows both servers are empty
        GenericManager<Event> eventQueue=new GenericManager<>();
        GenericManager<Customer> customerQueue=new GenericManager<>();
        Customer served1= new Customer(-9);
        Customer served2= new Customer(-9);
    }
}
