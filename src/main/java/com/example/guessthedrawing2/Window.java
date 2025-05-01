package com.example.guessthedrawing2;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import server.Client;
import server.Server;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class Window extends Application {


    private Stage primaryStage=new Stage();
    private Server server;
    private Client client;
    drawingpage drawingpage;
    public void start(Stage stage) {

        drawingpage = new drawingpage();
        Scene scene = drawingpage.createScene();

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



        Scene scene1 = new Scene(Vbox,800,600);
        scene1.getStylesheets().add(getClass().getResource("/app.css").toExternalForm());

        primaryStage.setTitle("GUESS THE DRAWING");
        primaryStage.setScene(scene1);
        primaryStage.show();



















    }





}
