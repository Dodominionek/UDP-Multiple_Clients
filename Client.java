package pack.com;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

public class Client {
    public static class inputnum implements Runnable {

        private String id, line;
        DatagramSocket clientSocket;

        public inputnum(String id, String line, DatagramSocket clientSocket) {
            this.id = id;
            this.line = line;
            this.clientSocket = clientSocket;
        }

        public void run() {
            Calendar hr = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String time1 = sdf.format(hr.getTime());
            System.out.println(line);
            Scanner input = new Scanner(System.in);
            String task = input.nextLine();
            System.out.println("LICZBA: " + task);
            task = new String("TM?" + time1 + "<<ID?" + id + "<<OD?" + task + "<<");//Wpisuje liczbe
            byte[] b = task.getBytes();
            InetAddress ia = null;
            try {
                ia = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            DatagramPacket clientPacket = new DatagramPacket(b, b.length, ia, SERVER_PORT);
            try {
                clientSocket.send(clientPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // code in the other thread, can reference "var" variable
        }
    }

    private static final int SERVER_PORT = 9090;

    static void confirm(DatagramSocket clientSocket) throws IOException {
        Calendar hr = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(hr.getTime());
        String response = "OP?Wiadomosc_Dostarczona<<TM?" + time + "<<";
        byte[] resp = (response).getBytes();
        InetAddress ia = InetAddress.getLocalHost();
        DatagramPacket clientPacket = new DatagramPacket(resp, resp.length, ia, SERVER_PORT);
        clientSocket.send(clientPacket);
    }

    public static void main(String[] args) throws IOException {
        try {
            String id = "";
            Calendar hr = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String time1 = sdf.format(hr.getTime());

            DatagramSocket clientSocket = new DatagramSocket();
            String msg = "OP?Sesja<<TM?" + time1 + "<<";
            byte[] b = msg.getBytes();
            InetAddress ia = InetAddress.getLocalHost();
            DatagramPacket clientPacket = new DatagramPacket(b, b.length, ia, SERVER_PORT);
            clientSocket.send(clientPacket);//Zapytanie o  sesję
            System.out.println("Wyslano zapytanie o ID sesji");

            b = new byte[1024];
            DatagramPacket serverPocket = new DatagramPacket(b, b.length);
            clientSocket.receive(serverPocket);//Odbiór potwierdzenia
            String received = new String(serverPocket.getData());
            //Client.confirm(clientSocket);//Potwierdzenie
            String[] t = received.split("[A-Z]{2}\\?|<<[A-Z]{2}\\?|<<");
            for (String x : t) {
                System.out.println(x);
                //line=line +x;
            }
            clientSocket.receive(serverPocket);//Odbiera ID sesji
            Client.confirm(clientSocket);//Wysyła potwierdzenie

            received = new String(serverPocket.getData());
            //  t = received.split("[A-Z]{2}\\?|<<[A-Z]{4}\\?|<<");
            //Łopatologiczne wprowadzenie ID
            if (received.charAt(13) != '<') {
                id = id + received.charAt(13);
            }
            if (received.charAt(14) != '<') {
                id = id + received.charAt(14);
            }
            if (received.charAt(15) != '<') {
                id = id + received.charAt(15);
            }
            System.out.println("\nPrzyznane ID sesji:" + id);

            while (true) {
                String line = "";
                clientSocket.receive(serverPocket);//Odbiera info o rozpoczęciu
                received = new String(serverPocket.getData());
                t = received.split("[A-Z]{2}\\?|<<[A-Z]{4}\\?|<<");
                for (String x : t) {
                   // System.out.println(x);
                    if(line.length()==8)
                    {
                        line =line +" ID:";
                    }

                    line = line + x;
                }
                line =line+"     ";
                if (line.contains("Pozostaly_Czas")) {
                    System.out.println(line);
                }
                if (line.contains("Podaj_Liczbe")) {
                    Client.confirm(clientSocket);//Wysyła Potwierdzenie
                    inputnum pl = new inputnum(id,line,clientSocket);
                    Thread tr = new Thread(pl);
                    tr.start();
                }
                if (line.contains("Wiadomosc_Dostarczona")) {
                    System.out.println(line);
                }
                if (line.contains("Gra_Skończona"))
                {
                    System.out.println(line);
                    break;
                }
                if (line.contains("Liczba_Odgadnieta"))
                {
                    System.out.println(line);

                }
                if(line.contains("Liczba_Nie_Odgadnieta"))
                {
                    System.out.println(line);
                }
                //Wysyła liczbę
                    /*
                    clientSocket.receive(serverPocket);//Otrzymuje potwierdzenie dostarczenia
                    received = new String(serverPocket.getData());
                    t = received.split("[A-Z]{2}\\?|<<[A-Z]{4}\\?|<<");
                    for(String x:t)
                    {
                        System.out.println(x);
                        //line=line +x;
                    }
                    clientSocket.receive(serverPocket);//Otrzymuje odpowiedź
                    received = new String(serverPocket.getData());
                    t = received.split("[A-Z]{2}\\?|<<[A-Z]{4}\\?|<<");
                    for(String x:t)
                    {
                        System.out.println(x);
                        //line=line +x;
                    }
                    Client.confirm(clientSocket);//Wysyła Potwierdzenie

                }*/
                /*
                serverPocket = new DatagramPacket(b, b.length);
                clientSocket.receive(serverPocket);
                String str = new String(serverPocket.getData());
                System.out.println(str);
                */


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
