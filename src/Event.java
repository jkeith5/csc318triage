public class Event implements Comparable {
    protected int eventType;
    protected double time;
    protected int custID;


    public Event(int type, double time, int balk){
        this.eventType= type;
        this.time=time;
        if (type==7){//balk event
            this.custID=balk;
        } else {
            this.custID=-9;
        }
    }

    @Override
    public int compareTo(Object o) {
        int temp=0;
        if (this.time>((Event)o).time)
            temp=1;
        else if (this.time<((Event)o).time)
            temp=-1;
        else
            temp=0;
        return temp;
    }
}
