README.txt

For my Assignment 2B, I used -  
the IDE: eclipse
the Java version: JavaSE-11
the JavaFX version: 18.0.1
the arguments under Run > Run Configurations:
--module-path "javafx-sdk-18.0.1/lib" --add-modules=javafx.controls,javafx.fxml,javafx.graphics,javafx.base

Directions:
1. The project consists of three sub-programs which read from 
(initially from "input.txt") and write to a single file 
("output.txt" which will be created when the SAVE button is clicked).

2. A 24 hour time representation is used: 
for example 800 means 8am and 2359 means 11.59pm

3. GIT HUB LINK:
https://github.com/RMIT-COSC1295/assignment-2-bellahuRMIT

4. At the moment, the programs is able to purchase and display journeys, update and recharge MyTi balance, 
adding a new user and other admin functions. Given more time, I would like to keep on improving the functionality  of 
the BuyJourney component, implementing the business logic at the same level of 
accuracy as my submission for Assignment 2A.

5. Although the recommended JavaFX component for the user list and station lists seems to be
Menus, I found that Menus do not provide the best GUI appearance as they can't display selections.  
Instead I used alternatives like ListView and ChoiceBox.