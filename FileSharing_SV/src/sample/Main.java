package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
    private BorderPane layout;



    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("File Sharing");
        //  primaryStage.setScene(new Scene(root, 800, 600));


        /* create an edit form (for the bottom of the user interface) */
        GridPane editArea = new GridPane();
        editArea.setPadding(new Insets(10, 10, 10, 10));
        editArea.setVgap(10);
        editArea.setHgap(10);


        Button downloadBtn = new Button("Download");

        Button uploadBtn = new Button("Upload");


        editArea.add(uploadBtn, 1, 0);
        editArea.add(downloadBtn, 2, 0);
        layout = new BorderPane();
        //layout.setLeft(uploadTable);
        // layout.setRight(downloadTable);
        layout.setTop(editArea);
        Scene scene = new Scene(layout, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

        public static void main(String[] args) {
        launch(args);
    }
};