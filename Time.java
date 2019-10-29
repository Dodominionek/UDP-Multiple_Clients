package pack.com;

import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Time extends TimerTask {
    private int tr;
    int port = 9091;

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


        try {
            if (tr > 0) {
                System.out.println("Pozostaly czas: " + tr + "s");
                String t = Integer.toString(tr);
                for (Map.Entry<Integer, String> entry : clientMap.entrySet()) {
                    int v = entry.getKey();
                    String temp = "TM?" + time + "<<ID?" + entry.getValue() + "<<OP? Pozostaly_Czas:" + t;
                    InetAddress ia = InetAddress.getLocalHost();
                    byte[] idans = (temp).getBytes();
                    //    serverSocket.send(new DatagramPacket(idans, idans.length, ia, v));
                    DatagramPacket clientPacket = new DatagramPacket(idans, idans.length, ia, v);
                    Server.serverSocket.send(clientPacket);
                }
            }
            if (tr < 0) {
                for (Map.Entry<Integer, String> entry : clientMap.entrySet()) {
                    int v = entry.getKey();
                    String temp = "TM?" + time + "<<ID?" + entry.getValue() + "<<OP? Upłynął_Czas_Rozgrywki";
                    InetAddress ia = InetAddress.getLocalHost();
                    byte[] idans = (temp).getBytes();
                    //    serverSocket.send(new DatagramPacket(idans, idans.length, ia, v));
                    DatagramPacket clientPacket = new DatagramPacket(idans, idans.length, ia, v);
                    Server.serverSocket.send(clientPacket);
                    this.cancel();
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tr = tr - 10;

    }
}
