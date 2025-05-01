package com.example.guessthedrawing2;

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

public class drawingpage {
    private Canvas canvas;
    private GraphicsContext graph;
    private Button clearButton;
    private Label label;
    private TextField textField;
    private Button submitButton;
    private VBox root;

    public Scene createScene(){



        canvas = new Canvas(500, 400);

        graph = canvas.getGraphicsContext2D();
        graph.setStroke(Color.BLACK);
        graph.setLineWidth(4);


        canvas.setOnMousePressed(e -> handleMousePressed(e));
        canvas.setOnMouseDragged(e -> handleMouseDragged(e));


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


        return  scene;
    }
    private void handleMousePressed(MouseEvent e) {
        graph.beginPath();
        graph.moveTo(e.getX(), e.getY());
        graph.stroke();
    }


    private void handleMouseDragged(MouseEvent e) {
        graph.lineTo(e.getX(), e.getY());
        graph.stroke();
    }
}
