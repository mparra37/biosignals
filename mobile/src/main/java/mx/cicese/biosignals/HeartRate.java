package mx.cicese.biosignals;

/**
 * Created by Mario on 30/01/2018.
 */

public class HeartRate {
    int id;
    long time;
    int ritmo;

    public HeartRate(){

    }

    public HeartRate(int id, long time, int ritmo) {
        this.id = id;
        this.time = time;
        this.ritmo = ritmo;
    }

    @Override
    public String toString() {
        return id + ", " + ritmo + ", " + time;
    }
}
