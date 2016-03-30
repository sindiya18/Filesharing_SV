package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

/**
 * Created by venujan on 25/03/16.
 */

public class Client extends Application {
    ListView<String> clientFiles = new ListView<String>();
    ListView<String> serverFiles = new ListView<String>();
    Server server;

    @Override
    public void start(Stage primaryStage) throws Exception{
        //starting the server
        server = new Server();

        HBox hbox = new HBox(315);
        BorderPane layout = new BorderPane();
        layout.setTop(hbox);

        //creating split table (local/server)
        SplitPane splitpane = new SplitPane();

        //getting files and adding to split panes
        splitpane.getItems().addAll(clientFiles, serverFiles);
        layout.setCenter(splitpane);

        //DOWNLOAD button action on click
        Button downloadButton = new Button("Download");
        downloadButton.setOnAction(event ->
        {
            try
            {
                String filename = serverFiles.getSelectionModel().getSelectedItem().toString();

                //connecting to localhost with port 8080
                Socket socket = new Socket("localhost", 8080);
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                server.createThread();

                //listening to "DOWNLOAD" command
                out.println("DOWNLOAD " + filename);
                out.flush();

                OutputStream fout = new FileOutputStream(new File("SharedFolder/" + filename));
                InputStream in = socket.getInputStream();

                copyAllBytes(in, fout);

                fout.close();
                socket.close();
                Dir();
            }
            catch(Exception e) { e.printStackTrace(); }
        });

        //UPLOAD button on click action
        Button uploadButton = new Button("Upload");
        uploadButton.setOnAction(event -> {
            try {
                String filename = clientFiles.getSelectionModel().getSelectedItem().toString();
                Socket socket = new Socket("localhost", 8080);
                OutputStream sout = socket.getOutputStream();
                PrintWriter out = new PrintWriter(sout);
                server.createThread();

                out.println("UPLOAD " + filename);
                out.flush();

                InputStream fin = new FileInputStream(new File("SharedFolder/" + filename));

                //makes sure server is open to receive file
                InputStream open = socket.getInputStream();
                open.read();

                copyAllBytes(fin, sout);

                fin.close();
                socket.close();
                Dir();
            }
            catch(Exception e) { e.printStackTrace();  }
        });

        hbox.getChildren().addAll(uploadButton, downloadButton);

        primaryStage.setTitle("File Sharing Application");
        primaryStage.setScene(new Scene(layout, 750, 500));
        primaryStage.show();

        Dir();
    }

    private void copyAllBytes(InputStream in, OutputStream out) throws IOException
    {
        int cByte = 0;
        try {
            while ((cByte = in.read()) != -1) {
                out.write(cByte);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.flush();
    }

    private void Dir() throws Exception {

        //setting client FileNames
        ObservableList<String> clientFiles = FXCollections.observableArrayList();
        File folder = new File("SharedFolder/");
        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            clientFiles.add(files[i].getName().toString());
        }
        this.clientFiles.setItems(clientFiles);

        // connecting to localhost through port 8080
        Socket socket = new Socket("localhost", 8080);
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        server.createThread();
        out.println("DIR ");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String titles = "";
        try
        {
            while ((titles = in.readLine()) != null) {
                break;
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        socket.close();

        //array for holding Filenames
        ObservableList serverFiles = FXCollections.observableArrayList();
        String[] fileNames = titles.split(",");
        for (int i = 0; i < fileNames.length; i++)
        {
            serverFiles.add(fileNames[i]);
        }

        this.serverFiles.setItems(serverFiles);
    }


        public static void main(String[] args) {
        launch(args);
    }
};