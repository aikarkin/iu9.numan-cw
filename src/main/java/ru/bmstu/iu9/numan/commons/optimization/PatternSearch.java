package ru.bmstu.iu9.numan.commons.optimization;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import java.util.Arrays;
import java.util.function.Function;

import static java.lang.Math.min;
import static ru.bmstu.iu9.numan.cw.Const.PatternSearch.*;

public class PatternSearch {


    public static RealVector optimizeWithPatternSearch(Function<RealVector, Double> targetFunc, RealVector x0, double[] coordSteps) {
        RealVector x1 = MatrixUtils.createRealVector(x0.toArray()), x2, x3, x4;
        double[] steps = Arrays.copyOf(coordSteps, coordSteps.length);

        for (; ; ) {
            x2 = exploringSearch(targetFunc, x1, steps, true);

            if (x2 == null)
                return x1;

            if(targetFunc.apply(x2) < EPS)
                return x2;


            for (; ; ) {
                x3 = x1.add(x2.subtract(x1).mapMultiply(LAMBDA));
                x4 = exploringSearch(targetFunc, x3, steps, false);

                if (x4 == null)
                    return x3;

                if(targetFunc.apply(x4) < EPS)
                    return x4;

                x1 = x2;
                if (x4.equals(x3)) {
                    break;
                } else {
                    x2 = x4;
                }

            }
        }
    }

    private static RealVector exploringSearch(Function<RealVector, Double> targetFunc, RealVector x, double[] steps, boolean mustDecreaseStep) {
        boolean allStepsTooLow = true;
        int dim = x.getDimension();
        double curFuncVal, rightFuncVal, leftFuncVal;
        RealVector x1 = MatrixUtils.createRealVector(x.toArray());

        for (int i = 0; i < dim; i++) {
            if (steps[i] < EPS) {
                continue;
            }

            allStepsTooLow = false;
            curFuncVal = targetFunc.apply(x1);
            x1.setEntry(i, x.getEntry(i) + steps[i]);
            rightFuncVal = targetFunc.apply(x1);
            x1.setEntry(i, x.getEntry(i) - steps[i]);
            leftFuncVal = targetFunc.apply(x1);

            if (curFuncVal <= min(rightFuncVal, leftFuncVal)) {
                if (mustDecreaseStep) {
                    steps[i] *= ALPHA;
                }
                // возвращаем прежнее значение координаты
                x1.setEntry(i, x.getEntry(i));
                continue;
            }

            if (rightFuncVal < leftFuncVal) {
                x1.setEntry(i, x.getEntry(i) + steps[i]);
            }
        }

        return allStepsTooLow ? null : x1;
    }

}
