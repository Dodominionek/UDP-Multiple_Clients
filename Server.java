package pack.com;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
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

    private static final int PORT = 9090;

    // private static ArrayList<ClientHandler> clients= new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(5);
    private static Map<Integer, Integer> clientMap;

    public static void main(String[] args) throws SocketException {
        clientMap = new HashMap<>();

        boolean res = false;
        try {
            //Time starts here
            Calendar hr = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Timer tt=new Timer();
            tt.schedule(new Time(),0,10000);
           //Time ends here

            DatagramSocket serverSocket = new DatagramSocket(PORT);
            while (true) {

                String sender;
                byte[] b = new byte[1024];
                DatagramPacket serverPocket = new DatagramPacket(b, b.length);
                serverSocket.receive(serverPocket);
                String received = new String(serverPocket.getData());
                String[] t = received.split("[A-Z]{2}\\?|<<[A-Z]{2}\\?|<<");
                //System.out.println(received);
                for(String x:t) System.out.print(x+" ");
                if (clientMap.containsKey(serverPocket.getPort())) {
                    for (Map.Entry<Integer, Integer> x : clientMap.entrySet()) {
                        //serverSocket.send(new DatagramPacket(received.getBytes(), received.length(), serverPocket.getAddress(), x.getKey()));
                    }
                } else {

                    clientMap.put(serverPocket.getPort(), serverPocket.getPort());
                    //TUTAJ WARUNEK DLA MAPY O ROZPOCZECIU GRY
                }


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
                }*/
                if (received.contains("ID")) {
                    String time = "[" + sdf.format(hr.getTime()) + "]:";
                    String toSend = new String(time +"ID sesji: " + id);
                    byte[] bOdp = (toSend + "").getBytes();
                    InetAddress ia = InetAddress.getLocalHost();
                    DatagramPacket message = new DatagramPacket(bOdp, bOdp.length, ia, serverPocket.getPort());
                    serverSocket.send(message);
                }
                if (received.contains("EX")) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
