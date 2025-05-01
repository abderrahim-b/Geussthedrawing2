package server;

import java.io.*;
import java.net.*;

public class Server {
    Socket socket;
    InputStreamReader inputreader;
    OutputStreamWriter outputwriter;
    BufferedReader bufferedreader;
    BufferedWriter bufferedwriter;
    ServerSocket serversocket;

    public void runserver() throws IOException {
        serversocket=new ServerSocket(7777);
        System.out.println("server started");

        new Thread(() -> {
            try {
                broadcastServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        while (true){
            try {
                socket=serversocket.accept();
                inputreader = new InputStreamReader(socket.getInputStream());
                outputwriter = new OutputStreamWriter(socket.getOutputStream());
                bufferedreader = new BufferedReader(inputreader);
                bufferedwriter = new BufferedWriter(outputwriter);

                while (true){
                    String msgfromclint=bufferedreader.readLine();
                    System.out.println("From Client: "+msgfromclint);

                    bufferedwriter.write("message reccivd");
                    bufferedwriter.newLine();
                    bufferedwriter.flush();

                    if (msgfromclint.equals("exit")){
                        break;
                    }


                }
                socket.close();
                inputreader.close();
                outputwriter.close();
                bufferedreader.close();
                bufferedwriter.close();

            }catch (IOException e){
                e.printStackTrace();
            }

        }

    }
    private void broadcastServer() throws Exception {
        DatagramSocket socket = new DatagramSocket();
        String message = "ServerIP:" + InetAddress.getLocalHost().getHostAddress() + ":7777";
        byte[] buffer = message.getBytes();
        InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");

        System.out.println("Broadcasting server ip");
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, 8888);
            socket.send(packet);
            Thread.sleep(5000);
        }
    }
}
