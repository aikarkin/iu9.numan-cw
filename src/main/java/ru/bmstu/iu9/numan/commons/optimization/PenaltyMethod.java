package ru.bmstu.iu9.numan.commons.optimization;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.Math.*;
import static ru.bmstu.iu9.numan.cw.Const.PenaltyMethod.*;

@SuppressWarnings("Duplicates")
public class PenaltyMethod {
    private static final double CONSTRAINT_FUNC_EXP = 6.0;
    private static final BiFunction<Function<RealVector, Double>, RealVector, Double> constraintFunc;

    static {
//        constraintFunc = (func, vec) -> pow(func.apply(vec), 2.0);
//        constraintFunc = (func, vec) -> {
//            double fVal = func.apply(vec);
//            return fVal >= 0 ? pow(fVal + 1.0, CONSTRAINT_FUNC_EXP) : 0.0;
//        };
        constraintFunc = (func, vec) -> {
            double funcVal = func.apply(vec);
            return funcVal >= 0 ? pow(funcVal + 2, CONSTRAINT_FUNC_EXP) : -log(-funcVal);
        };
    }

    public static RealVector optimizeWithPenaltyMethod(
            Function<RealVector, Double> targetFunc,
            List<Function<RealVector, Double>> constraints,
            List<Double> constraintsWeights,
            double[] coordSteps,
            RealVector x0
    ) {
        System.out.println("Условная оптимизация методом штрафных функций");
//        isPointSatisifiesConstraints(constraints, x0);
        final Function<RealVector, Double> penaltyFunc = getPenaltyFunc(constraints, constraintsWeights);
        RealVector x = x0;
        int k = 0;
        double penalty;

        try {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\t число итераций: " + k);

        return x;
    }

    public static RealVector optimizeWithPenaltyMethod2(
            Function<RealVector, Double> targetFunc,
            List<Function<RealVector, Double>> constraints,
            List<Double> constraintsWeights,
            RealVector x0
    ) {
        System.out.println("Условная оптимизация методом штрафных функций");
//        isPointSatisifiesConstraints(constraints, x0);
        final Function<RealVector, Double> penaltyFunc = getPenaltyFunc(constraints, constraintsWeights);
        RealVector x = x0;
        int k = 0;
        double penalty, r = R0;

        do {
            final double finalRVal = r;
            Function<RealVector, Double> func = (vec) -> {
                double funcVal = targetFunc.apply(vec);
                double penaltyFuncVal = penaltyFunc.apply(vec);
                return funcVal + finalRVal * penaltyFuncVal;
            };

            x = NelderMeadMethod.optimizeWithNelderMead(func, x);

            penalty = penaltyFunc.apply(x);
            r = r * BETA;
            k++;
        } while (penalty > EPS);

        System.out.println("\t число итераций: " + k);

        return x;
    }

    private static boolean isPointSatisifiesConstraints(List<Function<RealVector, Double>> constraints, RealVector x) {
        for (int i = 0; i < constraints.size(); i++) {
            if (constraints.get(i).apply(x) > 0) {
                return false;
            }
        }
        return true;
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
