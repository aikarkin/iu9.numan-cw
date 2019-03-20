package ru.bmstu.iu9.numan.commons.optimization;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.function.Function;

import static java.lang.Math.*;
import static ru.bmstu.iu9.numan.cw.Const.NelderMeadMethod.*;

public class NelderMeadMethod {

    public static RealVector optimizeWithNelderMead(Function<RealVector, Double> objectiveFunc, RealVector x0) {
        System.out.println("Метод Нелдера-Мида");

        int n, hi, gi, li, k = 0;
        n = x0.getDimension();
        boolean shrinkRequired;
        double fr, fe, fs, dist, der;
        RealVector[] simplexDots = createSimplex(x0, EDGE_LEN, n);
        double[] fValues = getFuncValues(objectiveFunc, simplexDots);
        RealVector xc, xr, xe, xs;

        int[] sortedDotsIndexes = getSortedDotsIndexes(fValues);
        li = sortedDotsIndexes[0];
        gi = sortedDotsIndexes[1];
        hi = sortedDotsIndexes[2];


        do {
            shrinkRequired = false;

            xc = findCenterOfMass(simplexDots, hi);
            xr = xc.add(xc.subtract(simplexDots[hi]).mapMultiply(ALPHA));
            fr = objectiveFunc.apply(xr);

            if (fr < fValues[li]) {
                xe = xc.mapMultiply(1 - GAMMA).add(xr.mapMultiply(GAMMA));
                fe = objectiveFunc.apply(xe);

                if (fe < fr) {
                    simplexDots[li] = xe;
                    fValues[li] = fe;
                } else {
                    simplexDots[hi] = xr;
                    fValues[hi] = fr;
                }
            }
            if (fValues[li] < fr && fr < fValues[gi]) {
                simplexDots[hi] = xr;
                fValues[hi] = fr;
            }
            if (fValues[gi] > fr && fr < fValues[hi]) {
                simplexDots[hi] = swap(xr, xr = simplexDots[hi]);
                fValues[hi] = swap(fr, fr = fValues[hi]);
                shrinkRequired = true;
            }
            if (fValues[hi] < fr) {
                shrinkRequired = true;
            }

            if (shrinkRequired) {
                xs = simplexDots[hi].mapMultiply(BETA).add(xc.mapMultiply(1 - BETA));
                fs = objectiveFunc.apply(xs);

                if (fs < fValues[hi]) {
                    simplexDots[hi] = xs;
                    fValues[hi] = fs;
                } else {
                    for (int i = 0; i < n + 1; i++) {
                        if (i != li) {
                            simplexDots[i] = simplexDots[li].add(simplexDots[i].subtract(simplexDots[li]).mapMultiply(MU));
                            fValues[i] = objectiveFunc.apply(simplexDots[i]);
                        }
                    }

                    sortedDotsIndexes = getSortedDotsIndexes(fValues);
                    li = sortedDotsIndexes[0];
                    gi = sortedDotsIndexes[1];
                    hi = sortedDotsIndexes[2];
                }
            }

            if (k > 0 && k % SIMPLEX_REPAIR_STEP == 0) {
                double minAngle = findMinAngleBetweenAdjacentEdges(simplexDots);
                if (minAngle < PSI) {
                    simplexDots = createSimplex(simplexDots[li], simplexDots[li].getDistance(simplexDots[gi]), n);
                    for (int i = 0; i < n + 1; i++) {
                        fValues[i] = objectiveFunc.apply(simplexDots[i]);
                    }
                }
            }

            k++;
            dist = findMaxEdgeLen(simplexDots);
            der = standardDeviationOf(fValues, li);
        } while (k < MAX_ITERATIONS && dist > SIGMA && der > EPS);

        System.out.println("\t\t\t число итераци: " + k);

        return simplexDots[li];
    }

    private static RealVector findCenterOfMass(RealVector[] dots, int skipIndex) {
        int n = dots.length;
        RealVector xc = new ArrayRealVector(dots[0].getDimension(), 0.0);

        for (int i = 0; i < n; i++) {
            if (i != skipIndex)
                xc = xc.add(dots[i]);
        }

        return xc.mapMultiply(1.0 / (n - 1));
    }

    public static int[] getSortedDotsIndexes(double[] fValues) {
        int hi = 0, li = 1, gi = 2;


        for (int i = 0; i < fValues.length; i++) {
            if (fValues[i] > fValues[hi]) {
                hi = i;
            }

            if (fValues[i] < fValues[li]) {
                gi = li;
                li = i;
            } else if(fValues[i] < fValues[gi]) {
                gi = i;
            }
        }

        return new int[]{li, gi, hi};
    }

    private static double findMaxEdgeLen(RealVector[] dots) {
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

    private static double[] getFuncValues(Function<RealVector, Double> func, RealVector[] dots) {
        double[] funcValues = new double[dots.length];

        for (int i = 0; i < dots.length; i++) {
            funcValues[i] = func.apply(dots[i]);
        }

        return funcValues;
    }

    // returns angle in radians
    private static double findMinAngleBetweenAdjacentEdges(RealVector[] dots) {
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

    private static double standardDeviationOf(double[] values, int skipIndex) {
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

    private static RealVector[] createSimplex(RealVector x0, double edgeLen, int dim) {
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

    private static <T> T swap(T a, T b) {
        return a;
    }

}
