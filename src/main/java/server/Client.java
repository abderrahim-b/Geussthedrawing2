package server;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class Client {
    private Scene homscean;
    private Canvas canvas;
    private GraphicsContext graph;
    private Button clearButton;
    private Label label;
    private TextField textField;
    private Button submitButton;
    private VBox root;
    private final int Rondes=4;
    private final int pointtowin=3;
    private int player1point=0;
    private int player2point=0;
    private int timeRemaining = 60;
    private Label timerLabel;
    private Timeline timeline;
    private VBox vboxend;
    private Text p2win;
    private Text p1win;
    private Text draw;
    private Button returnhome;
    private Scene scenefinal;
    Socket socket;
    InputStreamReader inputreader;
    OutputStreamWriter outputwriter;
    BufferedReader bufferedreader;
    BufferedWriter bufferedwriter;
    private int curentround=1;
    String randomtopic=getrandomWord();
    String guess;
    boolean isroundend=false;
   public Stage stage;
    boolean isdrawing = false;
    public Client( Stage stage ,Scene homescene) {
        this.homscean=homescene;
        this.stage = stage;

    }

    public void runserver() {

        canvas = new Canvas(500, 400);
        graph = canvas.getGraphicsContext2D();
        graph.setStroke(Color.BLACK);
        graph.setLineWidth(4);
        timerLabel = new Label("Time Remaining: " + timeRemaining + " seconds");


        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeRemaining--;
            timerLabel.setText("Time Remaining: " + timeRemaining + " seconds");

            if (timeRemaining <= 0) {
//                timeline.stop();
//                ((Timeline) event.getSource()).stop();
                isTimerEnd();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        p1win=new Text("Player 1 Win");
        p2win=new Text("Player 2 Win");
        draw=new Text("Draw");
        vboxend = new VBox(100);
        vboxend.setAlignment(Pos.CENTER);
        returnhome = new Button("Return Home");
        returnhome.getStyleClass().add("submitbutton");
        returnhome.setOnAction(event -> {
            stage.setScene(homscean);
        });
        scenefinal = new Scene(vboxend, 800, 600);
        scenefinal.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/app.css")).toExternalForm());

        Text P1=new Text("P1:"+player1point);
        Text P2=new Text("P2 (you):"+player2point);
        HBox hbox = new HBox(100);
        hbox.getChildren().addAll(timerLabel);
        hbox.setAlignment(Pos.BASELINE_CENTER);
        HBox hbox2 = new HBox(200);
        hbox2.setAlignment(Pos.BASELINE_CENTER);
        HBox hbox3 = new HBox();
        hbox3.getChildren().addAll(canvas);
        hbox3.setAlignment(Pos.BASELINE_CENTER);
        root = new VBox(40);
        root.getStyleClass().add("vbox-root");
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(hbox, hbox3, hbox2);
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/app.css")).toExternalForm());




































        new Thread(() -> {
            try {
                String[] serverInfo = discoverServer().split(":");
                String serverIP = serverInfo[0];
                int serverPort = Integer.parseInt(serverInfo[1]);

                socket = new Socket(serverIP, serverPort);
                System.out.println(" conected to the server");
                Platform.runLater(() -> {stage.setScene(scene);
                    startTimer();
                } );
                inputreader = new InputStreamReader(socket.getInputStream());
                outputwriter = new OutputStreamWriter(socket.getOutputStream());
                bufferedreader = new BufferedReader(inputreader);
                bufferedwriter = new BufferedWriter(outputwriter);
            }catch(Exception e){
                e.printStackTrace();
            }
            while (true){
                if (!hbox3.getChildren().contains(P1) && !hbox3.getChildren().contains(P2)) {
                    Platform.runLater(() -> {
                        hbox3.getChildren().addAll(P1,P2);

                    });

                }
                Platform.runLater(() -> {
                    P1.setText("P1:"+player1point);
                    P2.setText("P2(you):"+player2point);

                });
                randomtopic=getrandomWord();
                try {


                    if (isdrawing==false) {

                        Platform.runLater(() -> {
                            canvas.setOnMousePressed(null);
                            canvas.setOnMouseDragged(null);
                            canvas.setOnMouseReleased(null);
                            hbox.getChildren().remove(clearButton);
                            hbox.getChildren().remove(label);
                            textField = new TextField();
                            textField.setPromptText("Type your guess");
                            textField.setId("typeguess");
                            submitButton = new Button("Submit");

                            try {
                                bufferedwriter.write("  ");
                                bufferedwriter.newLine();
                                bufferedwriter.flush();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            submitButton.getStyleClass().add("submitbutton");
                            if (!hbox2.getChildren().contains(textField) || !hbox2.getChildren().contains(submitButton)) {
                                hbox2.getChildren().addAll(textField, submitButton);
                            }
                            submitButton.setOnAction(event -> {
                                Platform.runLater(() -> {
                                    guess = textField.getText();
                                    textField.setText("");
                                    try {
                                        bufferedwriter.write("Guess:" + guess);
                                        bufferedwriter.newLine();
                                        bufferedwriter.flush();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });



                            });
                        });


                        String data;

                        while ((data = bufferedreader.readLine()) != null) {

                            if (isroundend==true){
                                if (isdrawing==false) {
                                    randomtopic=getrandomWord();
                                    curentround++;
                                    player1point++;
                                    if (curentround <= Rondes) {

                                        if (player1point>=pointtowin) {
                                            Platform.runLater(() -> {
                                                vboxend.getChildren().addAll(p2win,returnhome);
                                                stage.setScene(scenefinal);

                                            });

                                        }

                                    } else {
                                        if (player2point>player1point){
                                            Platform.runLater(() -> {
                                                vboxend.getChildren().addAll(p2win,returnhome);
                                                stage.setScene(scenefinal);
                                            });
                                        }else if (player2point==player1point){
                                            Platform.runLater(() -> {
                                                vboxend.getChildren().addAll(draw,returnhome);
                                                stage.setScene(scenefinal);
                                            });
                                        }else if (player1point>player2point){
                                            Platform.runLater(() -> {
                                                vboxend.getChildren().addAll(p1win,returnhome);
                                                stage.setScene(scenefinal);
                                            });
                                        }

                                    }


                                }

                                isdrawing=!isdrawing;
                                isroundend=!isroundend;
                                break;}
                            if (data.equals("CLEAR")) {

                                Platform.runLater(() -> graph.clearRect(0, 0, 500, 400));
                                continue;
                            }
                            if (data.equals("truegeuss")) {
                                resetTimer();
                                isdrawing=!isdrawing;
                                curentround++;
                                player2point++;
                                if ((curentround) <= Rondes) {

                                    if (player2point >= pointtowin) {
                                        Platform.runLater(() -> {
                                            vboxend.getChildren().addAll(p2win, returnhome);
                                            stage.setScene(scenefinal);

                                        });
                                        break;
                                    }
                                    break;
                                } else {
                                    if (player2point>player1point){
                                        Platform.runLater(() -> {
                                            vboxend.getChildren().addAll(p2win,returnhome);
                                            stage.setScene(scenefinal);
                                        });
                                        break;
                                    }else if (player2point==player1point){
                                        Platform.runLater(() -> {
                                            vboxend.getChildren().addAll(draw,returnhome);
                                            stage.setScene(scenefinal);
                                        });
                                        break;
                                    }else if (player1point>player2point){
                                        Platform.runLater(() -> {
                                            vboxend.getChildren().addAll(p1win,returnhome);
                                            stage.setScene(scenefinal);
                                        });
                                    }
                                    break;
                                }


                            }

                            String[] parts = data.split(":");

                            if (parts.length == 3) {
                                String action = parts[0];
                                double x = Double.parseDouble(parts[1].replace(",", "."));
                                double y = Double.parseDouble(parts[2].replace(",", "."));
                            


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


                    } else {


                        Platform.runLater(() -> {
                            canvas.setOnMousePressed(this::handleMousePressed);
                            canvas.setOnMouseDragged(this::handleMouseDragged);
                            canvas.setOnMouseReleased(this::handleMouseReleased);
                            hbox2.getChildren().remove(textField);
                            hbox2.getChildren().remove(submitButton);
                            clearButton = new Button("Clear");
                            clearButton.setId("clearButton");
                            label = new Label("THE TOPIC:" + randomtopic);

                            if (!hbox.getChildren().contains(clearButton)) {
                                hbox.getChildren().addAll(label, clearButton);

                            }
                            clearButton.setOnAction(e -> {
                                graph.clearRect(0, 0, 500, 400);
                                sendClearCommand();
                            });
                            try {
                                bufferedwriter.write("  ");
                                bufferedwriter.newLine();
                                bufferedwriter.flush();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }


                        });


                        try {
                            String data1;
                            while ((data1 = bufferedreader.readLine()) != null) {
                                if (isroundend == true) {
                                        randomtopic = getrandomWord();
                                        curentround++;
                                        player2point++;
                                        if (curentround <= Rondes) {

                                            if (player2point >= pointtowin) {
                                                Platform.runLater(() -> {
                                                    vboxend.getChildren().addAll(p2win, returnhome);
                                                    stage.setScene(scenefinal);

                                                });

                                            }

                                        } else {
                                            if (player2point > player1point) {
                                                Platform.runLater(() -> {
                                                    vboxend.getChildren().addAll(p2win, returnhome);
                                                    stage.setScene(scenefinal);
                                                });
                                                break;
                                            } else if (player2point == player1point) {
                                                Platform.runLater(() -> {
                                                    vboxend.getChildren().addAll(draw, returnhome);
                                                    stage.setScene(scenefinal);
                                                });
                                                break;
                                            } else if (player1point > player2point) {
                                                Platform.runLater(() -> {
                                                    vboxend.getChildren().addAll(p1win, returnhome);
                                                    stage.setScene(scenefinal);
                                                });
                                            }
                                            break;
                                        }

                                    isdrawing = !isdrawing;
                                    isroundend = !isroundend;
                                    break;

                                }


                                if (data1.startsWith("Guess:")) {

                                    String[] parts = data1.split(":", 2);
                                    String guessWord = parts[1];

                                    if (guessWord.equalsIgnoreCase(randomtopic)) {
                                        bufferedwriter.write("truegeuss");
                                        bufferedwriter.newLine();
                                        bufferedwriter.flush();
                                        isdrawing = !isdrawing;
                                        resetTimer();
                                        curentround++;
                                        player1point++;
                                        if (curentround <= Rondes) {

                                            if (player1point >= pointtowin) {
                                                Platform.runLater(() -> {
                                                    vboxend.getChildren().addAll(p1win, returnhome);
                                                    stage.setScene(scenefinal);

                                                });
                                                break;
                                            }

                                        } else {
                                            if (player2point > player1point) {
                                                Platform.runLater(() -> {
                                                    vboxend.getChildren().addAll(p2win, returnhome);
                                                    stage.setScene(scenefinal);
                                                });
                                                break;
                                            } else if (player2point == player1point) {
                                                Platform.runLater(() -> {
                                                    vboxend.getChildren().addAll(draw, returnhome);
                                                    stage.setScene(scenefinal);
                                                });
                                                break;
                                            } else if (player1point > player2point) {
                                                Platform.runLater(() -> {
                                                    vboxend.getChildren().addAll(p1win, returnhome);
                                                    stage.setScene(scenefinal);
                                                });
                                            }
                                            break;
                                        }



                                        break;
                                    }
                                }
                        }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }



                    }


                } catch (IOException e) {
                    System.err.println("Client error: " + e.getMessage());
                }
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
            }
        }).start();
    }
    private void isTimerEnd() {
        randomtopic=getrandomWord();
        stopTimer();

        isroundend=true;

        try {
            bufferedwriter.write("  ");
            bufferedwriter.newLine();
            bufferedwriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Platform.runLater(this::resetTimer);

    }

    private void resetTimer() {
        stopTimer();

        timeRemaining = 60;
        Platform.runLater(() -> {
            timerLabel.setText("Time Remaining: " + timeRemaining + " seconds");
        });



        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            timeRemaining--;
            timerLabel.setText("Time Remaining: " + timeRemaining + " seconds");

            if (timeRemaining <= 0) {
                timerLabel.setText("Time's up!");
                stopTimer();
                isTimerEnd();
            }
        }));

        startTimer();

    }
    private void startTimer() {

        timeline.play();
    }

    private void stopTimer() {

        timeline.stop();
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

        String coordinateData = String.format("%s:%.1f:%.1f", action, x, y);

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

    public static String getrandomWord() {

        String[] words = {
                "Sun", "Tree", "House", "Car", "Cat", "Dog", "Apple", "Star", "Heart", "Fish",
                "Flower", "Balloon", "Book", "Mountain", "Cup", "Cloud", "Ice cream", "Shoe", "Boat", "Pencil"
        };


        Random random = new Random();


        int index = random.nextInt(words.length);


        return words[index];
    }




}
