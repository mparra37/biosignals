package mx.cicese.biosignals;

/**
 * Created by admin on 12/7/2017.
 */

public class HeartRate {
    int id;
    int ritmo;
    long time;
    double IBI;
    int accuracy;

    public HeartRate(){

    }

    public HeartRate(int id, long time, int ritmo, int accuracy, double IBI) {
        this.id = id;
        this.time = time;
        this.ritmo = ritmo;
        this.accuracy = accuracy;
        this.IBI = IBI;
    }

    public long[] getLongArray(){
        long[] arreglo = new long[4];
        arreglo[0] = id;
        arreglo[1] = ritmo;
        arreglo[2] = time;
        arreglo[3] = accuracy;
        return arreglo;

    }

    @Override
    public String toString() {
        return id + ", " + ritmo + ", " + time + ", " + accuracy + ", " + IBI;
    }
}

