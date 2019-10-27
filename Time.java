package pack.com;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Time extends TimerTask {
    private int tr;

    public Time(int tr) {
        this.tr = tr;
    }

    @Override
    public void run() {


        System.out.println("Time remaining: " + tr + " seconds");
        tr = tr - 10;
        if (tr < 0) {
            System.out.println("XD");
        }
    }
}
