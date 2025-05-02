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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Canvas canvas;
    private GraphicsContext graph;
    private Button clearButton;
    private Label label;
    private TextField textField;
    private Button submitButton;
    private VBox root;
    Socket socket;
    InputStreamReader inputreader;
    OutputStreamWriter outputwriter;
    BufferedReader bufferedreader;
    BufferedWriter bufferedwriter;

   public Stage stage;
    boolean isdrawing = false;
    public Client( Stage stage) {
        this.stage = stage;

    }

    public void runserver() {

        canvas = new Canvas(500, 400);
        graph = canvas.getGraphicsContext2D();
        graph.setStroke(Color.BLACK);
        graph.setLineWidth(4);
        if (isdrawing){ canvas.setOnMousePressed(e -> handleMousePressed(e));
            canvas.setOnMouseDragged(e -> handleMouseDragged(e));
            canvas.setOnMouseReleased(e -> handleMouseReleased(e));};

        clearButton = new Button("Clear");
        clearButton.setId("clearButton");
        clearButton.setOnAction(e -> graph.clearRect(0, 0, 500, 400));
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




































        new Thread(() -> {
            try {
                String[] serverInfo = discoverServer().split(":");
                String serverIP = serverInfo[0];
                int serverPort = Integer.parseInt(serverInfo[1]);

                socket = new Socket(serverIP, serverPort);
                System.out.println(" conected to the server");
                Platform.runLater(() -> stage.setScene(scene));
                inputreader = new InputStreamReader(socket.getInputStream());
                outputwriter = new OutputStreamWriter(socket.getOutputStream());
                bufferedreader = new BufferedReader(inputreader);
                bufferedwriter = new BufferedWriter(outputwriter);

                while (true) {

                    String data;
                    while ((data = bufferedreader.readLine()) != null) { // Read the data only once
                        if (data.equals("CLEAR")) {

                            Platform.runLater(() -> graph.clearRect(0, 0, 500, 400));
                            continue;
                        }


                        String[] parts = data.split(":");
                        if (parts.length == 3) {
                            String action = parts[0];
                            double x = Double.parseDouble(parts[1]);
                            double y = Double.parseDouble(parts[2]);

                            Platform.runLater(() -> {
                                switch (action) {
                                    case "START":
                                        graph.beginPath();
                                        graph.moveTo(x, y);
                                        break;
                                    case "DRAG":
                                        graph.lineTo(x, y);
                                        graph.stroke();
                                        break;
                                    case "END":
                                        graph.lineTo(x, y);
                                        graph.stroke();
                                        graph.closePath();
                                        break;
                                }
                            });
                        } else {
                            System.err.println("Malformed data received: " + data);
                        }
                    }






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
        }).start();
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

    private void handleMousePressed(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        graph.beginPath();
        graph.moveTo(x, y);
        graph.stroke();

    }

    private void handleMouseDragged(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        graph.lineTo(x, y);
        graph.stroke();

    }

    private void handleMouseReleased(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        graph.lineTo(x, y);
        graph.stroke();
        graph.closePath();

    }

}
