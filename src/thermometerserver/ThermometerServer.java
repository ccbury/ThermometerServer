import java.io.*;
import java.net.*;
import java.util.List;
import java.util.StringTokenizer;

public class ThermometerServer {

    private static final int PORT = 1234;
    private static DatagramSocket dgramSocket;
    private static DatagramPacket inPacket, outPacket;
    private static byte[] buffer;
    static double euroUSD = 1.10;
    static double euroGBP = 0.84;
    static double USDEuro = 0.89;
    static double USDCny = 6.94;

    public static void main(String[] args) {
        System.out.println("Opening port...\n\n");
        try {
            dgramSocket = new DatagramSocket(PORT);//Step 1.
        } catch (SocketException e) {
            System.out.println("Unable to attach to port!");
            System.exit(1);
        }

        run();
    }

    private static void run() {
        try {
            String messageIn, messageOut;
            int numMessages = 0;
            do {
                buffer = new byte[256]; //Step 2.
                inPacket = new DatagramPacket(buffer, buffer.length); //Step 3.
                dgramSocket.receive(inPacket); //Step 4.
                InetAddress clientAddress = inPacket.getAddress(); //Step 5.
                int clientPort = inPacket.getPort(); //Step 5. 
                messageIn = new String(inPacket.getData(), 0, inPacket.getLength()); //Step 6.
                StringTokenizer st = new StringTokenizer(messageIn);

                System.out.println("Message received from client: ");
                
                Double temp = 0.0;
                String unit = "";
                Double tempConverted;
                String unitConvertedTo;
                String[] split = messageIn.split("\\s+");
                
                if (split.length == 2){
                    try{
                        temp = Double.parseDouble(split[0]);
                        unit = split[1];
                    } catch (Exception e){
                        temp = 0.0;
                        unit = "na";
                    }
                    System.out.println(temp+" "+unit);
                 }else if (split.length == 1){
                     split = messageIn.split("(?<=\\D)(?=\\d)([^\\d-]+)|(?<=\\d)(?=\\D)");
                     if(split.length == 2){
                    try{
                        temp = Double.parseDouble(split[0]);
                        unit = split[1];
                    } catch (Exception e){
                        temp = 0.0;
                        unit = "na";
                    }
                        System.out.println(temp+" "+unit);
                     }
              
                 }

                
                switch (unit){
                    case "C":
                    case "c":
                        tempConverted = (temp * 1.8) + 32;
                        unitConvertedTo = "F";
                        messageOut = (temp+" "+unit+" converts to "+tempConverted+" "+unitConvertedTo );
                        System.out.println("Converts to "+tempConverted+" "+unitConvertedTo);
                        break;
                    case "F":
                    case "f":
                        tempConverted = (temp-32) * 5/9;
                        unitConvertedTo = "C";
                        messageOut = (temp+" "+unit+" converts to "+tempConverted+" "+unitConvertedTo );
                        System.out.println("Converts to "+tempConverted+" "+unitConvertedTo);
                        break;
                    default:
                        System.out.println("Valid format was not received.");
                        messageOut = ("A valid temperature format was not found in user input....");
                        break;
                }
                
                outPacket = new DatagramPacket(messageOut.getBytes(), messageOut.length(), clientAddress, clientPort); //Step 7.
                dgramSocket.send(outPacket);  
                System.out.println("Responce sent to client.\n");

            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally { //If exception thrown, close connection.
            System.out.println("\n* Closing connection... *");
            dgramSocket.close(); //Step 9.
        }
    }
}