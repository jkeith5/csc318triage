import java.util.Random;
import java.util.Random.*;

public class Main {

    public static void main(String[] args) {
        /*
        event list with numbers to their eve3nts
        1=arrival
        2=enters line
        3=enters service bay 1
        4=leaves service bay 1
        5=balks
        8=shutdown of sim
        */
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
        double totalTimeInLine=0.0,totalTimeInLine2=0, totalTimeInSys=0.0, totalTimeInServer=0.0, totalTimeInServer2=0.0,ttil,ttis;
        boolean busy1=false, busy2=false;//shows both servers are empty
        GenericManager<Event> eventQueue=new GenericManager<>();
        GenericManager<Customer> customerQueue=new GenericManager<>();
        Customer served1= new Customer(-9);
        Customer served2= new Customer(-9);
        double delTimeServe,timeArrive,delTimeArrive;
        Customer newCust=new Customer(-9);
        Customer workCust= new Customer(-9);
        Event workEvent= new Event(8,100,0);
        eventQueue.addInOrder(workEvent);
        delTimeArrive=timeToEvent(3);//three per hour
        eventTime=bigTime+delTimeArrive;
        System.out.println("First customer arrives at: "+ eventTime);
        workEvent=new Event(1, eventTime,0);
        eventQueue.addInOrder(workEvent);
        workEvent=eventQueue.getVal(0);

        while (workEvent.eventType!=8){
            deltaTime=workEvent.time-bigTime;
            ttil= updateCustomer(customerQueue, deltaTime);
            totalTimeInLine+=ttil;
            totalTimeInLine2+=Math.pow(ttil,2);
            ttis=updateServer(served1,served2,busy1,busy2,deltaTime);
            totalTimeInServer+=ttis;
            totalTimeInServer2+=Math.pow(ttis,2);
            bigTime=workEvent.time;
            //above preps the server and updates customer and statistics
            numInQueue=customerQueue.count;
            switch (workEvent.eventType){
                case 1://arrives at triage
                    if (!busy1&& numInQueue<=0){//server 1 is not busy
                        newCust.timeArrived=bigTime;
                        busy1=true;
                        served1=newCust;
                        setAilment(newCust);
                        if (newCust.ailment==0)//this conditional statement determines service rate based on ailments
                            delTimeServe=timeToEvent(2);
                        else if (newCust.ailment==1)
                            delTimeServe=timeToEvent(4);
                        else
                            delTimeServe=timeToEvent(6);
                        eventTime=delTimeServe+bigTime;
                        workEvent=new Event(4,eventTime,-9);
                        eventQueue.addInOrder(workEvent);
                    }else {
                        balkID++;
                        newCust=new Customer(balkID);
                        newCust.timeArrived=bigTime;
                        setAilment(newCust);
                        //line 142
                        balkTime=generateBalkTime(newCust)+bigTime;
                        System.out.println(balkTime);
                        addHeartInOrder(customerQueue,newCust);
                    }

            }
        }
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

    public static void addHeartInOrder(GenericManager<Customer> custLine,Customer customer){
        //this function adds patients in order of arrival and sorts hearts to the front.
        if (customer.ailment==0){
            for (int i=0; i<custLine.myList.size();i++){
                if (custLine.getVal(i).ailment!=0){
                    custLine.myList.add(i,customer);
                }
            }
        }else{
            custLine.addAtEnd(customer);
        }

    }

    public static void setAilment(Customer customer){//sets the ailment of the arriving customer
        double temp=0;
        temp=Math.random();
        if (temp<.3)
            customer.ailment=0;
        else if (temp<.5)
            customer.ailment=1;
        else
            customer.ailment=2;
        System.out.println(customer.ailment);
    }

    public static double generateBalkTime(Customer customer){
        double balkTime=0;
        double bigX;
        bigX=Math.random();
        while (bigX>.9){
            bigX=Math.random();
            System.out.println("here");
        }
        if (customer.ailment==0){//hearts
            balkTime= .9*bigX+.6;
        }else if (customer.ailment==1){//gastro
            balkTime=.6*bigX+.8;
        }else {//bleeders
            balkTime=1.5*bigX+3;
        }
        return balkTime;
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
