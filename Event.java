package mm1;

public class Event {
    private double time;
    private int type; 

    public Event(double t, int e) {
        this.time = t;
        this.type = e;
    }

    public double getTime() {
        return time;
    }

    public int getType() {
        return type;
    }

}
