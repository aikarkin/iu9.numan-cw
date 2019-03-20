package ru.bmstu.iu9.numan.commons.rk;

import org.apache.commons.math3.linear.RealVector;

import java.util.function.BiFunction;

/**
 * Represents parametric function multi-dimensional parametric equation of type z = f(t, y)
 * first param - t (in R)
 * second param - y (in R^n)
 * return type - result vector f(t, y)
 *
 * */
@FunctionalInterface
public interface ParamEq {
    RealVector apply(double t, RealVector vec);
}