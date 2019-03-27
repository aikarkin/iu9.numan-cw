package ru.bmstu.iu9.numan.cw.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ru.bmstu.iu9.numan.cw.FxWindows;
import ru.bmstu.iu9.numan.cw.task.OptimizationTask;

public class ConfigurationManager {

    @FXML
    private TextField m1Field;
    @FXML
    private TextField b1Field;
    @FXML
    private TextField c1Field;
    @FXML
    private TextField m2Field;
    @FXML
    private TextField b2Field;
    @FXML
    private TextField c2Field;
    @FXML
    private TextField fAField;
    @FXML
    private TextField omegaField;
    @FXML
    private TextField alphaField;
    @FXML
    private TextField xi2MaxField;

    @FXML
    private TextField m1MinField;
    @FXML
    private TextField m1MaxField;
    @FXML
    private TextField m2MinField;
    @FXML
    private TextField m2MaxField;
    @FXML
    private TextField b1MinField;
    @FXML
    private TextField b1MaxField;
    @FXML
    private TextField b2MinField;
    @FXML
    private TextField b2MaxField;
    @FXML
    private TextField c1MinField;
    @FXML
    private TextField c1MaxField;
    @FXML
    private TextField c2MinField;
    @FXML
    private TextField c2MaxField;
    @FXML
    private TextField xi2MinField;
    @FXML
    private ProgressIndicator progressSpinner;
    @FXML
    private GridPane mainPane;

    @FXML
    public void onOptimizeClicked() {
        OptimizationTask optimizationTask = new OptimizationTask(this);
        Thread optimizationThread = new Thread(optimizationTask);

        progressSpinner.progressProperty().bind(optimizationTask.progressProperty());
        startProgress();
        optimizationThread.start();

        optimizationTask.setOnSucceeded(event -> {
            FxWindows.showOptimizationResult(optimizationTask.getValue());
            stopProgress();
        });
    }

    private void startProgress() {
        System.out.println("Starting process ...");
        progressSpinner.toFront();
        mainPane.setDisable(true);
        progressSpinner.setVisible(true);
        progressSpinner.setDisable(false);
    }

    private void stopProgress() {
        mainPane.setDisable(false);
        progressSpinner.setDisable(true);
        progressSpinner.setVisible(false);
        System.out.println("Process stopped.");
    }

    public double m1() {
        return getFieldValue(m1Field);
    }

    public double b1() {
        return getFieldValue(b1Field);
    }

    public double c1() {
        return getFieldValue(c1Field);
    }

    public double m2() {
        return getFieldValue(m2Field);
    }

    public double b2() {
        return getFieldValue(b2Field);
    }

    public double c2() {
        return getFieldValue(c2Field);
    }

    public double fA() {
        return getFieldValue(fAField);
    }

    public double omega() {
        return getFieldValue(omegaField);
    }

    public double alpha() {
        return getFieldValue(alphaField);
    }

    public double m1Min() {
        return getFieldValue(m1MinField);
    }

    public double m1Max() {
        return getFieldValue(m1MaxField);
    }

    public double m2Min() {
        return getFieldValue(m2MinField);
    }

    public double m2Max() {
        return getFieldValue(m2MaxField);
    }

    public double b1Min() {
        return getFieldValue(b1MinField);
    }

    public double b1Max() {
        return getFieldValue(b1MaxField);
    }

    public double b2Min() {
        return getFieldValue(b2MinField);
    }

    public double b2Max() {
        return getFieldValue(b2MaxField);
    }

    public double c1Min() {
        return getFieldValue(c1MinField);
    }

    public double c1Max() {
        return getFieldValue(c1MaxField);
    }

    public double c2Min() {
        return getFieldValue(c2MinField);
    }

    public double c2Max() {
        return getFieldValue(c2MaxField);
    }

    public double xi2Max() {
        return getFieldValue(xi2MaxField);
    }

    public double xi2Min() {
        return getFieldValue(xi2MinField);
    }

    private static double getFieldValue(TextField textField) {
        return Double.valueOf(textField.getText());
    }

}
