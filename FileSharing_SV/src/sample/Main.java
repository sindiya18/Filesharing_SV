package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
    private BorderPane layout;
    private TableView<String> uploadTable = new TableView();;
    private TableView<String> downloadTable= new TableView();;



    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("File Sharing");
        primaryStage.setScene(new Scene(root, 800, 600));

        SplitPane splitPane1 = new SplitPane();
        splitPane1.setPrefSize(500, 200);

        uploadTable.setItems(DataSource.getClientFiles());
        downloadTable.setItems(DataSource.getServerFiles());

        TableColumn clientColumn = null;
        clientColumn = new TableColumn("Local Files");
        clientColumn.setMinWidth(300);
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("FileName"));

        TableColumn serverColumn = null;
        serverColumn = new TableColumn("Server Files");
        serverColumn.setMinWidth(300);
        serverColumn.setCellValueFactory(new PropertyValueFactory<>("FileName"));


        uploadTable.getColumns().add(clientColumn);
        downloadTable.getColumns().add(serverColumn);


        splitPane1.getItems().addAll(uploadTable, downloadTable);

        //BUTTON FUNCTIONALITY
        GridPane functionArea = new GridPane();
        Button up = new Button("Upload");
        Button down = new Button("Download");
        up.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent upload) {
                //BUTTON FUNCTIONALITY
                //UPLOADS SELECTED FILE
                String selected = uploadTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    downloadTable.getItems().add(selected);
                }
            }
        });

        down.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent download) {
                //BUTTON FUNCTIONALITY
                //DOWNLOAD SELECTED FILE
                String selected = downloadTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    uploadTable.getItems().add(selected);
                }
            }
        });

        functionArea.add(up, 1, 1);
        functionArea.add(down, 3, 1);

        layout = new BorderPane();
        layout.setTop(functionArea);
        layout.setCenter(splitPane1);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }



        public static void main(String[] args) {
        launch(args);
    }
};