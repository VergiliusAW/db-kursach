package ru.rsatu.dbkursach;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.rsatu.dbkursach.db.DbManager;
import ru.rsatu.dbkursach.db.SubjectDAO;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        DbManager dbManager = DbManager.getInstance();
//        SubjectDAO subjectDAO = new SubjectDAO(dbManager);
//        subjectDAO.printSubjectTable();
//        System.out.println(subjectDAO.getAllSubjects());

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("application.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1250, 550);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void stop() {
        DbManager.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        System.setProperty("jdbc.url", "jdbc:mysql://localhost:3306/conference_schema");
        System.setProperty("jdbc.user", "root");
        System.setProperty("jdbc.password", "root");
        launch();
    }
}