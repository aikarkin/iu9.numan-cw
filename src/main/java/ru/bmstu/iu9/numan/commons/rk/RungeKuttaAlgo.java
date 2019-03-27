package ru.bmstu.iu9.numan.commons.rk;

import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RungeKuttaAlgo {
    public static List<RealVector> rungeKutta4thOrder(
            ParamEq f,
            RungeKuttaPredicate terminatePredicate,
            RealVector y0,
            double t0,
            double dt
    ) {
        RealVector k1, k2, k3, k4;
        ArrayList<RealVector> yArr = new ArrayList<>(Collections.singletonList(y0));
        double t;

        int i = 0;

        do {
            t = t0 + i * dt;
            RealVector yn = yArr.get(i);

            k1 = f.apply(t, yn).mapMultiply(dt);
            k2 = f.apply(t + dt / 2.0, yn.add(k1.mapMultiply(0.5)))
                    .mapMultiply(dt);
            k3 = f.apply(t + dt / 2.0, yn.add(k2.mapMultiply(0.5)))
                    .mapMultiply(dt);
            k4 = f.apply(t + dt,yn.add(k3))
                    .mapMultiply(dt);

            yArr.add(yn
                    .add(k1
                            .add(k2.mapMultiply(2.0))
                            .add(k3.mapMultiply(2.0))
                            .add(k4)
                            .mapMultiply(1.0 / 6.0)
                    )
            );
            i++;
        } while (!terminatePredicate.apply(t, yArr.get(i - 1), yArr.get(i)));

        return yArr;
    }
}
