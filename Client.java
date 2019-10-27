package pack.com;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Client {
    private static final int SERVER_PORT=9090;
    public int id;


    public static void main(String[] args) throws IOException {
        try {
            Calendar hr = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            String time1 = sdf.format(hr.getTime());

            DatagramSocket clientSocket = new DatagramSocket();
            String msg = "OP?sesja<<TM?"+time1+"<<";
            byte [] b= msg.getBytes();
            InetAddress ia = InetAddress.getLocalHost();
            DatagramPacket clientPacket = new DatagramPacket(b, b.length, ia, SERVER_PORT);
            clientSocket.send(clientPacket);

            while(true) {
                Scanner input = new Scanner(System.in);
                String task = input.nextLine();//Wpisuje polecenie
                //Następuje sprawdzanie polecenia
        /*    if((task.substring(0,2).equals("OD")&&task.substring(0,2).equals("ID")&&task.substring(0,2).equals("OP"))||task.length()<5)
            {
                System.out.println("[Client]:Wrong command: "+task);
            }
            else{
                if(task.charAt(2)!='?'|| !task.substring(task.length()-2,task.length()).equals("<<"))
                {
                    System.out.println("[Client]:Wrong Command: "+task.substring(task.length()-2,task.length()) );
                }
                else
                {*/
                b = (task).getBytes();
                ia = InetAddress.getLocalHost();
                clientPacket = new DatagramPacket(b, b.length, ia, SERVER_PORT);
                clientSocket.send(clientPacket);

                // DatagramSocket serverSocket=new DatagramSocket();
                b = new byte[1024];
                DatagramPacket serverPocket = new DatagramPacket(b, b.length);
                clientSocket.receive(serverPocket);
                String str = new String(serverPocket.getData());
                str="OP?Sesja<<ID?1032<<TM?21:37:55<<";
                String[] t =str.split("[A-Z]{2}\\?|<<[A-Z]{2}\\?|<<");
                for(String x:t) System.out.print(x+" ");

            }
           // temp=task.substring(0,1); //Wyciągam z

        } catch(IOException e){
                e.printStackTrace();
            }
    }
}
