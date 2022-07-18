package gui;

import java.util.ArrayList;
import java.util.HashMap;

import fileio.ReadFile;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import myti.Journey;
import myti.MyTiSystem;
import myti.Station;
import myti.User;

public class BuyWindow {
	User selectedUser;
	Station selectedFromSt;
	Station selectedToSt;
	String selectedDay;
	Button btHome;
	private ArrayList<Station> stationList = new ArrayList<Station>();

	public void start(Stage primaryStage, HashMap<String, User> userList) {
		stationList = ReadFile.readStations();

		BorderPane userStationPane = new BorderPane();
		userStationPane.setPrefHeight(300);
		VBox vbox1 = new VBox();
		vbox1.setSpacing(10);
		vbox1.setPadding(new Insets(10, 20, 20, 30));
		vbox1.setPrefWidth(200);// prefWidth

		ListView<User> users = new ListView<User>();
		ReadFile.loadUsers(userList, users);
		Label lb1 = new Label("Select User:");
		lb1.setLabelFor(users);
		vbox1.getChildren().addAll(lb1, users);

		VBox vbox2 = new VBox();
		vbox2.setSpacing(10);
		vbox2.setPadding(new Insets(10, 20, 20, 30));
		vbox2.setPrefWidth(150);// prefWidth

		ListView<Station> fromSt = new ListView<Station>();
		loadStations(stationList, fromSt);
		Label lb2 = new Label("From Station:");
		lb2.setLabelFor(fromSt);
		vbox2.getChildren().addAll(lb2, fromSt);

		VBox vbox3 = new VBox();
		vbox3.setSpacing(10);
		vbox3.setPadding(new Insets(10, 20, 20, 30));
		vbox3.setPrefWidth(150);// prefWidth

		ListView<Station> toSt = new ListView<Station>();
		loadStations(stationList, toSt);
		Label lb3 = new Label("To Station:");
		lb3.setLabelFor(toSt);
		vbox3.getChildren().addAll(lb3, toSt);

		userStationPane.setLeft(vbox1);
		userStationPane.setCenter(vbox2);
		userStationPane.setRight(vbox3);

		GridPane dayTimePane = new GridPane();
		dayTimePane.setAlignment(Pos.TOP_LEFT);
		dayTimePane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
		dayTimePane.setHgap(5.5);
		dayTimePane.setVgap(5.5);

		String days[] = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
		ObservableList<String> daysData = FXCollections.observableArrayList(days);
		ChoiceBox<String> daysBox = new ChoiceBox<String>(daysData); // Shorter than days.getItems().add("Mon")...

		dayTimePane.add(new Label("Day:"), 0, 0);
		dayTimePane.add(daysBox, 1, 0);
		dayTimePane.add(new Label("Start:"), 0, 1);
		TextField start = new TextField();
		dayTimePane.add(start, 1, 1);
		dayTimePane.add(new Label("End:"), 0, 2);
		TextField end = new TextField();
		dayTimePane.add(end, 1, 2);

		VBox vbox4 = new VBox();
		vbox4.setSpacing(10);
		vbox4.setPadding(new Insets(10, 20, 20, 30));
		vbox4.setPrefWidth(300);// prefWidth

		Button btBuy = new Button("PURCHASE");
		TextArea buyMsg = new TextArea();
		buyMsg.setPrefHeight(50);
		buyMsg.setWrapText(true);
		vbox4.getChildren().addAll(dayTimePane, btBuy, buyMsg);

		fromSt.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Station>() {
			@Override
			public void changed(ObservableValue<? extends Station> observable, Station oldValue, Station newValue) {
				selectedFromSt = fromSt.getSelectionModel().getSelectedItem();
			}
		});

		toSt.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Station>() {
			@Override
			public void changed(ObservableValue<? extends Station> observable, Station oldValue, Station newValue) {
				selectedToSt = toSt.getSelectionModel().getSelectedItem();
			}
		});
//		selectedIndexProperty().addListener(new ChangeListener<Number>() NOTE: INDEX & NUMBER 
		daysBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> ov, String value, String new_value) {
				selectedDay = daysBox.getSelectionModel().getSelectedItem();
			}
		});

		btBuy.setOnAction(ev -> {
			String startStr = start.getText();
			String endStr = end.getText();
			if (selectedUser == null) {
				buyMsg.setText("First select a user!");
			} else if (selectedFromSt == null) {
				buyMsg.setText("From which station?");
			} else if (selectedToSt == null) {
				buyMsg.setText("Where to?");
			} else if (selectedDay == null) {
				buyMsg.setText("On what day?");
			} else if (startStr == "") {
				buyMsg.setText("Enter start time, e.g. 1900 for 7pm");
			} else if (endStr == "") {
				buyMsg.setText("Fill in end time too!");
			} else {
				int startTime = Integer.parseInt(startStr);
				int endTime = Integer.parseInt(endStr);
				Journey j = selectedUser.askForJourney(selectedFromSt, selectedToSt, selectedDay, startTime, endTime);
				String zone = MyTiSystem.getZone(selectedFromSt, selectedToSt);
				boolean isSuccess = MyTiSystem.findCheapestOption(selectedUser, selectedDay, startTime, zone);
				if (isSuccess) {
					selectedUser.addJourney(j);
				}
				buyMsg.setText("Purchased a " + j.getDescription() + " for " + selectedUser.getID());
			}
		});

		users.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {
			@Override
			public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
				selectedUser = users.getSelectionModel().getSelectedItem();
				if(selectedUser != null)
				{					
					buyMsg.setText("Buying for " + selectedUser.getID());
				}

			}
		});

		btHome = new Button("GO BACK");
		Button btQuit = new Button("QUIT");
		Label empty1 = new Label("        ");
		Label empty2 = new Label("        ");
		Button btSave = new Button("SAVE");
		FlowPane btPane = new FlowPane();
		btPane.getChildren().addAll(btQuit, empty1, btHome, empty2, btSave);
		btPane.setAlignment(Pos.CENTER);

		btQuit.setOnAction(ev -> {
			System.exit(0);
		});

		btSave.setOnAction(ev -> {
			ReadFile.saveFile();
//	ALTERNATIVE: SAVE ALL JOURNEYS into the userList using .forEach()
		});

		BorderPane bigPane = new BorderPane();
		bigPane.setCenter(userStationPane);
		bigPane.setRight(vbox4);
		bigPane.setBottom(btPane);

		Scene scene = new Scene(bigPane);
		primaryStage.setTitle("Journeys History"); // Set the stage title
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.show(); // Display the stage
	}
	public void clean()
	{
		
	}

	private void loadStations(ArrayList<Station> stationList, ListView<Station> stations) {
		stations.getItems().clear();
		for (Station st : stationList) {
			stations.getItems().add(st);
		}
	}

}
