package ru.bmstu.iu9.numan.cw;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static java.lang.Double.valueOf;

public class Const {

    private Const() {
    }

    public static void load(File file) throws IOException {
        Properties prop = new Properties();

        InputStream stream = new FileInputStream(file);
        prop.load(stream);

        Charts.DURATION = valueOf((String) prop.get("charts.rk.duration"));
        Charts.TIME_STEP = valueOf((String) prop.get("charts.rk.step"));

        DynamicVibrationDamper.DURATION = valueOf((String) prop.get("vibrationDamper.rk.duration"));
        DynamicVibrationDamper.TIME_STEP = valueOf((String) prop.get("vibrationDamper.rk.timeStep"));

        PenaltyMethod.EPS = valueOf((String) prop.get("penalty.eps"));
        PenaltyMethod.BETA = valueOf((String) prop.get("penalty.beta"));
        PenaltyMethod.R0 = valueOf((String) prop.get("penalty.r0"));


        PatternSearch.EPS = valueOf((String) prop.get("ps.eps"));
        PatternSearch.SIGMA = valueOf((String) prop.get("ps.sigma"));
        PatternSearch.ALPHA = valueOf((String) prop.get("ps.alpha"));
        PatternSearch.LAMBDA = valueOf((String) prop.get("ps.lambda"));

        PatternSearch.M1_STEP = valueOf((String) prop.get("ps.m1Step"));
        PatternSearch.C1_STEP = valueOf((String) prop.get("ps.c1Step"));
        PatternSearch.B1_STEP = valueOf((String) prop.get("ps.b1Step"));
        PatternSearch.M2_STEP = valueOf((String) prop.get("ps.m2Step"));
        PatternSearch.C2_STEP = valueOf((String) prop.get("ps.c2Step"));
        PatternSearch.B2_STEP = valueOf((String) prop.get("ps.b2Step"));

        NelderMeadMethod.ALPHA = valueOf((String) prop.get("nm.alpha"));
        NelderMeadMethod.BETA = valueOf((String) prop.get("nm.beta"));
        NelderMeadMethod.GAMMA = valueOf((String) prop.get("nm.gamma"));
        NelderMeadMethod.MU = valueOf((String) prop.get("nm.mu"));
        NelderMeadMethod.PSI = valueOf((String) prop.get("nm.minDistortionAngle"));
        NelderMeadMethod.EDGE_LEN = valueOf((String) prop.get("nm.startEdgeLen"));
        NelderMeadMethod.SIMPLEX_REPAIR_STEP = valueOf((String) prop.get("nm.simplexRepairStep"));
        NelderMeadMethod.MAX_ITERATIONS = Integer.valueOf((String) prop.get("nm.maxIter"));
        NelderMeadMethod.SIGMA = valueOf((String) prop.get("nm.minEdgeLen"));
        NelderMeadMethod.EPS = valueOf((String) prop.get("nm.minStandardDeviation"));
    }

    public static class Charts {

        public static double DURATION;
        public static double TIME_STEP;

    }

    public static class PenaltyMethod {

        public static double EPS;
        public static double BETA;
        public static double R0;

    }

    public static class PatternSearch {

        public static double M1_STEP;
        public static double M2_STEP;
        public static double C1_STEP;
        public static double C2_STEP;
        public static double B1_STEP;
        public static double B2_STEP;

        public static double SIGMA;
        public static double EPS;
        public static double ALPHA;
        public static double LAMBDA;

    }

    public static class DynamicVibrationDamper {

        public static double DURATION;
        public static double TIME_STEP;

    }

    public static class NelderMeadMethod {

        public static double ALPHA;
        public static double BETA;
        public static double GAMMA;
        public static double MU;
        public static double PSI;
        public static double EDGE_LEN;

        public static double SIMPLEX_REPAIR_STEP;
        public static double SIGMA;
        public static double EPS;
        public static int MAX_ITERATIONS;

    }

}
