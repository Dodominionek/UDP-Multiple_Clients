package pack.com;

import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Time extends TimerTask {
    private int tr;
    int PORT = 9091;

    public Time(int tr) {
        this.tr = tr;
    }

    @Override
    public void run() {
        //CZAS
        Calendar hr = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(hr.getTime());

        //POBIERA KLIENTÓW DO WYSŁANIA CZASU (MOZLIWE ŻE OD RUSZENIA ZEGARA KTOS DOLACZYŁ)
        Map<Integer, String> clientMap = Server.getClientMap();
        System.out.println("Time remaining: " + tr + " seconds");

        try {
            tr = tr - 10;
            DatagramSocket serverSocket = new DatagramSocket(PORT);
            if (tr < 0) {
                String t = Integer.toString(tr);
                for (Map.Entry<Integer, String> entry : clientMap.entrySet()) {
                    int v = entry.getKey();
                    String temp = "TM?" + time + "<<ID?" + entry.getValue() + "<<OP?Pozostaly_Czas" + t;
                    InetAddress ia = InetAddress.getLocalHost();
                    byte[] idans = (temp).getBytes();
                    serverSocket.send(new DatagramPacket(idans, idans.length, ia, v));
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
