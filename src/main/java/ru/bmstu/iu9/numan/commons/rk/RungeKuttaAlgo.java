package ru.bmstu.iu9.numan.commons.rk;

import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.List;

import static ru.bmstu.iu9.numan.cw.Const.RungeKutta.DT;

public class RungeKuttaAlgo {
    public static List<RealVector> rungeKutta(ParamEq f, RungeKuttaPredicate exitPredicate, RealVector y0) {
        RealVector k1, k2, k3, k4;
        ArrayList<RealVector> yArr = new ArrayList<>();
        yArr.add(y0);
        double t;

        int i = 0;

        do {
            t = i * DT;
            RealVector yn = yArr.get(i);

            k1 = f.apply(t, yn).mapMultiply(DT);
            k2 = f.apply(t + DT / 2.0, yn.add(k1.mapMultiply(0.5)))
                    .mapMultiply(DT);
            k3 = f.apply(t + DT / 2.0, yn.add(k2.mapMultiply(0.5)))
                    .mapMultiply(DT);
            k4 = f.apply(t + DT,yn.add(k3))
                    .mapMultiply(DT);

            yArr.add(yn
                    .add(k1
                            .add(k2.mapMultiply(2.0))
                            .add(k3.mapMultiply(2.0))
                            .add(k4)
                            .mapMultiply(1.0 / 6.0)
                    )
            );
            i++;
        } while (!exitPredicate.apply(t, yArr.get(i - 1), yArr.get(i)));

//        res.remove(i);

        return yArr;
    }
}
