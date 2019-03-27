package ru.bmstu.iu9.numan.commons.optimization;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.function.Function;

import static java.lang.Math.*;
import static ru.bmstu.iu9.numan.cw.Const.NelderMeadMethod.*;

public class NelderMeadMethod {

    public static RealVector optimizeWithNelderMead(
            Function<RealVector, Double> objectiveFunc,
            RealVector x0
    ) {
        int n, hi, gi, li, k = 0;
        n = x0.getDimension();
        boolean shrinkRequired;
        double fr, fe, fs, dist, der;
        RealVector[] simplexVectors = createSimplex(x0, EDGE_LEN, n);
        double[] fValues = getFuncValues(objectiveFunc, simplexVectors);
        int[] sortedSimplexIndexes;
        RealVector xc, xr, xe, xs;

        do {
            shrinkRequired = false;
            sortedSimplexIndexes = getSortedDotsIndexes(fValues);
            li = sortedSimplexIndexes[0];
            gi = sortedSimplexIndexes[1];
            hi = sortedSimplexIndexes[2];

            xc = getCenterOfMass(simplexVectors, hi);
            xr = xc.add(xc.subtract(simplexVectors[hi]).mapMultiply(ALPHA));
            fr = objectiveFunc.apply(xr);

            if (fr < fValues[li]) {
                xe = xc.subtract(xc.subtract(xr).mapMultiply(GAMMA));
                fe = objectiveFunc.apply(xe);

                if (fe < fValues[li]) {
                    simplexVectors[hi] = xe;
                    fValues[hi] = fe;
                } else {
                    simplexVectors[hi] = xr;
                    fValues[hi] = fr;
                }
            }
            if (fValues[li] < fr && fr < fValues[gi]) {
                simplexVectors[hi] = xr;
                fValues[hi] = fr;
            }
            if (fValues[gi] < fr && fr < fValues[hi]) {
                // меняем местами xr и xh, fr и fh
                simplexVectors[hi] = swap(xr, xr = simplexVectors[hi]);
                fValues[hi] = swap(fr, fr = fValues[hi]);
                shrinkRequired = true;
            }
            if (fValues[hi] < fr) {
                shrinkRequired = true;
            }

            if (shrinkRequired) {
                xs = xc
                        .add(simplexVectors[hi].subtract(xc)
                        .mapMultiply(BETA));
                fs = objectiveFunc.apply(xs);

                if (fs < fValues[hi]) {
                    simplexVectors[hi] = xs;
                    fValues[hi] = fs;
                } else {
                    for (int i = 0; i < n + 1; i++) {
                        if (i != li) {
                            simplexVectors[i] = simplexVectors[li]
                                    .add(simplexVectors[i].subtract(simplexVectors[li])
                                            .mapMultiply(MU));
                            fValues[i] = objectiveFunc.apply(simplexVectors[i]);
                        }
                    }
                }
            }

            if (k > 0 && k % SIMPLEX_REPAIR_STEP == 0) {
                double minAngle = findMinAngleBetweenAdjacentEdges(simplexVectors);
                if (minAngle < PSI) {
                    simplexVectors = createSimplex(
                            simplexVectors[li],
                            simplexVectors[li].getDistance(simplexVectors[gi]),
                            n
                    );
                    for (int i = 0; i < n + 1; i++) {
                        fValues[i] = objectiveFunc.apply(simplexVectors[i]);
                    }
                }
            }

            k++;
            dist = getMaxEdgeLen(simplexVectors);
            der = standardDeviationOf(fValues, li);
        } while (k < MAX_ITERATIONS && dist > SIGMA && der > EPS);

        System.out.println("\t\tНМ - число итераций: " + k);
        return simplexVectors[li];
    }

    public static int[] getSortedDotsIndexes(double[] fValues) {
        int hi = 0, li = 1, gi = 2;


        for (int i = 0; i < fValues.length; i++) {
            if (fValues[i] > fValues[hi]) {
                gi = hi;
                hi = i;
            }

            if (fValues[i] < fValues[li]) {
                li = i;
            }
        }

        return new int[]{li, gi, hi};
    }

    private static RealVector getCenterOfMass(RealVector[] vectors, int skipIndex) {
        int n = vectors.length;
        RealVector xc = new ArrayRealVector(vectors[0].getDimension(), 0.0);

        for (int i = 0; i < n; i++) {
            if (i != skipIndex)
                xc = xc.add(vectors[i]);
        }

        return xc.mapMultiply(1.0 / (n - 1));
    }

    private static double getMaxEdgeLen(RealVector[] vectors) {
        double dist, maxEdgeLen = 0.0;

        for (int i = 0; i < vectors.length; i++) {
            for (int j = i + 1; j < vectors.length; j++) {
                dist = vectors[i].getDistance(vectors[j]);
                if (dist > maxEdgeLen) {
                    maxEdgeLen = dist;
                }
            }
        }

        return maxEdgeLen;
    }

    private static double[] getFuncValues(Function<RealVector, Double> func, RealVector[] vectors) {
        double[] funcValues = new double[vectors.length];

        for (int i = 0; i < vectors.length; i++) {
            funcValues[i] = func.apply(vectors[i]);
        }

        return funcValues;
    }

    // returns angle in radians
    private static double findMinAngleBetweenAdjacentEdges(RealVector[] vectors) {
        int n = vectors.length;
        double minAngle = Double.MAX_VALUE, angle;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    if (i != j && j != k && k != i) {
                        angle = acos(
                                vectors[j].subtract(vectors[k]).dotProduct(vectors[j].subtract(vectors[k]))
                                        / (vectors[i].getDistance(vectors[k]) * vectors[j].getDistance(vectors[k]))
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
        RealVector[] vectors = new RealVector[dim + 1];
        RealVector lVec;
        vectors[0] = x0;

        for (int i = 1; i < dim + 1; i++) {
            lVec = new ArrayRealVector(dim, l2);
            lVec.setEntry(i - 1, l1);

            vectors[i] = x0.add(lVec);
        }

        return vectors;
    }

    private static <T> T swap(T a, T b) {
        return a;
    }

}
