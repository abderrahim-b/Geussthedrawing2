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
import java.net.*;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class Server {
    private Scene homscean;
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
    private int timeRemaining =60;
    private Label timerLabel;
    private Timeline timeline;
    public Stage stage;
   public boolean isdrawing=true;
    private final int Rondes=4;
    private int curentround=1;
    private final int pointtowin=3;
    private int player1point=0;
    private int player2point=0;
    private VBox vboxend;
   private Text p2win;
   private Text p1win;
   private Text draw;
   private Button returnhome;
   private Scene scenefinal;
    String randomtopic=getrandomWord();
    String guess;
    boolean isroundend=false;

    public Server( Stage stage,Scene homescene) {
        this.homscean=homescene;
        this.stage = stage;
    }


    public void runserver() throws IOException {

        canvas = new Canvas(500, 400);
        graph = canvas.getGraphicsContext2D();
        graph.setStroke(Color.BLACK);
        graph.setLineWidth(4);
       canvas.setOnMousePressed(this::handleMousePressed);
       canvas.setOnMouseDragged(this::handleMouseDragged);
       canvas.setOnMouseReleased(this::handleMouseReleased);

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

        //label = new Label("THE TOPIC:");

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

                    Platform.runLater(() -> {
                        stage.setScene(scene);
                        startTimer();
                    });

                    inputreader = new InputStreamReader(socket.getInputStream());
                    outputwriter = new OutputStreamWriter(socket.getOutputStream());
                    bufferedreader = new BufferedReader(inputreader);
                    bufferedwriter = new BufferedWriter(outputwriter);

                }catch(Exception e){
                    e.printStackTrace();
                }
                    while (true){
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
                            submitButton.setOnAction(event -> {
                                Platform.runLater(() -> {
                                    guess = textField.getText();
                                    textField.setText("");
                                });
                                try {
                                    bufferedwriter.write("Guess:" + guess);
                                    bufferedwriter.newLine();
                                    bufferedwriter.flush();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }


                            });
                            submitButton.getStyleClass().add("submitbutton");
                            if (!hbox.getChildren().contains(textField) || !hbox.getChildren().contains(submitButton)) {
                                hbox2.getChildren().addAll(textField, submitButton);
                            }
                        });
                        String data;
                        while ((data = bufferedreader.readLine()) != null) {

                            if (isroundend==true){
                                if (isdrawing==false) {
                                    randomtopic=getrandomWord();
                                    if ((curentround + 1) < Rondes) {
                                        curentround++;
                                        if (player2point >= pointtowin|| player2point+1>=pointtowin) {
                                            Platform.runLater(() -> {
                                                vboxend.getChildren().addAll(p2win,returnhome);
                                                stage.setScene(scenefinal);

                                            });

                                        } else {
                                            player2point++;
                                        }
                                    } else {

                                    }


                                }else{
                                    if ((curentround + 1) < Rondes) {
                                        curentround++;
                                        if (player1point >= pointtowin || player1point+1>=pointtowin) {
                                            Platform.runLater(() -> {
                                                vboxend.getChildren().addAll(p1win,returnhome);
                                                stage.setScene(scenefinal);

                                            });

                                        } else {
                                            player1point++;
                                        }
                                    } else {
                                        Platform.runLater(() -> {
                                            vboxend.getChildren().addAll(draw,returnhome);
                                            stage.setScene(scenefinal);

                                        });
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
                                if ((curentround + 1) < Rondes) {
                                    curentround++;
                                    if (player1point >= pointtowin) {
                                        Platform.runLater(() -> {
                                            vboxend.getChildren().addAll(p1win, returnhome);
                                            stage.setScene(scenefinal);

                                        });
                                        break;
                                    } else {
                                        player1point++;
                                        break;
                                    }
                                } else {
                                    Platform.runLater(() -> {
                                        vboxend.getChildren().addAll(draw, returnhome);
                                        stage.setScene(scenefinal);

                                    });
                                    break;
                                }


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


                    } else {


                        Platform.runLater(() -> {
                            hbox2.getChildren().remove(textField);
                            hbox2.getChildren().remove(submitButton);
                            clearButton = new Button("Clear");
                            clearButton.setId("clearButton");
                            label = new Label("THE TOPIC:" + randomtopic);
                            try {
                                bufferedwriter.write("  ");
                                bufferedwriter.newLine();
                                bufferedwriter.flush();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            clearButton.setOnAction(e -> {
                                graph.clearRect(0, 0, 500, 400);
                                sendClearCommand();
                            });
                            if (!hbox.getChildren().contains(clearButton)) {
                                hbox.getChildren().addAll(label, clearButton);

                            }


                            canvas.setOnMousePressed(this::handleMousePressed);
                            canvas.setOnMouseDragged(this::handleMouseDragged);
                            canvas.setOnMouseReleased(this::handleMouseReleased);
                        });


                            try {
                                String data1;
                               while ((data1 = bufferedreader.readLine()) != null) {
                                   if (isroundend==true){
                                       if (isdrawing==false) {
                                           randomtopic=getrandomWord();
                                           if ((curentround + 1) < Rondes) {
                                               curentround++;
                                               if (player2point >= pointtowin|| player2point+1>=pointtowin) {
                                                   Platform.runLater(() -> {
                                                       vboxend.getChildren().addAll(p2win,returnhome);
                                                       stage.setScene(scenefinal);

                                                   });

                                               } else {
                                                   player2point++;
                                               }
                                           } else {

                                           }


                                       }else{
                                           if ((curentround + 1) < Rondes) {
                                               curentround++;
                                               if (player1point >= pointtowin || player1point+1>=pointtowin) {
                                                   Platform.runLater(() -> {
                                                       vboxend.getChildren().addAll(p1win,returnhome);
                                                       stage.setScene(scenefinal);

                                                   });

                                               } else {
                                                   player1point++;
                                               }
                                           } else {
                                               Platform.runLater(() -> {
                                                   vboxend.getChildren().addAll(draw,returnhome);
                                                   stage.setScene(scenefinal);

                                               });
                                           }




                                       }

                                       isdrawing=!isdrawing;
                                       isroundend=!isroundend;
                                       break;}
                                    if (data1.startsWith("Guess:")) {

                                        String[] parts = data1.split(":", 2);
                                        String guessWord = parts[1];

                                        if (guessWord.equalsIgnoreCase(randomtopic)) {
                                            bufferedwriter.write("truegeuss");
                                            bufferedwriter.newLine();
                                            bufferedwriter.flush();
                                            isdrawing = !isdrawing;
                                            resetTimer();
                                            if ((curentround + 1) < Rondes) {
                                                curentround++;
                                                if (player2point >= pointtowin) {
                                                    Platform.runLater(() -> {
                                                        vboxend.getChildren().addAll(p2win, returnhome);
                                                        stage.setScene(scenefinal);

                                                    });
                                                    break;
                                                } else {
                                                    player2point++;
                                                    break;
                                                }
                                            } else {
                                                Platform.runLater(() -> {
                                                    vboxend.getChildren().addAll(draw, returnhome);
                                                    stage.setScene(scenefinal);

                                                });
                                            }
                                            ;


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

        try {
            bufferedwriter.write("  ");
            bufferedwriter.newLine();
            bufferedwriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        isroundend=true;
        Platform.runLater(this::resetTimer);
    }

    private void resetTimer() {
        stopTimer();
        timeRemaining = 60;
        timerLabel.setText("Time Remaining: " + timeRemaining + " seconds");


        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            timeRemaining--;
            timerLabel.setText("Time Remaining: " + timeRemaining + " seconds");
            if (timeRemaining <= 0) {
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
