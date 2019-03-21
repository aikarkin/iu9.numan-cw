package ru.bmstu.iu9.numan.cw.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import ru.bmstu.iu9.numan.cw.FxWindows;
import ru.bmstu.iu9.numan.cw.task.ChartDrawingTask;
import ru.bmstu.iu9.numan.fx.ChartType;
import ru.bmstu.iu9.numan.fx.EnumChoiceBox;

import java.io.IOException;

public class OptimizationResultsManager {

    @FXML
    private Label m1Value;
    @FXML
    private Label c1Value;
    @FXML
    private Label b1Value;
    @FXML
    private Label m2Value;
    @FXML
    private Label c2Value;
    @FXML
    private Label b2Value;
    @FXML
    private Label m1OptValue;
    @FXML
    private Label c1OptValue;
    @FXML
    private Label b1OptValue;
    @FXML
    private Label m2OptValue;
    @FXML
    private Label c2OptValue;
    @FXML
    private Label b2OptValue;

    @FXML
    private ConfigurationManager paramsManagerController;
    @FXML
    private EnumChoiceBox<ChartType> chartTypeField;
    //    @FXML
//    private ProgressIndicator chartProgress;
    @FXML
    private GridPane mainPane;
    @FXML
    private Button displayChartBtn;

    @FXML
    public void onShowChartsClicked() {
        try {
            FxWindows.showCharts(
                    chartType(),
                    startParams(),
                    optimalParams(),
                    omega(),
                    alpha(),
                    fA()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double omega() {
        return paramsManagerController.omega();
    }

    public double alpha() {
        return paramsManagerController.alpha();
    }

    public double fA() {
        return paramsManagerController.fA();
    }

    public ChartType chartType() {
        return chartTypeField.getValue();
    }

    public RealVector startParams() {
        return labelsToVector(m1Value, c1Value, b1Value, m2Value, c2Value, b2Value);
    }

    public RealVector optimalParams() {
        return labelsToVector(m1OptValue, c1OptValue, b1OptValue, m2OptValue, c2OptValue, b2OptValue);
    }

    private RealVector labelsToVector(Label... labels) {
        RealVector vector = new ArrayRealVector(labels.length);
        for (int i = 0; i < labels.length; i++) {
            vector.setEntry(i, Double.valueOf(labels[i].getText()));
        }

        return vector;
    }

}
