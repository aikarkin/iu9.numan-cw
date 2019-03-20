package ru.bmstu.iu9.numan.commons.optimization;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static java.lang.Math.pow;

public class PenaltyMethodTest {

//    @Test
//    public void testOptimizeWithPenaltyMethod() {
//        Function<RealVector, Double> func = (vec) -> {
//            double x = vec.getEntry(0), y = vec.getEntry(1);
//            return 250.0 * pow(pow(x, 2.0) - y, 2.0) + 2.0 * pow(x - 1, 2.0) + 300;
//        };
//        List<Function<RealVector, Double>> constraints = Arrays.asList(
//                vec -> pow(vec.getEntry(0), 2.0) + pow(vec.getEntry(1), 2.0) - 40,
//                vec -> - vec.getEntry(0),
//                vec -> - vec.getEntry(1)
//        );
//        List<Double> weights = Arrays.asList(1.0, 1.0, 1.0);
//        RealVector x0 = MatrixUtils.createRealVector(new double[]{ -25, -25 });
//
//        RealVector minDot = PenaltyMethod.optimizeWithPenaltyMethod(func, constraints, weights, x0);
//        System.out.println(minDot);
//        System.out.println(func.apply(minDot));
//    }

}