public class Customer implements Comparable {
    protected double timeInLine;
    protected double timeInSys;
    protected double timeInServer;
    protected double timeArrived;
    protected int ailment;//0=heart, 1=gastro,2=bleeder
    protected int myNum;
    protected int balk;

    public Customer(int id){
        this.myNum = id;
        this.balk=id;
    }

    public double getTimeInLine() {
        return timeInLine;
    }

    public void setTimeInLine(double timeInLine) {
        this.timeInLine = timeInLine;
    }

    public double getTimeInSys() {
        return timeInSys;
    }

    public void setTimeInSys(double timeInSys) {
        this.timeInSys = timeInSys;
    }

    public double getTimeInServer() {
        return timeInServer;
    }

    public void setTimeInServer(double timeInServer) {
        this.timeInServer = timeInServer;
    }

    public int getMyNum() {
        return myNum;
    }

    public void setMyNum(int myNum) {
        this.myNum = myNum;
    }

    public int getBalk() {
        return balk;
    }

    public void setBalk(int balk) {
        this.balk = balk;
    }

    @Override
    public int compareTo(Object o) {
        int temp=0;
        if(((Customer)o).ailment != 0) {
            if (this.timeInLine > ((Customer) o).timeInLine)
                temp = 1;
            else if (this.timeInLine < ((Customer) o).timeInLine)
                temp = -1;
            else
                temp = 0;
        }
        return temp;
    }
}
