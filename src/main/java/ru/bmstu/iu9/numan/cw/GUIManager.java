package ru.bmstu.iu9.numan.cw;

import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import ru.bmstu.iu9.numan.commons.optimization.PenaltyMethod;
import ru.bmstu.iu9.numan.commons.rk.ParamEq;
import ru.bmstu.iu9.numan.commons.rk.RungeKuttaAlgo;
import ru.bmstu.iu9.numan.fx.ChartType;
import ru.bmstu.iu9.numan.fx.EnumChoiceBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.stream.DoubleStream;

import static java.lang.Math.*;
import static ru.bmstu.iu9.numan.cw.Const.PatternSearch.*;
import static ru.bmstu.iu9.numan.cw.Const.RungeKutta.*;
import static ru.bmstu.iu9.numan.cw.Const.DynamicVibrationDamper.*;

public class GUIManager {

    @FXML
    private ScatterChart<Number, Number> chart;
    @FXML
    private TextField blockElasticityField;
    @FXML
    private TextField platformElasticityField;
    @FXML
    private TextField blockDampingField;
    @FXML
    private TextField frequencyField;
    @FXML
    private TextField platformDampingField;
    @FXML
    private TextField blockMassField;
    @FXML
    private TextField platformMassField;
    @FXML
    private TextField phaseShiftField;
    @FXML
    private TextField amplitudeForceField;
    @FXML
    private TextField durationField;
    @FXML
    private EnumChoiceBox<ChartType> chartTypeField;

    private RealVector startVec;


    @FXML
    public void initialize() {
        System.out.println("initialize ...");
        startVec = new ArrayRealVector(new double[]{
                // omega (0)
                OMEGA_0,
                // alpha (1)
                ALPHA_0,
                // m1 (2)
                M1_0,
                // c1 (3)
                C1_0,
                // b1 (4)
                B1_0,
                // m2 (5)
                M2_0,
                // c2 (6)
                C2_0,
                // b2 (7)
                B2_0,
                // f  (8)
                F_0,
        });
        setFields(startVec);
    }


    @FXML
    public void onShowClicked() {
        removeChart();
        addChart(getFieldValue(chartTypeField).label(), DT, true);
    }

    @FXML
    public void onOptimizeClicked() {
        Function<RealVector, RealVector> findVecWithMaxCoord = (paramVec) -> {
            ParamEq sdeRhs = (t, x) -> getDeRhsVec(t, paramVec, x);

            List<RealVector> rkSol = RungeKuttaAlgo.rungeKutta(
                    sdeRhs,
                    (time, prevVec, curVec) -> time - DURATION >= 0.0001,
                    new ArrayRealVector(4, 0.0)
            );

            double maxFuncVal = 0.0;
            RealVector platformCoord = rkSol.get(0);

            for (RealVector vec : rkSol) {
                if (abs(vec.getEntry(0)) > maxFuncVal) {
                    maxFuncVal = abs(vec.getEntry(0));
                    platformCoord = vec;
                }
            }

            return platformCoord;
        };

        List<Function<RealVector, Double>> constraints = Arrays.asList(
                // omega:
                x -> -x.getEntry(0) + omegaMin,
                x -> x.getEntry(0) - omegaMax,
                // alpha:
                x -> -x.getEntry(1) + alphaMin,
                x -> x.getEntry(1) - alphaMax,
                // m1
                x -> -x.getEntry(2) + m1Min,
                x -> x.getEntry(2) - m1Max,
                // c1
                x -> -x.getEntry(3) + c1Min,
                x -> x.getEntry(3) - c1Max,
                // b1
                x -> -x.getEntry(4) + b1Min,
                x -> x.getEntry(4) - b1Max,
                // m2
                x -> -x.getEntry(5) + m2Min,
                x -> x.getEntry(5) - m2Max,
                // c2
                x -> -x.getEntry(6) + c2Min,
                x -> x.getEntry(6) - c2Max,
                // b2
                x -> -x.getEntry(7) + b2Min,
                x -> x.getEntry(7) - b2Max,
                // f
                x -> -x.getEntry(8) + fMin,
                x -> x.getEntry(8) - fMax,
                // xi2
                x -> findVecWithMaxCoord.apply(x).getEntry(1) - xi2Max
        );

        double[] coordSteps = new double[]{
                OMEGA_STEP,
                ALPHA_STEP,
                M1_STEP,
                C1_STEP,
                B1_STEP,
                M2_STEP,
                C2_STEP,
                B2_STEP,
                F_STEP
        };

        ArrayList<Double> weights = new ArrayList<>();
        for (int i = 0; i < constraints.size(); i++) {
            weights.add(1.5);
        }

        weights.set(constraints.size() - 1, 1.0);

        RealVector optimalParams = PenaltyMethod.optimizeWithPenaltyMethod(
                (vec) -> abs(findVecWithMaxCoord.apply(vec).getEntry(0)),
                constraints,
                weights,
                coordSteps,
                startVec
        );

        setFields(optimalParams);

        addChart(getFieldValue(chartTypeField).label() + " (после оптимизации)", DT, false);
    }

    private void setFields(RealVector params) {
        frequencyField.setText(String.valueOf(params.getEntry(0)));
        phaseShiftField.setText(String.valueOf(params.getEntry(1)));
        platformMassField.setText(String.valueOf(params.getEntry(2)));
        platformElasticityField.setText(String.valueOf(params.getEntry(3)));
        platformDampingField.setText(String.valueOf(params.getEntry(4)));
        blockMassField.setText(String.valueOf(params.getEntry(5)));
        blockElasticityField.setText(String.valueOf(params.getEntry(6)));
        blockDampingField.setText(String.valueOf(params.getEntry(7)));
        amplitudeForceField.setText(String.valueOf(params.getEntry(8)));
    }

