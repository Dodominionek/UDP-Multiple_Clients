package pack.com;
//Wysyłanie informacji o zakończeniu się rozgrywki w przypadku odgadnięcia liczby (po kolei wysyłając do każdego klienta w mapie)
//Zachowanie informacji o czasie rozgrywki
//No teraz jest tego tekstu za dużo
//Coś tam jest ale lepiej poprawić:
//Odpowiedź tak,nie na przesyłaną przez klienta liczbę wysyłana na jego port  Przykład: OP?nie<<ID?id_klienta<<TM?czas<<


import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

    public static String number = getRandomnumber();
    public static String id = getRandomID();

    public static DatagramSocket serverSocket;

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

    static void confirm(int port, DatagramPacket serverPocket, DatagramSocket serverSocket) throws IOException {
        Calendar hr = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String time = sdf.format(hr.getTime());
        InetAddress ia = InetAddress.getLocalHost();
        String response = "OP?Wiadomosc_Dostarczona<<TM?" + time + "<<\n";
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

        try {
            boolean end = false;
            boolean running = false;
            int s = 0;
            long tStart;
            long tGame;
            long tEnd;
            //Time starts here
            Calendar hr = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            //Time ends here

            serverSocket = new DatagramSocket(PORT);
            while (true) {

                byte[] b = new byte[128];
                DatagramPacket serverPocket = new DatagramPacket(b, b.length);
                serverSocket.receive(serverPocket);//Odbiera zapytanie o sesje
                Server.confirm(serverPocket.getPort(), serverPocket, serverSocket);//Wysyła potwierdzenie zapytania
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
                    serverSocket.send(new DatagramPacket(idans, idans.length, ia, serverPocket.getPort()));//Wysyła ID  sesji
                    serverSocket.receive(serverPocket);//Odbiera potwierdzenie
                    received = new String(serverPocket.getData());
                    t = received.split("[A-Z]{2}\\?|<<[A-Z]{2}\\?|<<");
                    for (String x : t) System.out.print(x + " ");

                }
                if (clientMap.size() == 2) {
                    //Obliczanie maksymalnego czasu rozgrywki

                    if (end == true) {
                        //   running = false;
                        //tt.cancel();
                        // System.out.println("Game Ends");
                    }
                }
                if (clientMap.size() >= 2) {
                    String yn = "";
                    if (s == 0) {
                        System.out.println("\nRozpocząć rozgrywkę(y/n)? Liczba graczy:" + clientMap.size());//Czy rozpocząć rozgrywkę
                        Scanner input = new Scanner(System.in);
                        yn = input.nextLine();
                    }
                    if (yn.charAt(0) == 'y') {
                        s++;
                        tStart=System.currentTimeMillis()%100000;
                        if(s==1)
                        {
                            int mt = 0;
                            for (Map.Entry<Integer, String> entry : clientMap.entrySet()) {
                                String v = entry.getValue();
                                int result = Integer.parseInt(v);
                                mt = mt + result;
                            }
                            mt = (mt * 99) % 100 + 30;   // [(id. sesji 1 + id. sesji 2) * 99] % 100 + 30
                            System.out.println("Wyznaczono czas rozgrywki: " + mt);
                            Timer tt = new Timer();
                            if (running == false) {
                                tt.schedule(new Time(mt), 0, 10000);
                            }
                        }
                        while (end!=true) {
                            running = true;
                            System.out.println("GAME ON");
                            for (Map.Entry<Integer, String> entry : clientMap.entrySet()) {
                                System.out.println(s);
                                s++;
                                hr = Calendar.getInstance();
                                sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                int v = entry.getKey();
                                String time = sdf.format(hr.getTime());
                                String toSend = new String("OP?Podaj_Liczbe<<TM? " + time + "<<");
                                byte[] bOdp = (toSend).getBytes();
                                InetAddress ia = InetAddress.getLocalHost();
                                DatagramPacket message = new DatagramPacket(bOdp, bOdp.length, ia, v);//Wysyła info o rozpoczęciu
                                serverSocket.send(message);
                                serverSocket.receive(serverPocket);
                            }
                            for (int i = 0; i < clientMap.size(); i++) {
                                serverSocket.receive(serverPocket);
                                Server.confirm(serverPocket.getPort(), serverPocket, serverSocket);
                                received = new String(serverPocket.getData());
                                System.out.println("\nGuess: " + received);
                                String guess = "";

                                for (int j = 2; j < received.length(); j++) {
                                    if (received.substring(j - 2, j).contains("OD")) {
                                        if (!received.substring(j + 1, j + 2).equals("<")) {
                                            guess = guess + received.charAt(j + 1);
                                        }
                                        if (!received.substring(j + 2, j + 3).equals("<")) {
                                            guess = guess + received.charAt(j + 2);
                                        }
                                        if (!received.substring(j + 3, j + 4).equals("<")) {
                                            guess = guess + received.charAt(j + 3);
                                        }
                                        System.out.println(guess);
                                        break;
                                    }
                                }
                                if (guess.equals(number)) {
                                    tEnd=System.currentTimeMillis()%100000;
                                    tGame=Math.abs(tStart-tEnd);
                                    String tOfGame=new String("Czas rozgrywki: "+tGame/1000+" sekund");
                                    hr = Calendar.getInstance();
                                    sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                    String time = sdf.format(hr.getTime());
                                    String id = clientMap.get(serverPocket.getPort());
                                    String temp = "TM?" + time + "<<ID?" + id + "<<OP?Liczba_Odgadnieta<<OD?" + guess + "<<" + "TG?" + tOfGame + "<<" ;
                                    byte[] bOdp = (temp).getBytes();
                                    InetAddress ia = InetAddress.getLocalHost();
                                    DatagramPacket message = new DatagramPacket(bOdp, bOdp.length, ia, serverPocket.getPort());
                                    serverSocket.send(message);
                                    serverSocket.receive(serverPocket);
                                    end = true;
                                    for (Map.Entry<Integer, String> entry : clientMap.entrySet()) {
                                        hr = Calendar.getInstance();
                                        sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                        int v = entry.getKey();
                                        time = sdf.format(hr.getTime());
                                        String toSend = new String("OP?Gra_Skończona<<TM?" + time + "<<");
                                        bOdp = (toSend).getBytes();
                                        ia = InetAddress.getLocalHost();
                                        message = new DatagramPacket(bOdp, bOdp.length, ia, v);//Wysyła info o rozpoczęciu
                                        serverSocket.send(message);
                                        serverSocket.receive(serverPocket); }
                                    break;
                                }
                                if (!guess.equals(number)) {
                                    hr = Calendar.getInstance();
                                    sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                    String time = sdf.format(hr.getTime());
                                    String id = clientMap.get(serverPocket.getPort());
                                    String temp = "TM?" + time + "<<ID?" + id + "<<OP?Liczba_Nie_Odgadnieta<<OD?" + guess + "<<";
                                    byte[] bOdp = (temp).getBytes();
                                    InetAddress ia = InetAddress.getLocalHost();
                                    DatagramPacket message = new DatagramPacket(bOdp, bOdp.length, ia, serverPocket.getPort());
                                    serverSocket.send(message);
                                    serverSocket.receive(serverPocket);
                                }

                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
