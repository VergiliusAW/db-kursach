package ru.rsatu.dbkursach.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.rsatu.dbkursach.db.DbManager;
import ru.rsatu.dbkursach.db.DomainDAO;
import ru.rsatu.dbkursach.db.SubjectDAO;
import ru.rsatu.dbkursach.db.entity.DomainView;

import java.net.URL;
import java.util.ResourceBundle;

public class AddSubjectController implements Initializable {

    @FXML
    TextField subjectName;

    @FXML
    ComboBox<DomainView> domainDropdown;

    @FXML
    AnchorPane rootPane;

    private DomainDAO domainDAO;

    private SubjectDAO subjectDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.domainDAO = new DomainDAO(DbManager.getInstance());
        this.subjectDAO = new SubjectDAO(DbManager.getInstance());
        loadDomains();
    }

    public void addNewSubject() {
        if (subjectDAO.addNewSubject(
                subjectName.getText(),
                domainDropdown.getSelectionModel().getSelectedItem().getId()
        )) {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
        }
    }

    private void loadDomains() {
        ObservableList<DomainView> domainViews = FXCollections.observableList(domainDAO.getDomainViewList());
        domainDropdown.setItems(domainViews);
        if (!domainViews.isEmpty()) {
            domainDropdown.setValue(domainViews.get(0));
        }
    }
}