    private void removeChart() {
        chart.getData().clear();
    }

    @SuppressWarnings("Duplicates")
    private void addChart(String label, double dt, boolean changeChartUnits) {
        try {
            ChartType chartType = getFieldValue(chartTypeField);
            double duration = getFieldValue(durationField);

            List<RealVector> solVectorsList = RungeKuttaAlgo.rungeKutta(
                    getDeRhsFromFields(),
                    (time, prevVec, curVec) -> time - duration >= 0.0001,
                    new ArrayRealVector(4, 0.0)
            );
            int dotsCount = solVectorsList.size();

//            printFunctionPeaks(chartType.ordinal(), solVectorsList);

            double[] x = new double[dotsCount];
            double[] y = new double[dotsCount];

            for (int i = 0; i < dotsCount; i++) {
                x[i] = dt * i;
                y[i] = solVectorsList.get(i).getEntry(chartType.ordinal());
            }

            addDataSeries(label == null ? chartType.label() : label, chartType.xAxis(), chartType.yAxis(), x, y, changeChartUnits);
        } catch (InvalidFieldException e) {
            e.printStackTrace();
            showError("Некорректно заполнено поле", e.getMessage());
        }
    }

    private void addDataSeries(String chartLabel, String xAxisLabel, String yAxisLabel, double[] x, double[] y, boolean changeChartUnits) {
        XYChart.Series<Number, Number> dataSeries = new XYChart.Series<>();

        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();

        for (int i = 0; i < min(x.length, y.length); i++) {
            dataSeries.getData().add(new XYChart.Data<>(x[i], y[i]));
        }

        dataSeries.setName(chartLabel);


        if(changeChartUnits) {
            xAxis.setTickLabelRotation(-90.0);
            xAxis.setTickLabelGap(0.01);
            xAxis.setTickUnit(0.025);

            yAxis.setTickLabelGap(0.05);
            yAxis.setTickUnit(0.05);
            setAxisInfo(xAxisLabel, xAxis, x, true);
            setAxisInfo(yAxisLabel, yAxis, y, true);
        }

        chart.getData().add(dataSeries);
    }

    private void setAxisInfo(String label, NumberAxis axis, double[] axisValues, boolean changeChartUnits) {
        axis.setLabel(label);

        if(changeChartUnits) {
            DoubleStream axisValuesStream = Arrays.stream(axisValues);
            OptionalDouble minOpt = axisValuesStream.min();
            axisValuesStream = Arrays.stream(axisValues);

            OptionalDouble maxOpt = axisValuesStream.max();
            if (minOpt.isPresent()) {
                axis.setLowerBound(minOpt.getAsDouble());
            }
            if (maxOpt.isPresent()) {
                axis.setUpperBound(maxOpt.getAsDouble());
            }
            axis.setAutoRanging(false);
            axis.setMinorTickVisible(false);
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private ParamEq getDeRhsFromFields() {
        RealVector paramVec = MatrixUtils.createRealVector(new double[]{
                getFieldValue(frequencyField),
                getFieldValue(phaseShiftField),
                getFieldValue(platformMassField),
                getFieldValue(platformElasticityField),
                getFieldValue(platformDampingField),
                getFieldValue(blockMassField),
                getFieldValue(blockElasticityField),
                getFieldValue(blockDampingField),
                getFieldValue(amplitudeForceField),
        });

        return (t, x) -> getDeRhsVec(t, paramVec, x);
    }

    private static void printFunctionPeaks(int chartType, List<RealVector> solVectorsList) {
        double left, middle, right;
        for (int i = 2; i < solVectorsList.size(); i++) {
            left = solVectorsList.get(i - 2).getEntry(chartType);
            middle = solVectorsList.get(i - 1).getEntry(chartType);
            right = solVectorsList.get(i).getEntry(chartType);

            if (middle > left && middle < right) {
                System.out.printf("y[%d](%.3f) = %.3f%n", i - 1, (i - 1) * DT, middle);
            }

        }
    }

    private static double getFieldValue(TextField textField) {
        try {
            return Double.valueOf(textField.getText());
        } catch (NumberFormatException nfe) {
            throw new InvalidFieldException(textField);
        }
    }

    private static ChartType getFieldValue(EnumChoiceBox<ChartType> chartChoiceBox) {
        return chartChoiceBox.getValue();
    }

    private static RealVector getDeRhsVec(
            double t,
            RealVector paramVec,
            RealVector x) {
        double eta1, eta2, xi1, xi2;
        double omega, alpha, m1, c1, b1, m2, c2, b2, f, m, dXi;

        omega = paramVec.getEntry(0);
        alpha = paramVec.getEntry(1);
        m1 = paramVec.getEntry(2);
        c1 = paramVec.getEntry(3);
        b1 = paramVec.getEntry(4);
        m2 = paramVec.getEntry(5);
        c2 = paramVec.getEntry(6);
        b2 = paramVec.getEntry(7);
        f = paramVec.getEntry(8);

        xi1 = x.getEntry(0);
        xi2 = x.getEntry(1);
        eta1 = x.getEntry(2);
        eta2 = x.getEntry(3);
        m = m1 + m2;
        dXi = xi2 - xi1;

        return new ArrayRealVector(new double[]{
                eta1,
                eta2,
                (2 * b1 * eta1 - 2 * c1 * xi1 + c2 * dXi + f * cos(omega * t + alpha)) / m,
                (b2 * eta2 - c2 * dXi) / m2
        });
    }

}
