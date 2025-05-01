package com.example.guessthedrawing2;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.*;


public class Window extends Application {

        public void start (Stage stage) {

               Stage primaryStage = new Stage();
                Canvas canvas = new Canvas(500, 400);
                GraphicsContext Graph = canvas.getGraphicsContext2D();
                Graph.setStroke(Color.BLACK);
                Graph.setLineWidth(4);




                canvas.setOnMousePressed(e -> {
                        Graph.beginPath();
                        Graph.moveTo(e.getX(), e.getY());
                        Graph.stroke();
                });

                canvas.setOnMouseDragged(e -> {
                        Graph.lineTo(e.getX(), e.getY());
                        Graph.stroke();
                });

               Button clearButton = new Button("Clear");
               clearButton.setId("clearButton");
               clearButton.setOnAction(e -> {
                   canvas.getGraphicsContext2D().clearRect(0, 0, 500, 400);

               });
            Label label = new Label("THE TOPIC:");
            HBox hbox = new HBox(400);
            hbox.getChildren().addAll(label);
            hbox.getChildren().add(clearButton);
            hbox.setAlignment(Pos.BASELINE_CENTER);

            TextField textField = new TextField();
            textField.setPromptText("Type your guess");
            textField.setId("typeguess");
            Button submitbutton = new Button("Submit");
            submitbutton.getStyleClass().add("submitbutton");
            HBox hbox2 = new HBox(200);
            hbox2.getChildren().addAll(textField, submitbutton);
            hbox2.setAlignment(Pos.BASELINE_CENTER);

            HBox hbox3 = new HBox();
            hbox3.getChildren().addAll(canvas);

                VBox root = new VBox(40);
                root.getStyleClass().add("vbox-root");
                root.setAlignment(Pos.CENTER);
                root.getChildren().add(hbox);
                root.getChildren().add(hbox3);
                root.getChildren().add(hbox2);


                Scene scene = new Scene(root, 800, 600);
               scene.getStylesheets().add(getClass().getResource("/app.css").toExternalForm());
                primaryStage.setTitle("GUESS THE DRAWING");
                primaryStage.setScene(scene);
                primaryStage.show();
    }

}
