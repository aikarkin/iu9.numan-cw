package ru.bmstu.iu9.numan.cw.task;

import javafx.concurrent.Task;
import ru.bmstu.iu9.numan.cw.FxWindows;
import ru.bmstu.iu9.numan.cw.controller.OptimizationResultsManager;

public class ChartDrawingTask extends Task {
    OptimizationResultsManager opt;

    public ChartDrawingTask(OptimizationResultsManager optimizationResultsManager) {
        this.opt = optimizationResultsManager;
    }

    @Override
    protected Object call() {
        try {
            System.out.println("start");
            FxWindows.showCharts(
                    opt.chartType(),
                    opt.startParams(),
                    opt.optimalParams(),
                    opt.omega(),
                    opt.alpha(),
                    opt.fA()
            );

            System.out.println("success");
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
