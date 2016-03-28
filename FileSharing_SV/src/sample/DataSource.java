package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by venujan on 28/03/16.
 */
public class DataSource {
    static ObservableList<String> clientFiles;
    static ObservableList<String> serverFiles;
    public DataSource(){
        clientFiles = FXCollections.observableArrayList();
        serverFiles = FXCollections.observableArrayList();
    }
    public static ObservableList<String> getClientFiles() {
        return clientFiles;
    }
    public static ObservableList<String> getServerFiles() {
        return serverFiles;
    }

    public static void addClientFile(String file){
        clientFiles.add(file);
    }
    public static void addServerFile(String file){
        serverFiles.add(file);
    }
}
