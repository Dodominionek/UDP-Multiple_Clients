package pack.com;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

public class Client {
    private static final int SERVER_PORT=9090;
    static void confirm(DatagramSocket clientSocket) throws IOException {
        Calendar hr = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(hr.getTime());
        String response = "OP?Wiadomosc_Dostarczona<<TM?" + time+"<<\n";
        byte[] resp = (response).getBytes();
        InetAddress ia = InetAddress.getLocalHost();
        DatagramPacket clientPacket = new DatagramPacket(resp, resp.length, ia, SERVER_PORT);
        clientSocket.send(clientPacket);
    }

    public static void main(String[] args) throws IOException {
        try {
            String id="";
            Calendar hr = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String time1 = sdf.format(hr.getTime());

            DatagramSocket clientSocket = new DatagramSocket();
            String msg = "OP?Sesja<<TM?"+time1+"<<";
            byte [] b= msg.getBytes();
            InetAddress ia = InetAddress.getLocalHost();
            DatagramPacket clientPacket = new DatagramPacket(b, b.length, ia, SERVER_PORT);
            clientSocket.send(clientPacket);
            System.out.println("Wyslano zapytanie o ID sesji");

            b = new byte[1024];
            DatagramPacket serverPocket = new DatagramPacket(b, b.length);
            clientSocket.receive(serverPocket);
            String received = new String(serverPocket.getData());
            Client.confirm(clientSocket);//Potwierdzenie
            String[] t = received.split("[A-Z]{2}\\?|<<[A-Z]{4}\\?|<<");
            for(String x:t)
            {
                System.out.print(x+" ");
            }


            clientSocket.receive(serverPocket);
            received = new String(serverPocket.getData());
            t = received.split("[A-Z]{2}\\?|<<[A-Z]{4}\\?|<<");
            for(String x:t)
            {
                System.out.print(x+" ");
            }
            id=received.substring(13, 16);
            System.out.println("\nPrzyznane ID sesji:"+id);

            while(true) {
                clientSocket.receive(serverPocket);
                received = new String(serverPocket.getData());
                t = received.split("[A-Z]{2}\\?|<<[A-Z]{4}\\?|<<");
                for(String x:t)
                {
                    System.out.print(x+" ");
                }
                Scanner input = new Scanner(System.in);
                String task = input.nextLine();//Wpisuje liczbe

                b = (task).getBytes();
                ia = InetAddress.getLocalHost();
                clientPacket = new DatagramPacket(b, b.length, ia, SERVER_PORT);
                clientSocket.send(clientPacket);

                b = new byte[1024];
                serverPocket = new DatagramPacket(b, b.length);
                clientSocket.receive(serverPocket);
                String str = new String(serverPocket.getData());
                System.out.println(str);

            }

        } catch(IOException e){
                e.printStackTrace();
            }
    }
}
