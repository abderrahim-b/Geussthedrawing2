package com.example.guessthedrawing2;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import server.Client;
import server.Server;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class Window extends Application {


    private Stage primaryStage;
    private Server server;
    private Client client;


    public void start(Stage stage) {
        primaryStage=new Stage();
         server = new Server( primaryStage);
        client = new Client( primaryStage);



//        drawingpage = new drawingpage(server, client);
//        Scene scene = drawingpage.createScene();
//        server=new Server(scene,primaryStage);
//        client=new Client(scene,primaryStage);

        Text text = new Text("GUESS THE DRAWING");
        text.setId("text");
        Button button = new Button("Host a Game");
        button.getStyleClass().add("submitbutton");
        Button button1 = new Button("Join a Game");
        button1.getStyleClass().add("submitbutton");

        VBox vbox2 = new VBox();
        vbox2.setAlignment(Pos.BASELINE_CENTER);
        vbox2.getChildren().addAll(button,button1);
        vbox2.setSpacing(20);



        VBox Vbox = new VBox();
        Vbox.getChildren().addAll(text,vbox2);
        Vbox.setAlignment(Pos.BASELINE_CENTER);
        Vbox.setSpacing(100);
        Vbox.setPadding(new Insets(100));

        Text text1 = new Text("Waiting for a player to connect");
        VBox vbox3 = new VBox();
        vbox3.setAlignment(Pos.BASELINE_CENTER);
        vbox3.getChildren().addAll(text1);
        Scene scene2 = new Scene(vbox3,800,600);



        Scene scene1 = new Scene(Vbox,800,600);
        scene1.getStylesheets().add(getClass().getResource("/app.css").toExternalForm());

        primaryStage.setTitle("GUESS THE DRAWING");
        primaryStage.setScene(scene1);

        button.setOnAction(e -> {
            primaryStage.setScene(scene2);
            new Thread(() ->{
                try {
                    server.runserver();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } ).start();

        });

        button1.setOnAction(e -> {
           primaryStage.setScene(scene2);
            new Thread(() ->{

                    client.runserver();

            } ).start();
        });




        primaryStage.show();







    }





}
