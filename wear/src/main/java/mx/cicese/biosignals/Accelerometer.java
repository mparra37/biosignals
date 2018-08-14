package mx.cicese.biosignals;

public class Accelerometer {
    int id;
    double valueX;
    double valueY;
    double valueZ;
    long time;
    int accuracy;

    public Accelerometer(){

    }

    public Accelerometer(int id, double x, double y, double z, long time, int accuracy){
        this.id = id;
        this.valueX = x;
        this.valueY  = y;
        this.valueZ = z;
        this.time = time;
        this.accuracy = accuracy;

    }

    @Override
    public String toString() {
        return id + ", " + valueX + ", " + valueY + ", " + valueZ + ", " + time + ", " + accuracy;
    }

}
