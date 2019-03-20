package ru.bmstu.iu9.numan.commons.optimization;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.function.Function;

import static java.lang.Math.*;

public class NelderMeadMethod {

    // коэффициент отражения
    private static final double NM_ALPHA = 1.0;
    // коэффициент сжатия
    private static final double NM_BETA = 0.5;
    // коэффициент растяжения
    private static final double NM_GAMMA = 2.0;
    // коэффициент редукции
    private static final double NM_MU = 0.5;
    // предельный угол искажения симплекса (rad)
    private static final double NM_PSI = 0.01;
    // на каждой k-й итерации равной ${SIMPLEX_REPAIR_STEP} будем проверять симплекс на искаженность, при необходимости восстанавливать его
    private static final int SIMPLEX_REPAIR_STEP = 10;


    public static RealVector optimizeWithNelderMead(Function<RealVector, Double> targetFunc, double sigma, double eps, RealVector x0, double s) {
        int n, hi, gi, li, k = 0;
        n = x0.getDimension();
        boolean shrinkRequired;
        double fr, fe, fs, dist, der;
        RealVector[] dots = createSimplex(x0, s, n);
        double[] fValues = getFuncValues(targetFunc, dots);
        RealVector xc, xr, xe, xs;

        int[] sortedDotsIndexes = getSortedDotsIndexes(fValues);
        li = sortedDotsIndexes[0];
        gi = sortedDotsIndexes[1];
        hi = sortedDotsIndexes[2];


        do {
            shrinkRequired = false;

            xc = findCenterOfMass(dots, hi);
            xr = xc.add(xc.subtract(dots[hi]).mapMultiply(NM_ALPHA));
            fr = targetFunc.apply(xr);

            if (fr < fValues[li]) {
                xe = xc.mapMultiply(1 - NM_GAMMA).add(xr.mapMultiply(NM_GAMMA));
                fe = targetFunc.apply(xe);

                if (fe < fr) {
                    dots[li] = xe;
                    fValues[li] = fe;
                } else {
                    dots[hi] = xr;
                    fValues[hi] = fr;
                }
            } else if (fValues[li] < fr && fr < fValues[gi]) {
                dots[hi] = xr;
                fValues[hi] = fr;
            } else if (fValues[gi] < fr && fr < fValues[hi]) {
                dots[hi] = swap(xr, xr = dots[hi]);
                fValues[hi] = swap(fr, fr = fValues[hi]);

                shrinkRequired = true;
            } else if (fValues[hi] < fr) {
                shrinkRequired = true;
            }

            if (shrinkRequired) {
                xs = dots[hi].mapMultiply(NM_BETA).add(xc.mapMultiply(1 - NM_BETA));
                fs = targetFunc.apply(xs);

                if (fs < fValues[hi]) {
                    dots[hi] = xs;
                    fValues[hi] = fs;
                } else {
                    for (int i = 0; i < n + 1; i++) {
                        if (i != li) {
                            dots[i] = dots[li].add(dots[i].subtract(dots[li]).mapMultiply(NM_MU));
                            fValues[i] = targetFunc.apply(dots[i]);
                        }
                    }

                    sortedDotsIndexes = getSortedDotsIndexes(fValues);
                    li = sortedDotsIndexes[0];
                    gi = sortedDotsIndexes[1];
                    hi = sortedDotsIndexes[2];
                }
            }

            if (k > 0 && k % SIMPLEX_REPAIR_STEP == 0) {
                double minAngle = findMinAngleBetweenAdjacentEdges(dots);
                if (minAngle < NM_PSI) {
                    dots = createSimplex(dots[li], dots[li].getDistance(dots[gi]), n);
                    for (int i = 0; i < n + 1; i++) {
                        fValues[i] = targetFunc.apply(dots[i]);
                    }
                }
            }

            k++;
            dist = findMaxEdgeLen(dots);
            der = standardDeviationOf(fValues, li);
        } while (dist >= sigma && der >= eps);

        System.out.println("\t\t\t число итераци: " + k);

        return dots[li];
    }

    public static RealVector findCenterOfMass(RealVector[] dots, int skipIndex) {
        int n = dots.length;
        RealVector xc = new ArrayRealVector(dots[0].getDimension(), 0.0);

        for (int i = 0; i < n; i++) {
            if (i != skipIndex)
                xc = xc.add(dots[i]);
        }

        return xc.mapMultiply(1.0 / (n - 1));
    }

    public static int[] getSortedDotsIndexes(double[] fValues) {
        int hi = 0, li = 0, gi = 0;

        for (int i = 0; i < fValues.length; i++) {
            if (fValues[i] > fValues[hi]) {
                hi = i;
            } else if (fValues[i] < fValues[li]) {
                gi = li;
                li = i;
            }
        }

        return new int[]{li, gi, hi};
    }

    public static double findMaxEdgeLen(RealVector[] dots) {
        double dist, maxEdgeLen = 0.0;

        for (int i = 0; i < dots.length; i++) {
            for (int j = i + 1; j < dots.length; j++) {
                dist = dots[i].getDistance(dots[j]);
                if (dist > maxEdgeLen) {
                    maxEdgeLen = dist;
                }
            }
        }

        return maxEdgeLen;
    }

    public static double[] getFuncValues(Function<RealVector, Double> func, RealVector[] dots) {
        double[] funcValues = new double[dots.length];

        for (int i = 0; i < dots.length; i++) {
            funcValues[i] = func.apply(dots[i]);
        }

        return funcValues;
    }

    // returns angle in radians
    public static double findMinAngleBetweenAdjacentEdges(RealVector[] dots) {
        int n = dots.length;
        double minAngle = Double.MAX_VALUE, angle;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    if (i != j && j != k && k != i) {
                        angle = acos(
                                dots[j].subtract(dots[k]).dotProduct(dots[j].subtract(dots[k]))
                                        / (dots[i].getDistance(dots[k]) * dots[j].getDistance(dots[k]))
                        );
                        if (angle < minAngle) {
                            minAngle = angle;
                        }
                    }
                }
            }
        }

        return minAngle;
    }

    public static double standardDeviationOf(double[] values, int skipIndex) {
        int n = values.length;
        double sumOfSquares = 0.0;
        double fl = values[skipIndex];

        for (int i = 0; i < n; i++) {
            if (i != skipIndex) {
                sumOfSquares += pow(values[i] - fl, 2.0);
            }
        }

        return sqrt(sumOfSquares / (n + 1));
    }

    public static RealVector[] createSimplex(RealVector x0, double edgeLen, int dim) {
        double l1 = edgeLen / (dim * sqrt(2.0)) * (sqrt(dim + 1) + dim - 1.0);
        double l2 = edgeLen / (dim * sqrt(2.0)) * (sqrt(dim + 1) - 1.0);
        RealVector[] dots = new RealVector[dim + 1];
        RealVector lVec;
        dots[0] = x0;

        for (int i = 1; i < dim + 1; i++) {
            lVec = new ArrayRealVector(dim, l2);
            lVec.setEntry(i - 1, l1);

            dots[i] = x0.add(lVec);
        }

        return dots;
    }

    public static <T> T swap(T a, T b) {
        return a;
    }

}
