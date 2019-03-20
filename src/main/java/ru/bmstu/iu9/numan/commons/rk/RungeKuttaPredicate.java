package ru.bmstu.iu9.numan.commons.rk;

import org.apache.commons.math3.linear.RealVector;

@FunctionalInterface
public interface RungeKuttaPredicate {
    boolean apply(double time, RealVector prevVec, RealVector curVec);
}
