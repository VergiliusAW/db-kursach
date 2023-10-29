package ru.rsatu.dbkursach.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;

import java.net.URL;
import java.util.ResourceBundle;

public class ChartController implements Initializable {

    @FXML
    PieChart chart;

    ObservableList<PieChart.Data> pieChartData;

    public ChartController(ObservableList<PieChart.Data> pieChartData) {
        this.pieChartData = pieChartData;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        chart.setData(pieChartData);
    }
}
