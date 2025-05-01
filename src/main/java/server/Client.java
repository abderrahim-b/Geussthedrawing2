package server;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    Socket socket;
    InputStreamReader inputreader;
    OutputStreamWriter outputwriter;
    BufferedReader bufferedreader;
    BufferedWriter bufferedwriter;
    Scanner scanner;

    public void runserver() {
        try {
            String[] serverInfo = discoverServer().split(":");
            String serverIP = serverInfo[0];
            int serverPort = Integer.parseInt(serverInfo[1]);

            socket = new Socket(serverIP, serverPort);
            inputreader = new InputStreamReader(socket.getInputStream());
            outputwriter = new OutputStreamWriter(socket.getOutputStream());
            bufferedreader = new BufferedReader(inputreader);
            bufferedwriter = new BufferedWriter(outputwriter);
            scanner = new Scanner(System.in);
            while (true) {
                String msg = scanner.nextLine();
                bufferedwriter.write(msg);
                bufferedwriter.newLine();
                bufferedwriter.flush();

                System.out.println("Server:" + bufferedreader.readLine());


            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (inputreader != null) {
                    inputreader.close();
                }
                if (outputwriter != null) {
                    outputwriter.close();
                }
                if (bufferedreader != null) {
                    bufferedreader.close();
                }
                if (bufferedwriter != null) {
                    bufferedwriter.close();
                }
            } catch (IOException ee) {
                ee.printStackTrace();
            }


        }
    }
    private String discoverServer() throws Exception {
        DatagramSocket socket = new DatagramSocket(8888);
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        System.out.println("Waiting for server broadcast");
        socket.receive(packet);

        String message = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Received broadcast: " + message);

        if (message.startsWith("ServerIP:")) {
            socket.close();
            return message.substring(9);
        }
        throw new IOException("Invalid broadcast message received");
    }


}
