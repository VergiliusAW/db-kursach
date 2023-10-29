package ru.rsatu.dbkursach.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.rsatu.dbkursach.db.ConferenceDAO;
import ru.rsatu.dbkursach.db.DbManager;
import ru.rsatu.dbkursach.db.ParticipantDAO;
import ru.rsatu.dbkursach.db.ScientistDAO;
import ru.rsatu.dbkursach.db.entity.ConferenceView;
import ru.rsatu.dbkursach.db.entity.ScientistView;

import java.net.URL;
import java.util.ResourceBundle;

public class AddParticipantController implements Initializable {

    @FXML
    ComboBox<ScientistView> scientistDropdown;

    @FXML
    ComboBox<ConferenceView> conferenceDropdown;

    @FXML
    TextField countReports;

    @FXML
    AnchorPane rootPane;

    private ConferenceDAO conferenceDAO;
    private ScientistDAO scientistDAO;
    private ParticipantDAO participantDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.conferenceDAO = new ConferenceDAO(DbManager.getInstance());
        this.scientistDAO = new ScientistDAO(DbManager.getInstance());
        this.participantDAO = new ParticipantDAO(DbManager.getInstance());
        loadScientists();
    }

    @FXML
    public void addNewParticipant() {
        if (participantDAO.registerScientistToConference(
                scientistDropdown.getSelectionModel().getSelectedItem().getId(),
                conferenceDropdown.getSelectionModel().getSelectedItem().getId(),
                Integer.valueOf(countReports.getText())
        )) {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
        }
    }

    private void loadScientists() {
        ObservableList<ScientistView> scientistViews = FXCollections.observableList(scientistDAO.getScientistViewList());
        scientistDropdown.setItems(scientistViews);
        if (!scientistViews.isEmpty()) {
            scientistDropdown.setValue(scientistViews.get(0));
        }
        scientistDropdown.setOnAction(event -> {
            loadConferences(scientistDropdown.getSelectionModel().getSelectedItem().getId());
        });
    }

    private void loadConferences(Integer id) {
        ObservableList<ConferenceView> conferenceViews = FXCollections
                .observableList(conferenceDAO.getAvailableConferencesForScientist(id));
        conferenceDropdown.setItems(conferenceViews);
        if (!conferenceViews.isEmpty()) {
            conferenceDropdown.setValue(conferenceViews.get(0));
        }
    }
}
