package gui;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import fileio.ReadFile;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import myti.User;

public class MyJavaFX3 extends Application {
	private static HashMap<String, User> userList = new HashMap<String, User>();
	ListView<User> users = new ListView<User>();
	User selectedUser;
	ArrayList<String> jList = new ArrayList<>();
	ObservableList<User> listViewData = FXCollections.observableArrayList();
	private Stage primaryStage;

	UserWindow manageUser;
	BuyWindow buyJourney;
	DisplayWindow travelHistory;

	@Override // Override the start method from the superclass
	public void start(Stage currentStage) throws FileNotFoundException {
		try {
			userList = ReadFile.readUsers("output.txt");
			jList = ReadFile.readJourneys("output.txt");

		} catch (FileNotFoundException e) {
			userList = ReadFile.readUsers("input.txt");
			jList = ReadFile.readJourneys("input.txt");
		}

		primaryStage = currentStage;

		manageUser = new UserWindow();
		buyJourney = new BuyWindow();
		travelHistory = new DisplayWindow();

		var pane = createStartPage(); // For compiler to determine. Similar to auto in C++
		changeToNewScene(new Scene(pane, 300, 150), "MyTi Manager Tools");
// Display the stage -- do this when the Stage is ready!
	}

	private void changeToNewScene(Scene newScene, String title) {
		primaryStage.setTitle(title); // Set the stage title
		primaryStage.setScene(newScene); // Place the scene in the stage
		primaryStage.show();
	}

	private GridPane createStartPage() {
		Button btUser = new Button("Manage Users");
		Button btBuy = new Button("Buy Journey");
		Button btDisplay = new Button("Travel History");
		Button btQuit = new Button("Quit");
		GridPane pane = new GridPane();
		pane.setPrefWidth(200);
		pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
		pane.setVgap(40);
		pane.setHgap(40);
		pane.add(btUser, 0, 0);
		pane.add(btBuy, 0, 1);
		pane.add(btDisplay, 1, 0);
		pane.add(btQuit, 1, 1);

		btUser.setOnAction(e -> {
			try {
				manageUser.start(primaryStage, userList);
			} catch (Exception e2) {
				System.out.println(e2.getMessage());
			}
			manageUser.btHome.setOnAction(ev -> {
//				userList = manageUser.getUserList();
				var pane2 = createStartPage();
				changeToNewScene(new Scene(pane2, 300, 150), "MyTi Manager Tools");
			});
		});

		btBuy.setOnAction(e -> {
			try {
				buyJourney.start(primaryStage, userList);
			} catch (Exception e2) {
				System.out.println(e2.getMessage());
			}
			buyJourney.btHome.setOnAction(ev -> {
				var pane2 = createStartPage();
				changeToNewScene(new Scene(pane2, 300, 150), "MyTi Manager Tools");
			});
		});

		btDisplay.setOnAction(e -> {
			try {
				jList = ReadFile.readJourneys("output.txt");
			} catch (FileNotFoundException e1) {
				System.out.println("Something went wrong...");
			}
			try {
				travelHistory.start(primaryStage, jList);
			} catch (Exception e2) {
				System.out.println(e2.getMessage());
			}
			travelHistory.btHome.setOnAction(ev -> {
				var pane2 = createStartPage();
				changeToNewScene(new Scene(pane2, 300, 150), "MyTi Manager Tools");
			});
		});

		btQuit.setOnAction(ev -> {
			System.exit(0);
		});
		return pane;
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

}