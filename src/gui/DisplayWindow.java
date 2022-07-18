package gui;

import java.util.ArrayList;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class DisplayWindow {
	Button btHome;

	public void start(Stage primaryStage, ArrayList<String> jList) {
		// Create a border pane
		BorderPane pane = new BorderPane();
		// Place nodes in the pane
		Button btDisplay = new Button("DISPLAY JOURNEYS");
		pane.setTop(btDisplay);

		TextArea textArea = new TextArea(); // making a TextArea object.
		textArea.setPrefHeight(300); // sets height of the TextArea to 400 pixels.
		textArea.setPrefWidth(300); // sets width of the TextArea to 300 pixels.
		pane.setCenter(textArea);

		if (jList.size() == 0) {
			textArea.setText("NO RECORD FOUND.");
		}
		btDisplay.setOnAction(ev -> {
			for (String s : jList) {
				textArea.appendText(s);
			}
		});

		btHome = new Button("GO BACK");
		Button btQuit = new Button("QUIT");
		Label empty = new Label("        ");
		FlowPane btPane = new FlowPane();
		btPane.getChildren().addAll(btQuit, empty, btHome);
		btPane.setAlignment(Pos.CENTER);
		pane.setBottom(btPane);

		btQuit.setOnAction(ev -> System.exit(0));
		// Create a scene and place it in the stage
		Scene scene = new Scene(pane);
		primaryStage.setTitle("Journeys History"); // Set the stage title
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.show(); // Display the stage
	}

}
