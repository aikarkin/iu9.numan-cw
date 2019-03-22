package ru.bmstu.iu9.numan.commons.optimization;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.bmstu.iu9.numan.cw.Const;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.function.Function;

import static java.lang.Math.pow;
import static org.testng.Assert.*;

public class NelderMeadMethodTest {

    @BeforeClass
    public void init() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL constUrl = classLoader.getResource("const.properties");
        if (constUrl == null) {
            throw new FileNotFoundException("Файл const.properties не найден в classpath");
        }

        Const.load(new File(constUrl.getFile()));
    }

    @Test
    public void testOptimizeWithNelderMead1() {
        Function<RealVector, Double> objectiveFunc = (vec) -> {
            double x = vec.getEntry(0), y = vec.getEntry(1);
            return 10.0 * pow(y - pow(x, 2.0), 2.0) + pow(1.0 - x, 2.0);
        };

        RealVector x0 = MatrixUtils.createRealVector(new double[]{10.0, -2.0});

        RealVector sol = NelderMeadMethod.optimizeWithNelderMead(objectiveFunc, x0);
        System.out.printf("x*=%s, f(x*)=%.3f", sol, objectiveFunc.apply(sol));

        assertEquals(sol.getEntry(0), 1.0, 0.001);
        assertEquals(sol.getEntry(1), 1.0, 0.001);
    }

    @Test
    public void testOptimizeWithNelderMead2() {
        Function<RealVector, Double> objectiveFunc = (vec) -> {
            double x = vec.getEntry(0), y = vec.getEntry(1);
            return pow(x, 2.0) + x * y + pow(y, 2.0) - 6.0 * x - 9.0 * y;
        };

        RealVector x0 = MatrixUtils.createRealVector(new double[]{0.0, 0.0});

        RealVector sol = NelderMeadMethod.optimizeWithNelderMead(objectiveFunc, x0);
        System.out.printf("x*=%s, f(x*)=%.3f", sol, objectiveFunc.apply(sol));

        assertEquals(objectiveFunc.apply(sol), -21.0, 0.1);
    }

    @Test
    public void testGetSortedDotsIndexes() {
        int[] indexes = NelderMeadMethod.getSortedDotsIndexes(new double[]{5.4, 1.8, -2.5});

        assertEquals(indexes[0], 2);
        assertEquals(indexes[1], 1);
        assertEquals(indexes[2], 0);

    }

}