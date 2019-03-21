package ru.bmstu.iu9.numan.cw;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import static java.lang.Math.min;
import static ru.bmstu.iu9.numan.cw.FxWindows.showConfigurationWindow;

@SuppressWarnings("Duplicates")
public class FxApplication extends Application {

    public static void main(String[] args) {
        try {
            Const.load(new File(args[0]));
            launch(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        showConfigurationWindow();
    }

}
