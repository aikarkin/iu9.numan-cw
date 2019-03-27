package ru.bmstu.iu9.numan.commons.rk;

import org.apache.commons.math3.linear.RealVector;

public class StabilizedAmplitudeRKPredicate implements RungeKuttaPredicate {
//    private static final double EPS = 0.0001;
//    private static final int MAX_K = 4;
//    private int k = 0;
//    private RealVector maxAbsAmplitudeDot, prevMaxAbsAmplitudeDot;
//    private double prevMaxAmplitude = 0;
//    private double curMaxAmplitude = 2 * EPS;
//
//    @Override
//    public boolean apply(double time, RealVector prevVec, RealVector curVec) {
//        if(prevVec.getEntry(0) * curVec.getEntry(0) < 0) {
//            if(prevMaxAmplitude > 0.0 && abs(prevMaxAmplitude - curMaxAmplitude) < EPS) {
//                k++;
//            }
//            prevMaxAmplitude = curMaxAmplitude;
//            prevMaxAbsAmplitudeDot = maxAbsAmplitudeDot;
//            curMaxAmplitude = 0.0;
//        }
//
//        if(abs(curVec.getEntry(0)) > curMaxAmplitude) {
//            curMaxAmplitude = abs(curVec.getEntry(0));
//            maxAbsAmplitudeDot = curVec;
//        }
//
//        return k >= MAX_K;
//    }
//
//    public RealVector dotWithFirstMaxAbsCoord() {
//        return prevMaxAbsAmplitudeDot;
//    }

    private boolean jumpedThroughAxis = false;

    public boolean apply(double time, RealVector prevVec, RealVector curVec) {
        if(prevVec.getEntry(0) * curVec.getEntry(0) < 0) {
            jumpedThroughAxis = true;
        }

        return jumpedThroughAxis && prevVec.getEntry(0) * curVec.getEntry(0) < 0;
    }


}
