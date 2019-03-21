import java.util.Random;
import java.util.Random.*;

public class Main {
    static int liveBleeders=0, bleedersDead=0, liveGastro=0, gastroDead=0, liveHeart=0, heartDead=0;
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
                    }else {//if server is busy, add to line
                        balkID++;
                        newCust=new Customer(balkID);
                        newCust.timeArrived=bigTime;
                        setAilment(newCust);
                        //line 142
                        balkTime=generateBalkTime(newCust)+bigTime;
                        addHeartInOrder(customerQueue,newCust);
                        workEvent=new Event(5,balkTime,balkID);//generates balk events and addes in order
                        eventQueue.addInOrder(workEvent);
                    }
                    delTimeArrive=timeToEvent(3);
                    eventTime=bigTime+delTimeArrive;
                    workEvent=new Event(1,eventTime,0);
                    eventQueue.addInOrder(workEvent);
                    break;
                case 2:
                    System.out.println("HOPE YA AINT HERE, IF SO BAD STUFF HAPPENED");
                    break;
                case 3:
                    //enter service bay 1
                    numInQueue=customerQueue.count;
                    if (!busy1&&numInQueue>0){//cust in front enters service
                        workCust=customerQueue.getVal(0);
                        myBalkCust=workCust.balk;
                        purgeEvent(eventQueue,myBalkCust);
                        totalThruLine++;
                        customerQueue.removeM(0);
                        busy1=true;
                        served1=workCust;
                        if (workCust.ailment==0) {//rates of service based on ailment
                            delTimeServe = timeToEvent(2);
                        } else if (workCust.ailment==1){
                            delTimeServe= timeToEvent(4);
                        }else{
                            delTimeServe=timeToEvent(6);
                        }
                        eventTime=delTimeServe+bigTime;
                        workEvent=new Event(4,eventTime,-9);
                        eventQueue.addInOrder(workEvent);
                        customerQueue.count--;//decrements counter for amt in line
                        numInQueue--;
                    }
                    break;
                case 4:
                    //leave service bay
                    busy1=false;
                    totalThruSys++;
                    numInQueue=customerQueue.count;
                    if (numInQueue>0){//generates event for someone to enter line
                        workEvent= new Event(3,bigTime+.0001,-9);
                        eventQueue.addInOrder(workEvent);
                    }
                    break;
                case 5:
                    //this handles balk events.

                    myBalkCust=workEvent.custID;
                    totalBalk++;
                    removeEventBalk(customerQueue,myBalkCust);
                    break;
                case 8://shutdown event
                    continue;
                default:
                    System.out.println("bad event type of: "+ workEvent.eventType +"at time: "+workEvent.time);
                    break;
            }

            eventQueue.removeM(0);//deletes processed event, and grabs next event.
            eventQueue.sort();
            workEvent=eventQueue.getVal(0);

        }//end of loop
        System.out.printf("Total Balk: %d\t\t Heart Balk: %d\t\t Bleeder Balk: %d \t\t Gastro Balk: %d\n",totalBalk,heartDead
        ,bleedersDead,gastroDead);
        System.out.println("Total thru Sys: "+totalThruSys);
        System.out.println("Total time in Sys"+ totalTimeInSys);

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
                    custLine.count++;

                }
            }
        }else{
            custLine.myList.add(customer);
        }

    }

    public static void setAilment(Customer customer){//sets the ailment of the arriving customer
        double temp=0;
        temp=Math.random();
        if (temp<.3)
            customer.ailment=0;
        else if (temp<.5)
            customer.ailment=1;
        else if (temp<1)
            customer.ailment=2;
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
        if (workCust.ailment==0)heartDead++;
        else if (workCust.ailment==1)gastroDead++;
        else bleedersDead++;
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
