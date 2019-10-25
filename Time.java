package pack.com;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Time extends TimerTask {
   // Timer tt = new Timer();
    private int  tr=60;
          //  tt.schedule(new TimerTask() {

        @Override
        public void run() {

            //for (Map.Entry<Integer, Integer> x : clientMap.entrySet()) {
            //Calendar hr = Calendar.getInstance();
            //System.out.println(sdf.format(hr.getTime()));
            System.out.println("Time remaining: "+tr+" seconds");
            tr=tr-10;
            if(tr<0)
            {
                System.out.println("XD");
            }
            //}
        }
  // },10000,10000);
}
