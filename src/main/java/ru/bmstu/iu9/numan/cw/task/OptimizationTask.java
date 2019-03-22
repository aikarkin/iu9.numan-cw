package ru.bmstu.iu9.numan.cw.task;

import javafx.concurrent.Task;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import ru.bmstu.iu9.numan.commons.optimization.PenaltyMethod;
import ru.bmstu.iu9.numan.commons.rk.ParamEq;
import ru.bmstu.iu9.numan.commons.rk.RungeKuttaAlgo;
import ru.bmstu.iu9.numan.cw.controller.ConfigurationManager;

import java.util.*;
import java.util.function.Function;

import static java.lang.Math.abs;
import static ru.bmstu.iu9.numan.cw.Const.DynamicVibrationDamper.DURATION;
import static ru.bmstu.iu9.numan.cw.Const.DynamicVibrationDamper.TIME_STEP;
import static ru.bmstu.iu9.numan.cw.Const.PatternSearch.*;
import static ru.bmstu.iu9.numan.cw.DynamicDamperUtils.getDeRhsVec;

public class OptimizationTask extends Task<Map<String, Object>> {

    private ConfigurationManager c;

    public OptimizationTask(ConfigurationManager configurationManager) {
        this.c = configurationManager;
    }

    @Override
    protected Map<String, Object> call() {
        RealVector startParams = new ArrayRealVector(new double[]{c.m1(), c.c1(), c.b1(), c.m2(), c.c2(), c.b2()});

        Function<RealVector, RealVector> findVecWithMaxCoord = (paramVec) -> {
            ParamEq sdeRhs = (t, x) -> getDeRhsVec(t, c.omega(), c.alpha(), c.fA(), paramVec, x);

            List<RealVector> rkSol = RungeKuttaAlgo.rungeKutta(
                    sdeRhs,
                    (time, prevVec, curVec) -> time - DURATION >= 0.0001,
                    new ArrayRealVector(4, 0.0),
                    TIME_STEP
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
                x -> -x.getEntry(0) + c.m1Min(),
                x -> x.getEntry(0) - c.m1Max(),
                // c1
                x -> -x.getEntry(1) + c.c1Min(),
                x -> x.getEntry(1) - c.c1Max(),
                // b1
                x -> -x.getEntry(2) + c.b1Min(),
                x -> x.getEntry(2) - c.b1Max(),
                // m2
                x -> -x.getEntry(3) + c.m2Min(),
                x -> x.getEntry(3) - c.m2Max(),
                // c2
                x -> -x.getEntry(4) + c.c2Min(),
                x -> x.getEntry(4) - c.c2Max(),
                // b2
                x -> -x.getEntry(5) + c.b2Min(),
                x -> x.getEntry(5) - c.b2Max(),
                // xi2
                x -> findVecWithMaxCoord.apply(x).getEntry(1) - c.xi2Max()
        );

        double[] coordSteps = new double[]{
                M1_STEP,
                C1_STEP,
                B1_STEP,
                M2_STEP,
                C2_STEP,
                B2_STEP
        };

        List<Double> weights = new ArrayList<>();
        for (int i = 0; i < constraints.size(); i++) {
            weights.add(2.0);
        }

        weights.set(constraints.size() - 1, 1.0);

        RealVector optimalParams = PenaltyMethod.optimizeWithPenaltyMethod(
                (vec) -> abs(findVecWithMaxCoord.apply(vec).getEntry(0)),
                constraints,
                weights,
                coordSteps,
                startParams
        );

        return getResultFields(startParams, optimalParams);
    }

    private Map<String, Object> getResultFields(RealVector startParams, RealVector optimalParams) {
        Map<String, Object> fields = new HashMap<>();

        fields.put("m1Value", String.format("%.6f", startParams.getEntry(0)));
        fields.put("c1Value", String.format("%.6f", startParams.getEntry(1)));
        fields.put("b1Value", String.format("%.6f", startParams.getEntry(2)));
        fields.put("m2Value", String.format("%.6f", startParams.getEntry(3)));
        fields.put("c2Value", String.format("%.6f", startParams.getEntry(4)));
        fields.put("b2Value", String.format("%.6f", startParams.getEntry(5)));
        fields.put("m1OptValue", String.format("%.6f", optimalParams.getEntry(0)));
        fields.put("c1OptValue", String.format("%.6f", optimalParams.getEntry(1)));
        fields.put("b1OptValue", String.format("%.6f", optimalParams.getEntry(2)));
        fields.put("m2OptValue", String.format("%.6f", optimalParams.getEntry(3)));
        fields.put("c2OptValue", String.format("%.6f", optimalParams.getEntry(4)));
        fields.put("b2OptValue", String.format("%.6f", optimalParams.getEntry(5)));

        return fields;
    }

}
