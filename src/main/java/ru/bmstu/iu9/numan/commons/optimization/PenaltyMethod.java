package ru.bmstu.iu9.numan.commons.optimization;

import org.apache.commons.math3.linear.RealVector;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.Math.*;
import static ru.bmstu.iu9.numan.cw.Const.PenaltyMethod.*;

public class PenaltyMethod {
    private static final BiFunction<Function<RealVector, Double>, RealVector, Double> constraintFunc;

    static {
        constraintFunc = (func, vec) -> pow(func.apply(vec), 2.0);
    }

    public static RealVector optimizeWithPenaltyMethod(
            Function<RealVector, Double> targetFunc,
            List<Function<RealVector, Double>> constraints,
            List<Double> constraintsWeights,
            double[] coordSteps,
            RealVector x0
    ) {
        System.out.println("Условная оптимизация методом штрафных функций");
//        checkConstraintsForPoint(constraints, x0);
        final Function<RealVector, Double> penaltyFunc = getPenaltyFunc(constraints, constraintsWeights);
        RealVector x = x0;
        int k = 0;
        double penalty;

        do {
            double r = R0 * pow(BETA, k);
            Function<RealVector, Double> func = (vec) -> {
                double funcVal = targetFunc.apply(vec);
                double penaltyFuncVal = penaltyFunc.apply(vec);
                return funcVal + r * penaltyFuncVal;
            };

            x = PatternSearch.optimizeWithPatternSearch(func, x, coordSteps);
            penalty = penaltyFunc.apply(x);
            k++;
        } while (abs(penalty) > EPS);

        System.out.println("\t число итераций: " + k);

        return x;
    }

    private static void checkConstraintsForPoint(List<Function<RealVector, Double>> constraints, RealVector x) {
        for (int i = 0; i < constraints.size(); i++) {
            if (constraints.get(i).apply(x) >= 0) {
                String msg = String.format(
                        "Точка %s не удавлетворяет условию №%d",
                        x,
                        i + 1

                );
                throw new IllegalStateException(msg);
            }
        }
    }

    private static Function<RealVector, Double> getPenaltyFunc(
            List<Function<RealVector, Double>> constraints,
            List<Double> weights) {
        return (x) -> {
            double penalty = 0.0, curRestrictVal;

            for (int i = 0; i < constraints.size(); i++) {
                curRestrictVal = constraints.get(i).apply(x);
                penalty += curRestrictVal <= 0 ? 0.0 : weights.get(i) * constraintFunc.apply(constraints.get(i), x);
            }

            return penalty;
        };
    }

}
