package server;


import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

public class Server {

    private Canvas canvas;
    private GraphicsContext graph;
    private Button clearButton;
    private Label label;
    private TextField textField;
    private Button submitButton;
    private VBox root;
     private Socket socket;
    private   InputStreamReader inputreader;
    private  OutputStreamWriter outputwriter;
    private  BufferedReader bufferedreader;
    private BufferedWriter bufferedwriter;
    private ServerSocket serversocket;

    public Stage stage;
   public boolean isdrawing=true;

    public Server( Stage stage) {
        this.stage = stage;
    }


    public void runserver() throws IOException {

        canvas = new Canvas(500, 400);
        graph = canvas.getGraphicsContext2D();
        graph.setStroke(Color.BLACK);
        graph.setLineWidth(4);
        if (isdrawing){canvas.setOnMousePressed(e -> handleMousePressed(e));
            canvas.setOnMouseDragged(e -> handleMouseDragged(e));
            canvas.setOnMouseReleased(e -> handleMouseReleased(e));}

        clearButton = new Button("Clear");
        clearButton.setId("clearButton");
        clearButton.setOnAction(e -> {graph.clearRect(0, 0, 500, 400);
            sendClearCommand();
        });
        label = new Label("THE TOPIC:");
        textField = new TextField();
        textField.setPromptText("Type your guess");
        textField.setId("typeguess");
        submitButton = new Button("Submit");
        submitButton.getStyleClass().add("submitbutton");
        HBox hbox = new HBox(400);
        hbox.getChildren().addAll(label, clearButton);
        hbox.setAlignment(Pos.BASELINE_CENTER);
        HBox hbox2 = new HBox(200);
        hbox2.getChildren().addAll(textField, submitButton);
        hbox2.setAlignment(Pos.BASELINE_CENTER);
        HBox hbox3 = new HBox();
        hbox3.getChildren().addAll(canvas);
        hbox3.setAlignment(Pos.BASELINE_CENTER);
        root = new VBox(40);
        root.getStyleClass().add("vbox-root");
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(hbox, hbox3, hbox2);
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/app.css").toExternalForm());




        serversocket = new ServerSocket(7777);
        System.out.println("server started");

        new Thread(() -> {
            try {
                broadcastServer();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                socket = serversocket.accept();
                System.out.println("Client connected");

                Platform.runLater(() -> stage.setScene(scene));

                inputreader = new InputStreamReader(socket.getInputStream());
                outputwriter = new OutputStreamWriter(socket.getOutputStream());
                bufferedreader = new BufferedReader(inputreader);
                bufferedwriter = new BufferedWriter(outputwriter);



            } catch (IOException e) {
                System.err.println("Client error: " + e.getMessage());}
//            } finally {
//
//                try {
//                    if (bufferedwriter != null) bufferedwriter.close();
//                    if (bufferedreader != null) bufferedreader.close();
//                    if (socket != null) socket.close();
//                } catch (IOException e) {
//                    System.err.println("Failed to close resources: " + e.getMessage());
//                }
//            }
        }).start();

    }
    private void broadcastServer() throws Exception{
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

    private void handleMousePressed(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        graph.beginPath();
        graph.moveTo(x, y);
        graph.stroke();
        sendCoordinates("START", x, y);
    }

    private void handleMouseDragged(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        graph.lineTo(x, y);
        graph.stroke();
        sendCoordinates("DRAG", x, y);
    }

    private void handleMouseReleased(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        graph.lineTo(x, y);
        graph.stroke();
        graph.closePath();
        sendCoordinates("END", x, y);
    }
    private void sendCoordinates(String action, double x, double y) {
        // Format: "ACTION:X:Y" (e.g., "DRAG:125.3:80.7")
        String coordinateData = String.format("%s:%.1f:%.1f", action, x, y);

        // Send via your network connection
        try {
            if (bufferedwriter != null) {
                bufferedwriter.write(coordinateData);
                bufferedwriter.newLine();
                bufferedwriter.flush();
            }
        } catch (IOException e) {
            System.err.println("Error sending coordinates: " + e.getMessage());
        }
    }
    private void sendClearCommand() {
        try {
            if (bufferedwriter != null) {
                bufferedwriter.write("CLEAR");
                bufferedwriter.newLine();
                bufferedwriter.flush();
            }
        } catch (IOException e) {
            System.err.println("Error sending clear command: " + e.getMessage());
        }
    }

}
