package ru.bmstu.iu9.numan.commons.optimization;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import org.testng.annotations.Test;

import java.util.function.Function;

import static java.lang.Math.pow;
import static org.testng.Assert.*;

public class PatternSearchTest {

//    @Test
//    public void testOptimizeWithPatternSearch() {
//        Function<RealVector, Double> targetFunc = (vec) -> {
//            double x = vec.getEntry(0), y = vec.getEntry(1);
//            return x * x + x * y + y * y - 6 * x - 9 * y;
//        };
//
//        RealVector x0 = MatrixUtils.createRealVector(new double[] {0.0, 0.0});
//
//        RealVector minDot = PatternSearch.optimizeWithPatternSearch(targetFunc, x0);
//        assertEquals(minDot.getEntry(0), 1.0, 0.01);
//        assertEquals(minDot.getEntry(1), 4.0, 0.01);
//        assertEquals(targetFunc.apply(minDot), -21.0, 0.01);
//    }

}