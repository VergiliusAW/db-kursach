package ru.rsatu.dbkursach.controller;

import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.SneakyThrows;
import ru.rsatu.dbkursach.HelloApplication;
import ru.rsatu.dbkursach.controller.common.ColorCollection;
import ru.rsatu.dbkursach.db.*;
import ru.rsatu.dbkursach.db.entity.CityView;
import ru.rsatu.dbkursach.db.entity.ConferenceView;
import ru.rsatu.dbkursach.db.entity.ScientistView;
import ru.rsatu.dbkursach.db.entity.SubjectView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ApplicationController implements Initializable {
    @FXML
    Label connectionStatusLabel;

    @FXML
    Label lastOperationStatusLabel;

    @FXML
    Label detaiLInfoLabel;

    @FXML
    ListView<String> tablesListView;

    @FXML
    TableView contentTableView;

    @FXML
    ListView<String> detailListView;

    @FXML
    VBox detailVBoxPane;

    @FXML
    Button detailDeleteButton;

    ConferenceDAO conferenceDAO;
    ScientistDAO scientistDAO;
    SubjectDAO subjectDAO;
    CityDAO cityDAO;

    @Override
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.conferenceDAO = new ConferenceDAO(DbManager.getInstance());
        this.scientistDAO = new ScientistDAO(DbManager.getInstance());
        this.subjectDAO = new SubjectDAO(DbManager.getInstance());
        this.cityDAO = new CityDAO(DbManager.getInstance());
        updateDbStatus();
        registerLastOperationStatusCallback();
        initTableListView();
    }

    private void initTableListView() {
        ObservableList<String> tables = FXCollections.observableArrayList("Конференции", "Учёные", "Научные темы", "Города");
        tablesListView.setItems(tables);
        tablesListView.setEditable(false);
        MultipleSelectionModel<String> selectionModel = tablesListView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        ChangeListener<ConferenceView> conferenceViewChangeListener = getConferenceContentTableViewChangeListener();
        ChangeListener<ScientistView> scientistViewChangeListener = getScientistContentTableViewChangeListener();
        ChangeListener<SubjectView> subjectScientistViewChangeListener = getSubjectScientistViewChangeListener();
        ChangeListener<CityView> cityConferenceViewChangeListener = getCityConferenceViewChangeListener();

        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            clearTableView(
                    conferenceViewChangeListener,
                    scientistViewChangeListener,
                    subjectScientistViewChangeListener,
                    cityConferenceViewChangeListener
            );
            switch ((String) observable.getValue()) {
                case "Конференции" -> setConferenceContentTableView(conferenceViewChangeListener);
                case "Учёные" -> setScientistContentTableView(scientistViewChangeListener);
                case "Научные темы" -> setSubjectContentTableView(subjectScientistViewChangeListener);
                case "Города" -> setCityContentTableView(cityConferenceViewChangeListener);
                default -> throw new UnsupportedOperationException();
            }
        });
    }

    private void clearTableView(ChangeListener... listeners) {
        detailListView.setItems(null);
        contentTableView.setItems(null);
        contentTableView.getColumns().clear();
        Arrays.stream(listeners).forEach(changeListener -> {
            try {
                contentTableView.getSelectionModel()
                        .selectedItemProperty().removeListener(changeListener);
            } catch (NullPointerException ignored) {
            }
        });
    }

    private void setCityContentTableView(ChangeListener<CityView> changeListener) {
        detaiLInfoLabel.setText("Конференции в городе");

        ObservableList<CityView> cityViews = FXCollections
                .observableList(cityDAO.getAllCities());

        TableView<CityView> cityViewTableView = (TableView<CityView>) contentTableView;
        cityViewTableView.setItems(cityViews);
        cityViewTableView.setEditable(true);

        cityViewTableView.getColumns().add(getTableColumn("Город", "name"));
        cityViewTableView.getColumns().add(getTableColumn("Регион", "region"));

        cityViewTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener(changeListener);


        detailDeleteButton.setOnMouseClicked(event -> {
            Integer id = cityViewTableView.getSelectionModel().getSelectedItem().getId();
            cityDAO.deleteCity(id);
            clearTableView(changeListener);
            setCityContentTableView(changeListener);
        });
    }

    private void setSubjectContentTableView(ChangeListener<SubjectView> subjectScientistViewChangeListener) {
        detaiLInfoLabel.setText("Изучается учёными");

        ObservableList<SubjectView> subjectViews = FXCollections
                .observableList(subjectDAO.getSubjectViewList());

        TableView<SubjectView> subjectViewTableView = (TableView<SubjectView>) contentTableView;
        subjectViewTableView.setItems(subjectViews);
        subjectViewTableView.setEditable(false);

        subjectViewTableView.getColumns().add(getTableColumn("Научная тематика", "name"));
        subjectViewTableView.getColumns().add(getTableColumn("Научная область", "domain"));
        subjectViewTableView.getColumns()
                .add(getTableColumn("Количество учёных", "scientist_count"));

        subjectViewTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener(subjectScientistViewChangeListener);

        detailDeleteButton.setOnMouseClicked(event -> {
            Integer id = subjectViewTableView.getSelectionModel().getSelectedItem().getId();
            subjectDAO.deleteSubject(id);
            clearTableView(subjectScientistViewChangeListener);
            setSubjectContentTableView(subjectScientistViewChangeListener);
        });
    }

    private void setScientistContentTableView(ChangeListener<ScientistView> changeListener) {
        detaiLInfoLabel.setText("Участвует в конференциях");

        ObservableList<ScientistView> scientistViews = FXCollections
                .observableList(scientistDAO.getScientistViewList());
        TableView<ScientistView> scientistViewTableView = (TableView<ScientistView>) contentTableView;
        scientistViewTableView.setItems(scientistViews);
        scientistViewTableView.setEditable(false);

        scientistViewTableView.getColumns().add(getTableColumn("Имя", "first_name"));
        scientistViewTableView.getColumns().add(getTableColumn("Фамилия", "second_name"));
        scientistViewTableView.getColumns().add(getTableColumn("Специализация", "subject"));
        scientistViewTableView.getColumns().add(getTableColumn("Город", "city"));
        scientistViewTableView.getColumns().add(getTableColumn("День рождения", "birthday"));

        scientistViewTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener(changeListener);

        detailDeleteButton.setOnMouseClicked(event -> {
            Integer id = scientistViewTableView.getSelectionModel().getSelectedItem().getId();
            scientistDAO.deleteScientist(id);
            clearTableView(changeListener);
            setScientistContentTableView(changeListener);
        });
    }

    private void setConferenceContentTableView(ChangeListener<ConferenceView> changeListener) {
        detaiLInfoLabel.setText("В конференции участвуют");

        ObservableList<ConferenceView> conferenceViews = FXCollections
                .observableList(conferenceDAO.getConferenceViewList());
        TableView<ConferenceView> conferenceViewTableView = (TableView<ConferenceView>) contentTableView;
        conferenceViewTableView.setItems(conferenceViews);

        conferenceViewTableView.setEditable(false);

        conferenceViewTableView.getColumns().add(getTableColumn("Название", "name"));
        conferenceViewTableView.getColumns().add(getTableColumn("Город", "city"));
        conferenceViewTableView.getColumns().add(getTableColumn("Тематика", "subject"));
        conferenceViewTableView.getColumns().add(getTableColumn("Начало", "start_date"));
        conferenceViewTableView.getColumns().add(getTableColumn("Конец", "finish_date"));

        //Если changeListener значит дальше не продолжаем
        if (changeListener == null) {
            return;
        }
        conferenceViewTableView.getSelectionModel().selectedItemProperty().addListener(changeListener);

        detailDeleteButton.setOnMouseClicked(event -> {
            Integer id = conferenceViewTableView.getSelectionModel().getSelectedItem().getId();
            conferenceDAO.deleteConference(id);
            clearTableView(changeListener);
            setConferenceContentTableView(changeListener);
        });

//        Button register = new Button("Подать заявку");
//        register.setMinWidth(216);
//        register.setPadding(new Insets(10));
//        VBox.setMargin(register, new Insets(10));
//        detailVBoxPane.getChildren().add(2, register);
    }

    private ChangeListener<ConferenceView> getConferenceContentTableViewChangeListener() {
        return ((observable, oldValue, newValue) -> {
            ConferenceView conferenceView = observable.getValue();

            if (conferenceView == null) {
                detailListView.setItems(null);
                return;
            }

            ObservableList<String> conferences = FXCollections.observableArrayList(
                    scientistDAO.getScientistViewsForConference(conferenceView.getId())
                            .stream()
                            .map(s -> String.format("%s %s", s.getFirst_name(), s.getSecond_name()))
                            .toList()
            );
            detailListView.setEditable(false);
            detailListView.setItems(conferences);
        });
    }

    private ChangeListener<ScientistView> getScientistContentTableViewChangeListener() {
        return ((observable, oldValue, newValue) -> {
            ScientistView scientistView = observable.getValue();

            if (scientistView == null) {
                detailListView.setItems(null);
                return;
            }

            ObservableList<String> conferences = FXCollections.observableArrayList(
                    conferenceDAO.getConferenceViewsForScientist(scientistView.getId())
                            .stream()
                            .map(ConferenceView::getName)
                            .toList()
            );
            detailListView.setEditable(false);
            detailListView.setItems(conferences);
        });
    }

    private ChangeListener<CityView> getCityConferenceViewChangeListener() {
        return ((observable, oldValue, newValue) -> {
            CityView cityView = observable.getValue();
            if (cityView == null) {
                detailListView.setItems(null);
                return;
            }

            ObservableList<String> conferences = FXCollections.observableList(
                    conferenceDAO.getConferenceViewsForCity(cityView.getId())
                            .stream()
                            .map(c -> String.format("%s, %s - %s", c.getName(), c.getStart_date(), c.getFinish_date()))
                            .toList()
            );
            detailListView.setEditable(false);
            detailListView.setItems(conferences);
        });
    }

    private ChangeListener<SubjectView> getSubjectScientistViewChangeListener() {
        return (observable, oldValue, newValue) -> {
            SubjectView subjectView = observable.getValue();

            if (subjectView == null) {
                detailListView.setItems(null);
                return;
            }

            ObservableList<String> scientists = FXCollections.observableArrayList(
                    scientistDAO.getScientistViewsForSubject(subjectView.getId())
                            .stream()
                            .map(s -> String.format("%s %s", s.getFirst_name(), s.getSecond_name()))
                            .toList()
            );
            detailListView.setEditable(false);
            detailListView.setItems(scientists);
        };
    }

    private <V> TableColumn<V, String> getTableColumn(String columnName, String propertyName) {
        TableColumn<V, String> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        return column;
    }

    @FXML
    public void openAddNewConferenceWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(HelloApplication.class.getResource("add-conference.fxml"));


            Scene scene = new Scene(fxmlLoader.load(), 430, 470);
            Stage stage = new Stage();
            stage.setTitle("Добавление новой конференции");

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @FXML
    public void openAddNewScientistWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(HelloApplication.class.getResource("add-scientist.fxml"));


            Scene scene = new Scene(fxmlLoader.load(), 430, 470);
            Stage stage = new Stage();
            stage.setTitle("Добавление нового учёного");

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @FXML
    public void openAddNewCityWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(HelloApplication.class.getResource("add-city.fxml"));


            Scene scene = new Scene(fxmlLoader.load(), 430, 200);
            Stage stage = new Stage();
            stage.setTitle("Добавление нового города");

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @FXML
    public void openAddNewSubjectWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(HelloApplication.class.getResource("add-subject.fxml"));


            Scene scene = new Scene(fxmlLoader.load(), 430, 200);
            Stage stage = new Stage();
            stage.setTitle("Добавление новой научной тематики");

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @FXML
    public void openAddNewParticipantWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(HelloApplication.class.getResource("add-participant.fxml"));


            Scene scene = new Scene(fxmlLoader.load(), 430, 250);
            Stage stage = new Stage();
            stage.setTitle("Приём заявки от учёного на участие в конференции");

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @FXML
    public void openChartScientistCities() {
        try {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableList(cityDAO
                    .getCityScientistsViews()
                    .stream()
                    .map(c -> new PieChart.Data(c.getCity(), c.getScientist_count()))
                    .toList());

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(HelloApplication.class.getResource("chart.fxml"));
            fxmlLoader.setController(new ChartController(pieChartData));

            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = new Stage();
            stage.setTitle("Диаграмма распределения учёных по городам");

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @FXML
    @SneakyThrows
    public void exportToExcel() {
        Workbook workbook = new Workbook();
        Worksheet worksheet = workbook.getWorksheets().get(0);

        List<ConferenceView> conferenceViews = conferenceDAO.getConferenceViewList();

        System.out.println(conferenceViews.size());

        int row = 0;
        ArrayList<String> headers = new ArrayList<>(List.of(
                "ID",
                "Название конференции",
                "Тематика",
                "Город проведения",
                "Начало",
                "Конец"));
        worksheet.getCells().importArrayList(headers, row++, 0, false);
        for (ConferenceView view : conferenceViews) {
            ArrayList<String> list = new ArrayList<>(List.of(
                    view.getId().toString(),
                    view.getName(),
                    view.getSubject(),
                    view.getCity(),
                    view.getStart_date(),
                    view.getFinish_date()));
            worksheet.getCells().importArrayList(list, row++, 0, false);
        }

//        worksheet.getCells().importCustomObjects((Collection) conferenceViews,
////                new String[]{"ID", "Название конференции", "Город проведения", "Тематика", "Начало", "Конец"},
//                null,
//                true,
//                0,
//                0,
//                conferenceViews.size(),
//                true,
//                null,
//                false);

        workbook.save("./output.xlsx");
    }

    @FXML
    public void exitApplication() {
        Platform.exit();
    }

    @FXML
    public void showAbout() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("О программе");
        a.setTitle("Научные конференции");
        a.setContentText("""
                В различных городах планируется проведение научных конференций по различным тематикам.
                Ученые, живущие в разных городах, участвуют в этих конференциях.
                Каждый ученый проводит исследования по определенной научной тематике и может участвовать в работе нескольких конференций.
                Каждая конференция имеет строго определенную научную тематику.
                """);
        a.show();
    }

    private void registerLastOperationStatusCallback() {
        Callback<DbStatus, DbStatus> lastOperationStatusCallback = (status) -> {
            switch (status) {
                case Ok -> {
                    lastOperationStatusLabel.setText("Ok");
                    lastOperationStatusLabel.setTextFill(ColorCollection.greenOk());
                }
                case FAILED -> {
                    lastOperationStatusLabel.setText("Failed");
                    lastOperationStatusLabel.setTextFill(ColorCollection.redFailed());
                }
            }
            return status;
        };
        DbManager.getInstance().registerLastOperationStatusCallback(lastOperationStatusCallback);
    }

    private void updateDbStatus() {
        String dbUrl = DbManager.getInstance().getDbUrl();
        if (dbUrl.isEmpty()) {
            setConnectionFailedDbStatus();
            return;
        }
        setConnectionEstablishDbStatus(dbUrl);
    }

    private void setConnectionEstablishDbStatus(String url) {
        connectionStatusLabel.setText(String.format("Connection establish %s", url));
        connectionStatusLabel.setTextFill(ColorCollection.greenOk());
    }

    private void setConnectionFailedDbStatus() {
        connectionStatusLabel.setText("Connection failed");
        connectionStatusLabel.setTextFill(ColorCollection.redFailed());
    }
}
