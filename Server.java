package pack.com;

import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static String number = getRandomnumber();
    public static String id = getRandomID();

    public static String getRandomnumber() {
        int number;
        String numb;
        number = (int) (20.0 * Math.random());
        numb = Integer.toString(number);
        System.out.println("Wylosowana liczba: " + numb);
        return numb;

    }

    public static String getRandomID() {
        int number;
        String numb;
        number = (int) (1000.0 * Math.random());
        numb = Integer.toString(number);
        System.out.println("Numer sesji: " + numb);
        return numb;
    }
    static void confirm(int port,DatagramPacket serverPocket,DatagramSocket serverSocket ) throws IOException {
        Calendar hr = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(hr.getTime());
        InetAddress ia = InetAddress.getLocalHost();
        String response = "OP?Wiadomosc_Dostarczona<<TM?" + time+"<<\n";
        byte[] resp = (response).getBytes();
        serverSocket.send(new DatagramPacket(resp, resp.length, ia, serverPocket.getPort()));
    }
    private static final int PORT = 9090;

    private static Map<Integer, String> clientMap;

    public static Map<Integer, String> getClientMap() {
        return clientMap;
    }

    public static void main(String[] args) throws SocketException {
        clientMap = new HashMap<>();

        boolean res = false;
        try {
            //Time starts here
            Calendar hr = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            //Time ends here

            DatagramSocket serverSocket = new DatagramSocket(PORT);
            while (true) {

                byte[] b = new byte[2048];
                DatagramPacket serverPocket = new DatagramPacket(b, b.length);
                serverSocket.receive(serverPocket);
                Server.confirm(serverPocket.getPort(),serverPocket,serverSocket);
                String received = new String(serverPocket.getData());
                String[] t = received.split("[A-Z]{2}\\?|<<[A-Z]{2}\\?|<<");
                for (String x : t) System.out.print(x + " ");
                if (clientMap.containsKey(serverPocket.getPort())) {
                    for (Map.Entry<Integer, String> x : clientMap.entrySet()) {
                    }
                } else {

                    String cid = getRandomID();
                    while (clientMap.containsKey(cid)) {
                        cid = getRandomID();
                    }

                    clientMap.put(serverPocket.getPort(), cid);
                    String time = sdf.format(hr.getTime());
                    String temp = "";
                    temp = "OP?Sesja<<ID?" + cid + "<<TM?" + time + "<<";
                    InetAddress ia = InetAddress.getLocalHost();
                    byte[] idans = (temp).getBytes();
                    serverSocket.send(new DatagramPacket(idans, idans.length, ia, serverPocket.getPort()));
                    serverSocket.receive(serverPocket);
                    received = new String(serverPocket.getData());
                    t = received.split("[A-Z]{2}\\?|<<[A-Z]{2}\\?|<<");
                    for (String x : t) System.out.print(x + " ");
                }
                if(clientMap.size() == 2)
                {
                    //Obliczanie maksymalnego czasu rozgrywki
                    int mt=0;
                    for (Map.Entry<Integer, String> entry : clientMap.entrySet()) {
                        String v = entry.getValue();
                        int result = Integer.parseInt(v);
                        mt=mt+result;
                    }
                    mt=(mt*99)%100+30;   // [(id. sesji 1 + id. sesji 2) * 99] % 100 + 30
                    System.out.println("Wyznaczono czas rozgrywki: "+mt);
                    Timer tt = new Timer();
                    tt.schedule(new Time(mt), 0, 10000);
                }
                if (clientMap.size() >= 2) {

                    String time = "[" + sdf.format(hr.getTime()) + "]:";
                    String toSend = new String("OP?Podaj_Liczbe<<TM" + time + "<<");
                    byte[] bOdp = (toSend).getBytes();
                    InetAddress ia = InetAddress.getLocalHost();
                    DatagramPacket message = new DatagramPacket(bOdp, bOdp.length, ia, serverPocket.getPort());
                    serverSocket.send(message);
                }

                /*
                if (received.substring(0, 2).equals("OD")) {
                    int x = 3;
                    String guess = "";
                    while (received.charAt(x) != '<') {
                        guess = guess + received.charAt(x);
                        x++;
                    }
                    if (guess.equals(number)) {
                        String time = "[" + sdf.format(hr.getTime()) + "]:";
                        String toSend = new String(time + "Gratulacje, udalo ci sie odgadnac liczbe!");
                        byte[] bOdp = (time + toSend + "").getBytes();
                        InetAddress ia = InetAddress.getLocalHost();
                        DatagramPacket message = new DatagramPacket(bOdp, bOdp.length, ia, serverPocket.getPort());
                        serverSocket.send(message);
                        res = true;
                    } else {
                        String time = "[" + sdf.format(hr.getTime()) + "]:";
                        String toSend = new String(time + "Niestety, nie udalo ci sie odgadnac liczby");
                        byte[] bOdp = (toSend + "").getBytes();
                        InetAddress ia = InetAddress.getLocalHost();
                        DatagramPacket message = new DatagramPacket(bOdp, bOdp.length, ia, serverPocket.getPort());
                        serverSocket.send(message);
                    }
                }
                /*
                if (received.substring(0, 2).equals("OP")) {
                    String toSend = new String("Dostepne operacje: ");
                    Calendar hr = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String time = "[" + sdf.format(hr.getTime()) + "]:";
                    byte[] bOdp = (toSend + "").getBytes();
                    InetAddress ia = InetAddress.getLocalHost();
                    DatagramPacket message = new DatagramPacket(bOdp, bOdp.length, ia, serverPocket.getPort());
                    serverSocket.send(message);
                    toSend = new String(time + "[OP] lista operacji");
                    bOdp = (toSend + "").getBytes();
                    message = new DatagramPacket(bOdp, bOdp.length, ia, serverPocket.getPort());
                    serverSocket.send(message);
                    toSend = new String(time + "[OD] udzielenie odpowiedzi (klucz?wartosc<<)");
                    bOdp = (toSend + "").getBytes();
                    message = new DatagramPacket(bOdp, bOdp.length, ia, serverPocket.getPort());
                    serverSocket.send(message);
                    toSend = new String(time + "[ID] uzyskanie id sesji");
                    bOdp = (toSend + "").getBytes();
                    message = new DatagramPacket(bOdp, bOdp.length, ia, serverPocket.getPort());
                    serverSocket.send(message);
                    toSend = new String(time + "[EX] opuszczenie sesji");
                    bOdp = (toSend + "").getBytes();
                    message = new DatagramPacket(bOdp, bOdp.length, ia, serverPocket.getPort());
                    serverSocket.send(message);
                }
                if (received.contains("ID")) {
                    String time = "[" + sdf.format(hr.getTime()) + "]:";
                    String toSend = new String(time +"ID sesji: " + id);
                    byte[] bOdp = (toSend + "").getBytes();
                    InetAddress ia = InetAddress.getLocalHost();
                    DatagramPacket message = new DatagramPacket(bOdp, bOdp.length, ia, serverPocket.getPort());
                    serverSocket.send(message);
                }
                if (received.contains("EX")) {
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
   
