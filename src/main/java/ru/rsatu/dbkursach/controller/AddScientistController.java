package ru.rsatu.dbkursach.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ru.rsatu.dbkursach.db.CityDAO;
import ru.rsatu.dbkursach.db.DbManager;
import ru.rsatu.dbkursach.db.ScientistDAO;
import ru.rsatu.dbkursach.db.SubjectDAO;
import ru.rsatu.dbkursach.db.entity.CityView;
import ru.rsatu.dbkursach.db.entity.SubjectView;

import java.net.URL;
import java.util.ResourceBundle;

public class AddScientistController implements Initializable {

    @FXML
    TextField scientistFirstName;

    @FXML
    TextField scientistSecondName;

    @FXML
    DatePicker birthday;

    @FXML
    ComboBox<CityView> cityDropdown;

    @FXML
    ComboBox<SubjectView> subjectDropdown;

    @FXML
    Button addScientistButton;

    @FXML
    HBox rootBox;

    ScientistDAO scientistDAO;

    SubjectDAO subjectDAO;

    CityDAO cityDAO;

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.scientistDAO = new ScientistDAO(DbManager.getInstance());
        this.subjectDAO = new SubjectDAO(DbManager.getInstance());
        this.cityDAO = new CityDAO(DbManager.getInstance());
        loadCities();
        loadSubjects();
    }

    public void addNewScientist() {
        if (scientistDAO.addNewScientist(
                cityDropdown.getSelectionModel().getSelectedItem().getId(),
                subjectDropdown.getSelectionModel().getSelectedItem().getId(),
                scientistFirstName.getText(),
                scientistSecondName.getText(),
                birthday.getValue()
        )) {
            Stage stage = (Stage) rootBox.getScene().getWindow();
            stage.close();
        }
    }

    private void loadSubjects() {
        ObservableList<SubjectView> subjectViews = FXCollections.observableList(subjectDAO.getSubjectViewList());
        subjectDropdown.setItems(subjectViews);
        if (!subjectViews.isEmpty()) {
            subjectDropdown.setValue(subjectViews.get(0));
        }
    }

    private void loadCities() {
        ObservableList<CityView> cityViews = FXCollections.observableList(cityDAO.getAllCities());
        cityDropdown.setItems(cityViews);
        if (!cityViews.isEmpty()) {
            CityView item = cityDropdown.getItems().get(0);
            cityDropdown.setValue(item);
        }
    }
}
