package ru.rsatu.dbkursach.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.rsatu.dbkursach.db.CityDAO;
import ru.rsatu.dbkursach.db.DbManager;

import java.net.URL;
import java.util.ResourceBundle;

public class AddCityController implements Initializable {

    @FXML
    TextField cityName;

    @FXML
    TextField regionName;

    @FXML
    AnchorPane rootPane;

    CityDAO cityDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.cityDAO = new CityDAO(DbManager.getInstance());
    }

    public void addNewCity() {
        if (cityDAO.addNewCity(
                cityName.getText(),
                regionName.getText()
        )) {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();

        }
    }
}
