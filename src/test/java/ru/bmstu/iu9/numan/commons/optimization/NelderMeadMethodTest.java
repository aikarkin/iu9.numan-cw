package ru.bmstu.iu9.numan.commons.optimization;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import org.testng.annotations.Test;

import java.util.function.Function;

import static java.lang.Math.pow;
import static org.apache.commons.math3.linear.MatrixUtils.createRealVector;
import static org.testng.Assert.assertEquals;

public class NelderMeadMethodTest {


    @Test
    public void testFindCenterOfMass() {
        RealVector[] dots = new RealVector[3];
        dots[0] = createRealVector(new double[]{0.0, 0.0});
        dots[1] = createRealVector(new double[]{0.0, 1.0});
        dots[2] = createRealVector(new double[]{1.0, 0.0});

        RealVector centerOfMass = NelderMeadMethod.findCenterOfMass(dots, 1);
        assertEquals(centerOfMass, createRealVector(new double[]{0.5, 0.0}));
    }

    @Test
    public void testFindMinAngleBetweenAdjacentEdges() {
        RealVector[] dots = new RealVector[3];
        dots[0] = createRealVector(new double[]{0.0, 0.0});
        dots[1] = createRealVector(new double[]{0.0, 2.0});
        dots[2] = createRealVector(new double[]{1.0, 0.0});

        double minAngle = NelderMeadMethod.findMinAngleBetweenAdjacentEdges(dots);
        assertEquals(minAngle, 0.463, 0.01);
    }

    @Test
    public void testStandardDeviationOf() {
        double[] values = new double[]{3.0, 1.0, 5.0};

        double dev = NelderMeadMethod.standardDeviationOf(values, 1);
        assertEquals(dev, 2.581, 0.01);
    }

    @Test
    public void testCreateSimplex() {
        RealVector x0 = MatrixUtils.createRealVector(new double[]{0.0, 0.0});
        double edgeLen = 2.0;

        RealVector[] simplexDots = NelderMeadMethod.createSimplex(x0, edgeLen, 2);

        assertEquals(simplexDots.length, 3);
        RealVector x1 = simplexDots[0], x2 = simplexDots[1], x3 = simplexDots[2];

        assertEquals(x1.getDistance(x2), edgeLen, 0.01);
        assertEquals(x2.getDistance(x3), edgeLen, 0.01);
        assertEquals(x3.getDistance(x1), edgeLen, 0.01);
    }

    @Test
    public void testOptimize() {
//        Function<RealVector, Double> targetFunc = (vec) -> {
//            double x = vec.getEntry(0), y = vec.getEntry(1);
//            return x * x + x * y + y * y - 6 * x - 9 * y;
//        };

        Function<RealVector, Double> targetFunc = vec -> {
            double x = vec.getEntry(0), y = vec.getEntry(1);
            return pow(pow(x, 2.0) + 3.0, 2.0) - pow(pow(y, 2.0) + 2.0, 2.0) - 10;
        };
        double sigma = 0.001, eps = 0.1;

        RealVector x0 = MatrixUtils.createRealVector(new double[]{1.0, 2.0});
        double s = 1.0;

        RealVector sol = NelderMeadMethod.optimizeWithNelderMead(targetFunc, sigma, eps, x0, s);

//        assertEquals(sol.getEntry(0), 1.0, 0.01);
//        assertEquals(sol.getEntry(1), 4.0, 0.01);
        System.out.printf("x*=%s, f(x*)=%.3f", sol, targetFunc.apply(sol));
    }

}