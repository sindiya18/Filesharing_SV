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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Client extends Application {

    ListView<String> clientFiles = new ListView<String>();
    ListView<String> serverFiles = new ListView<String>();
    Server server;

    @Override
    public void start(Stage primaryStage) throws Exception{

        //starting the server
        server = new Server();


        HBox hbox = new HBox();
        BorderPane layout = new BorderPane();
        layout.setTop(hbox);

        //create the client and server file lists in the table
        SplitPane sp = new SplitPane();;

        sp.getItems().addAll(clientFiles,serverFiles);
        layout.setCenter(sp);

        //DOWNLOAD filename
        Button downloadButton = new Button("Download");
        downloadButton.setOnAction(event ->
        {
            try
            {
                String filename = serverFiles.getSelectionModel().getSelectedItem().toString();
                Socket socket = new Socket("localhost", 8080);
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                server.createThread();

                out.println("DOWNLOAD " + filename);
                out.flush();

                OutputStream fout = new FileOutputStream(new File("LocalSharedFolder/" + filename));
                InputStream in = socket.getInputStream();

                copyAllBytes(in, fout);

                fout.close();
                socket.close();
                Dir();
            }catch(NullPointerException e) {
                Stage popup = new Stage();
                Text text = new Text();
                text.setText("Please select a file from the right, before clicking download");
                BorderPane popLayout = new BorderPane();
                popLayout.setCenter(text);
                popup.setTitle("Selection Error");
                popup.setScene(new Scene(popLayout,500,50));
                popup.show();
            }
            catch(Exception e) { e.printStackTrace(); }
        });

        //UPLOAD filename
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

                //makes sure server is ready to receive file
                InputStream ready = socket.getInputStream();
                ready.read();

                copyAllBytes(fin, sout);

                fin.close();
                socket.close();
                Dir();
            } catch(NullPointerException e) {
                Stage popup = new Stage();
                Text text = new Text();
                text.setText("Please select a file from the left, before clicking upload");
                BorderPane popLayout = new BorderPane();
                popLayout.setCenter(text);
                popup.setTitle("Selection Error");
                popup.setScene(new Scene(popLayout,750, 400));
                popup.show();
            }
            catch(Exception e) { e.printStackTrace();  }
        });

        //closes down the server
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event ->
        {
            try {
                server.disconnect();
                primaryStage.close();
            } catch (Exception e) {}
        });
        //add buttons to hbox
        hbox.getChildren().addAll(downloadButton, uploadButton, exitButton);

        primaryStage.setTitle("File Sharer v1.0");
        primaryStage.setScene(new Scene(layout, 450, 400));
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

        //update client files
        ObservableList<String> clientFiles = FXCollections.observableArrayList();
        File folder = new File("SharedFolder/");
        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            clientFiles.add(files[i].getName().toString());
        }
        clientFiles.setItems(clientFiles);

        //update server files
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

        ObservableList serverFiles = FXCollections.observableArrayList();
        String[] fileTitles = titles.split(",");
        for (int i = 0; i < fileTitles.length; i++)
        {
            serverFiles.add(fileTitles[i]);
        }

        serverFiles.setItems(serverFiles);
    }


        public static void main(String[] args) {
        launch(args);
    }
};