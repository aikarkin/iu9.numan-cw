package ru.bmstu.iu9.numan.cw;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import ru.bmstu.iu9.numan.commons.rk.RungeKuttaAlgo;
import ru.bmstu.iu9.numan.fx.ChartType;
import ru.bmstu.iu9.numan.fx.EnumChoiceBox;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.DoubleStream;

import static java.lang.Math.min;
import static ru.bmstu.iu9.numan.cw.DynamicDamperUtils.getDeRhsVec;
import static ru.bmstu.iu9.numan.cw.Const.Charts;

public class FxWindows {
    public static void showConfigurationWindow() throws IOException {
        Stage stage = createStage(
                "Параметры динамического гасителя колебаний",
                "layouts/params.fxml",
                false,
                "const"
        );
        stage.show();
    }

    @SuppressWarnings("unchecked")
    public static void showOptimizationResult(Map<String, Object> fields){
        try {
            Stage stage = createStage(
                    "Реузльтат",
                    "layouts/result.fxml",
                    true,
                    "const"
            );
            stage.show();
            setFields(stage, fields);
            // select first item
            Scene scene = stage.getScene();
            EnumChoiceBox<ChartType> chartChoice = (EnumChoiceBox<ChartType>)scene.lookup("#chartTypeField");
            chartChoice.getSelectionModel().selectFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCharts(ChartType chartType, RealVector startParams, RealVector optimalParams, double omega, double alpha, double fA) throws IOException {
        Stage stage = createStage(
                "График: " + chartType.label(),
                "layouts/charts.fxml",
                true,
                "const"
        );
        stage.show();
        Scene scene = stage.getScene();
        drawChartsWithRungeKuttaMethod(omega, alpha, fA, scene, chartType, startParams, optimalParams);
    }

    @SuppressWarnings("unchecked")
    private static void drawChartsWithRungeKuttaMethod(double omega, double alpha, double fA, Scene scene, ChartType chartType, RealVector startParams, RealVector optimalParams) {
        ScatterChart<Number, Number> chart = (ScatterChart<Number, Number>) scene.lookup("#chart");
        Function<RealVector, List<RealVector>> solutionFor = (params) ->  RungeKuttaAlgo.rungeKutta(
                (t, x) -> getDeRhsVec(t, omega, alpha, fA, params, x),
                (time, prevVec, curVec) -> time - Charts.DURATION >= 0.0001,
                new ArrayRealVector(4, 0.0),
                Charts.TIME_STEP
        );

        List<RealVector> optimalSolution = solutionFor.apply(optimalParams);
        List<RealVector> startSolution = solutionFor.apply(startParams);

        int dotsCount = min(optimalSolution.size(), startSolution.size());
        double[] x = new double[dotsCount];
        double[] yMin = new double[dotsCount];
        double[] yStart = new double[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            x[i] = Charts.TIME_STEP * i;
            yMin[i] = optimalSolution.get(i).getEntry(chartType.ordinal());
            yStart[i] = startSolution.get(i).getEntry(chartType.ordinal());
        }

        addDataSeries(chart, String.format("%s (начальные параметры)", chartType.label()), x, yStart);
        addDataSeries(chart, String.format("%s (оптимальные параметры)", chartType.label()), x, yMin);

        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();

        xAxis.setLabel(chartType.xAxis());
        xAxis.setTickLabelRotation(-90.0);
        xAxis.setTickLabelGap(0.01);
        xAxis.setTickUnit(0.025);
        xAxis.setLowerBound(0.0);
        xAxis.setUpperBound(Charts.DURATION);

        yAxis.setLabel(chartType.yAxis());
        yAxis.setTickLabelGap(0.05);
        yAxis.setTickUnit(0.05);
        setAxisRange(yAxis, yStart);
    }

    private static void setAxisRange(NumberAxis axis, double[] axisValues) {
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

    private static void addDataSeries(ScatterChart<Number, Number> chart, String chartLabel, double[] x, double[] y) {
        XYChart.Series<Number, Number> dataSeries = new XYChart.Series<>();

        for (int i = 0; i < min(x.length, y.length); i++) {
            dataSeries.getData().add(new XYChart.Data<>(x[i], y[i]));
        }

        dataSeries.setName(chartLabel);
        chart.getData().add(dataSeries);
    }

    private static void setFields(Stage stage, Map<String, Object> fields) {
        Scene scene = stage.getScene();

        for (Map.Entry<String, Object> fieldEntry : fields.entrySet()) {
            String selector = "#" + fieldEntry.getKey();
            Node node = scene.lookup(selector);
            String stringValue = String.valueOf(fieldEntry.getValue());
            if (node != null) {
                if (node.getClass() == TextField.class) {
                    ((TextField) node).setText(stringValue);
                } else if (node.getClass() == Label.class) {
                    ((Label) node).setText(stringValue);
                }
            }
        }
    }

    private static Stage createStage(String title, String layoutPath, boolean resizable, String bundleName) throws IOException {
        ClassLoader classLoader = FxApplication.class.getClassLoader();
        final URL fxmlRes = classLoader.getResource(layoutPath);

        if (fxmlRes == null) {
            throw new RuntimeException("Не возможно открыть fxml файл: " + layoutPath);
        }

        Parent root = bundleName == null ? FXMLLoader.load(fxmlRes) : FXMLLoader.load(fxmlRes, ResourceBundle.getBundle(bundleName));
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setResizable(resizable);

        Scene scene = new Scene(root, -1, -1);
        scene.getStylesheets().add("styles.css");

        stage.setScene(scene);

        return stage;
    }

}
