import java.net.*;
import java.io.*;

public class ChatMulticast implements Runnable {
    private MulticastSocket socket;
    private InetAddress group;
    private int port = 50000; // Puerto del grupo multicast
    private String username;

    public ChatMulticast(String username) {
        try {
            this.username = username;
            socket = new MulticastSocket(port);
            group = InetAddress.getByName("239.0.0.0"); // Dirección del grupo multicast
            socket.joinGroup(group);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        while(true) {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), "UTF-8").trim(); // elimina los espacios vacíos al final
                System.out.println(message);
                System.out.print("Escribe tu mensaje: ");
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void send(String message) {
        try {
            message = username + " ---> " + message;
            byte[] buffer = new byte[1024];
            byte[] bytes = message.getBytes("UTF-8");
            System.arraycopy(bytes, 0, buffer, 0, bytes.length); // copia los bytes en el buffer
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
            socket.send(packet);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Debe proporcionar el nombre de usuario como argumento.");
            return;
        }

        ChatMulticast chat = new ChatMulticast(args[0]);
        Thread thread = new Thread(chat);
        thread.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Escribe tu mensaje: ");
        while(true) {
            try {
                String message = reader.readLine();
                chat.send(message);
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}


