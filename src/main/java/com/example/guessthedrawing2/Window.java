package com.example.guessthedrawing2;
import server.Client;
import server.Server;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class Window extends Application {


    private Stage primaryStage=new Stage();
    private Server server;
    private Client client;

    public void start(Stage stage) {

        drawingpage drawingpage = new drawingpage();
        Scene scene = drawingpage.createScene();
        primaryStage.setTitle("GUESS THE DRAWING");
        primaryStage.setScene(scene);
        primaryStage.show();



















    }





}
