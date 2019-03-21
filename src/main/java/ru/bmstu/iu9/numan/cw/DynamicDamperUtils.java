package ru.bmstu.iu9.numan.cw;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import static java.lang.Math.cos;

public class DynamicDamperUtils {
    public static RealVector getDeRhsVec(
            double t,
            double omega,
            double alpha,
            double fA,
            RealVector paramVec,
            RealVector x) {
        double eta1, eta2, xi1, xi2;
        double m1, c1, b1, m2, c2, b2, m, dXi;

        m1 = paramVec.getEntry(0);
        c1 = paramVec.getEntry(1);
        b1 = paramVec.getEntry(2);
        m2 = paramVec.getEntry(3);
        c2 = paramVec.getEntry(4);
        b2 = paramVec.getEntry(5);

        xi1 = x.getEntry(0);
        xi2 = x.getEntry(1);
        eta1 = x.getEntry(2);
        eta2 = x.getEntry(3);
        m = m1 + m2;
        dXi = xi2 - xi1;

        return new ArrayRealVector(new double[]{
                eta1,
                eta2,
                (2 * b1 * eta1 - 2 * c1 * xi1 + c2 * dXi + fA * cos(omega * t + alpha)) / m,
                (b2 * eta2 - c2 * dXi) / m2
        });
    }
}
