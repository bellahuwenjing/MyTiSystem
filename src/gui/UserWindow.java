package gui;

import java.util.HashMap;

import fileio.ReadFile;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
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
import javafx.stage.Stage;
import myti.InvalidAmount;
import myti.MyTiSystem;
import myti.User;

public class UserWindow {
	private static HashMap<String, User> savedUserList;
	ListView<User> users = new ListView<User>();
	User selectedUser;
	ObservableList<User> listViewData = FXCollections.observableArrayList();
	Button btHome;

	public void start(Stage primaryStage, HashMap<String, User> userList) {
		GridPane userListPane = new GridPane();
		userListPane.setPrefWidth(200);
		userListPane.setAlignment(Pos.CENTER);
		userListPane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
		userListPane.setVgap(4);
		userListPane.setHgap(4);
		userListPane.setStyle("-fx-background-color: DAE6F3;");

		Label creditInfo = new Label();
		Label enterAmount = new Label("Enter an amount to top up:");
		TextField amountString = new TextField();
		TextArea topUpMsg = new TextArea();
		topUpMsg.setPrefHeight(60);
		topUpMsg.setWrapText(true);
		Button btAdd = new Button("ADD CREDIT");
		savedUserList = userList;

		listViewData = ReadFile.loadUsers(userList, users);

		userListPane.add(new Label("Select a user:"), 0, 0);
		userListPane.add(users, 0, 1);
		userListPane.add(creditInfo, 0, 2);
		userListPane.add(enterAmount, 0, 3);
		userListPane.add(amountString, 0, 4);
		userListPane.add(btAdd, 0, 5);
		userListPane.add(topUpMsg, 0, 6);

		users.setOrientation(Orientation.VERTICAL); // doesn't seem to make a difference?
		users.setPrefSize(100, 100);
		users.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {
			@Override
			public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
				selectedUser = users.getSelectionModel().getSelectedItem();
				if (selectedUser != null) {
					creditInfo.setText(
							"Credit for " + selectedUser.getID() + " : $" + selectedUser.getCard().getCredit());
				}
				btAdd.setOnAction(ev -> {
					String s = amountString.getText();
					if (s.length() == 0) {
						topUpMsg.setText("NO INPUT!");
					}
					try {
						double amount = Double.parseDouble(s);
						if (amount <= 0) {
							topUpMsg.setText("Enter a positive number!");
						} else {
							double validAmount;
							try {
								validAmount = checkAmount(selectedUser, amount);
								System.out.println("Adding $" + validAmount + "!");
								MyTiSystem.recharge(selectedUser, validAmount);
								topUpMsg.setText("$" + s + " added to " + selectedUser.getID());
								creditInfo.setText("Credit for " + selectedUser.getID() + " : $"
										+ selectedUser.getCard().getCredit());

							} catch (InvalidAmount e1) {
								topUpMsg.setText(e1.getMessage());
							}
						}
					} catch (Exception e2) {
						topUpMsg.setText("Invalid input!");
					}
				});

			}

		});

		btAdd.setOnAction(ev -> {
			topUpMsg.setText("First select a user!");
		});

		// Create a pane for creating new users
		GridPane newUserPane = new GridPane();
		newUserPane.setAlignment(Pos.TOP_CENTER);
		newUserPane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
//		newUserPane.setPrefWidth(300);
		newUserPane.setHgap(5.5);
		newUserPane.setVgap(5.5);
		newUserPane.setStyle("-fx-background-color: DAE6F3;");
		newUserPane.add(new Label("ID:"), 0, 0);
		TextField id = new TextField();
		newUserPane.add(id, 1, 0);
		newUserPane.add(new Label("Name:"), 0, 1);
		TextField name = new TextField();
		newUserPane.add(name, 1, 1);
		newUserPane.add(new Label("Email:"), 0, 2);
		TextField email = new TextField();
		newUserPane.add(email, 1, 2);
		newUserPane.add(new Label("Type:"), 0, 3);
		// alternative to using a menu (can't display selections)
		ChoiceBox<String> types = new ChoiceBox<String>();
		types.getItems().add("Adult");
		types.getItems().add("Senior");
		types.getItems().add("Junior");

		newUserPane.add(types, 1, 3);
		Button btAddUser = new Button("CREATE USER");
		newUserPane.add(btAddUser, 1, 4);
		TextArea createUserMsg = new TextArea();
		createUserMsg.setPrefWidth(100);
		createUserMsg.setPrefHeight(20);
//		createUserMsg.setWrapText(true);
		newUserPane.add(createUserMsg, 1, 5);

		btAddUser.setOnAction(ev -> {
			String idStr = id.getText();
			User tempUser = savedUserList.get(idStr);
			if (tempUser == null) {
				String nameStr = name.getText();
				String emailStr = email.getText();
				String typeStr = types.getValue();
				if (nameStr.length() == 0 || idStr.length() == 0 || emailStr.length() == 0 || typeStr == null) {
					createUserMsg.setText("INCOMPLETE INPUT!");
				} else {
					User newUser = new User(idStr, typeStr, nameStr, emailStr);
					savedUserList.put(idStr, newUser);
					createUserMsg.setText("Successfully added a new user " + idStr);
					ReadFile.loadUsers(savedUserList, users);
				}
			} else {
				createUserMsg.setText("User ID already exists.");
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
		});
		// Create a border pane
		BorderPane pane = new BorderPane();
		// Place nodes in the pane
		pane.setLeft(userListPane);
		pane.setRight(newUserPane);
		pane.setBottom(btPane);

		// Create a scene and place it in the stage
		Scene scene = new Scene(pane);
		primaryStage.setTitle("Manage Users"); // Set the stage title
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.show(); // Display the stage
	}

	public static double checkAmount(User user, double amount) throws InvalidAmount {
		double balance = user.getCard().getCredit();
		if ((balance + amount) > 100) {
			throw new InvalidAmount("That takes you over the credit limit. Please enter a smaller amount.\r\n");
		} else if (amount % 5 != 0) {
			throw new InvalidAmount("Charge amounts must be in multiples of 5.\r\n");
		} else {
			return amount;
		}
	}


}
