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
    public static double timeToEvent(double rate){
        double delTime;
        double bigX;
        bigX=Math.random();
        while (bigX>.9){
            bigX=Math.random();
        }
        delTime=-Math.log(1-bigX)/rate;
        return delTime;
    }

    public static void removeEventBalk(GenericManager<Customer> custLine, int balkID){
        //removes a balking customer from the queue
        int i, numInLine, custBalkID;
        Customer workCust= new Customer(-9);
        numInLine=custLine.count;
        workCust=custLine.getVal(0);
        custBalkID=workCust.getBalk();
        i=0;
        while ((custBalkID!=balkID)&&(i<=(numInLine-1))){
            workCust=custLine.getVal(i);
            custBalkID=workCust.getBalk();
            i++;
        }
        if (i==0) custLine.removeM(0);
        else if (custBalkID==balkID && i>0){
            custLine.removeM(i-1);
        }
        return;
    }

    public static void purgeEvent(GenericManager<Event> eventQueue, int balkID){//removes balking customer event
        int i, numInQueue, eventBalkID;
        Event workEvent= new Event(1,1.0,1);
        numInQueue=eventQueue.count;
        workEvent=eventQueue.getVal(0);
        eventBalkID=workEvent.custID;
        i=0;
        while (eventBalkID!=balkID&&i<=numInQueue-1){
            workEvent=eventQueue.getVal(i);
            eventBalkID=workEvent.custID;
            i++;
        }
        if (eventBalkID==balkID)eventQueue.removeM(i-1);
        return;
    }

    public static double updateCustomer(GenericManager<Customer> custLine, double delTime){
        double lineTime=0;
        int custInLine=custLine.count;
        if (custInLine==0)
            return lineTime;
        else
            return lineTime=delTime*custInLine;
    }

    public static double updateServer(Customer s1, Customer s2, boolean b1, boolean b2, double delTime){
        double serveTime=0;
        if (b1&&b2) return serveTime=2*delTime;
        else if (b1||b2) return delTime;
        return serveTime;
    }
}
