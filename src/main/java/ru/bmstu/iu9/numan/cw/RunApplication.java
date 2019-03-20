package ru.bmstu.iu9.numan.cw;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class RunApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        final URL fxmlRes = getClass().getClassLoader().getResource("params-configurer.fxml");

        if(fxmlRes == null) {
            System.out.println("[error] Failed to start GUI - params-configurer.xml not found");
            return;
        }

        Parent root = FXMLLoader.load(fxmlRes);
//        primaryStage.setTitle("График зависимости координаты платформы от времени (x1 = f(t))");
        primaryStage.setResizable(false);
        Scene scene = new Scene(root, -1, -1);
        scene.getStylesheets().add("styles.css");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showChartsWindow(Stage stage) throws IOException {
        final URL fxmlRes = getClass().getClassLoader().getResource("charts.fxml");

        if(fxmlRes == null) {
            System.out.println("[error] Failed to start GUI - layout.xml not found");
            return;
        }

        Parent root = FXMLLoader.load(fxmlRes);
        stage.setTitle("График зависимости координаты платформы от времени (x1 = f(t))");
        stage.setResizable(false);
        Scene scene = new Scene(root, -1, -1);
        scene.getStylesheets().add("styles.css");

        stage.setScene(scene);
        stage.show();

        Node chartTypeObj = root.lookup("#chartTypeField");

        if(chartTypeObj instanceof ChoiceBox) {
            ChoiceBox chartChoiceBox = (ChoiceBox) chartTypeObj;
            chartChoiceBox.getSelectionModel().selectFirst();
        }
    }

    public static void main(String[] args) throws IOException {
        Const.load(new File(args[0]));
        launch(args);
    }
}
