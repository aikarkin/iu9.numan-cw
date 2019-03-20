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

        RungeKutta.DURATION = valueOf((String) prop.get("deferentialEquations.rk.duration"));
        RungeKutta.DT = valueOf((String) prop.get("deferentialEquations.rk.step"));


        PenaltyMethod.EPS = valueOf((String) prop.get("optimization.penalty.eps"));
        PenaltyMethod.BETA = valueOf((String) prop.get("optimization.penalty.beta"));
        PenaltyMethod.R0 = valueOf((String) prop.get("optimization.penalty.r0"));


        NelderMeadMethod.ALPHA = valueOf((String) prop.get("optimization.nm.reflection"));
        NelderMeadMethod.BETA = valueOf((String) prop.get("optimization.nm.shrink"));
        NelderMeadMethod.GAMMA = valueOf((String) prop.get("optimization.nm.dilation"));
        NelderMeadMethod.MU = valueOf((String) prop.get("optimization.nm.reduction"));
        NelderMeadMethod.PSI = valueOf((String) prop.get("optimization.nm.minDistortionAngle"));
        NelderMeadMethod.EDGE_LEN = valueOf((String) prop.get("optimization.nm.startEdgeLen"));
        NelderMeadMethod.SIMPLEX_REPAIR_STEP = valueOf((String) prop.get("optimization.nm.simplexRepairStep"));
        NelderMeadMethod.MAX_ITERATIONS = Integer.valueOf((String) prop.get("optimization.nm.maxIter"));
        NelderMeadMethod.SIGMA = valueOf((String) prop.get("optimization.nm.minEdgeLen"));
        NelderMeadMethod.EPS = valueOf((String) prop.get("optimization.nm.minStandardDeviation"));


        PatternSearch.EPS = valueOf((String) prop.get("optimization.ps.eps"));
        PatternSearch.SIGMA = valueOf((String) prop.get("optimization.ps.sigma"));
        PatternSearch.ALPHA = valueOf((String) prop.get("optimization.ps.alpha"));
        PatternSearch.LAMBDA = valueOf((String) prop.get("optimization.ps.lambda"));

        PatternSearch.OMEGA_STEP = valueOf((String) prop.get("optimization.ps.omegaStep"));
        PatternSearch.ALPHA_STEP = valueOf((String) prop.get("optimization.ps.alphaStep"));
        PatternSearch.M1_STEP = valueOf((String) prop.get("optimization.ps.m1Step"));
        PatternSearch.M2_STEP = valueOf((String) prop.get("optimization.ps.m2Step"));
        PatternSearch.C1_STEP = valueOf((String) prop.get("optimization.ps.c1Step"));
        PatternSearch.C2_STEP = valueOf((String) prop.get("optimization.ps.c2Step"));
        PatternSearch.B1_STEP = valueOf((String) prop.get("optimization.ps.b1Step"));
        PatternSearch.B2_STEP = valueOf((String) prop.get("optimization.ps.b2Step"));
        PatternSearch.F_STEP = valueOf((String) prop.get("optimization.ps.fStep"));


        DynamicVibrationDamper.m1Min = valueOf((String) prop.get("optimization.dvd.m1.min"));
        DynamicVibrationDamper.m1Max = valueOf((String) prop.get("optimization.dvd.m1.max"));

        DynamicVibrationDamper.m2Min = valueOf((String) prop.get("optimization.dvd.m2.min"));
        DynamicVibrationDamper.m2Max = valueOf((String) prop.get("optimization.dvd.m2.max"));

        DynamicVibrationDamper.c1Min = valueOf((String) prop.get("optimization.dvd.c1.min"));
        DynamicVibrationDamper.c1Max = valueOf((String) prop.get("optimization.dvd.c1.max"));

        DynamicVibrationDamper.c2Min = valueOf((String) prop.get("optimization.dvd.c2.min"));
        DynamicVibrationDamper.c2Max = valueOf((String) prop.get("optimization.dvd.c2.max"));

        DynamicVibrationDamper.b1Min = valueOf((String) prop.get("optimization.dvd.b1.min"));
        DynamicVibrationDamper.b1Max = valueOf((String) prop.get("optimization.dvd.b1.max"));

        DynamicVibrationDamper.b2Min = valueOf((String) prop.get("optimization.dvd.b2.min"));
        DynamicVibrationDamper.b2Max = valueOf((String) prop.get("optimization.dvd.b2.max"));

        DynamicVibrationDamper.fMin = valueOf((String) prop.get("optimization.dvd.f.min"));
        DynamicVibrationDamper.fMax = valueOf((String) prop.get("optimization.dvd.f.max"));

        DynamicVibrationDamper.omegaMin = valueOf((String) prop.get("optimization.dvd.omega.min"));
        DynamicVibrationDamper.omegaMax = valueOf((String) prop.get("optimization.dvd.omega.max"));

        DynamicVibrationDamper.alphaMin = valueOf((String) prop.get("optimization.dvd.alpha.min"));
        DynamicVibrationDamper.alphaMax = valueOf((String) prop.get("optimization.dvd.alpha.max"));
        DynamicVibrationDamper.xi2Max = valueOf((String) prop.get("optimization.dvd.xi2.max"));
    }

    public static class RungeKutta {

        public static double DURATION;
        public static double DT;

    }

    public static class PenaltyMethod {

        public static double EPS;
        public static double BETA;
        public static double R0;

    }

    public static class PatternSearch {

        public static double OMEGA_STEP;
        public static double ALPHA_STEP;
        public static double M1_STEP;
        public static double M2_STEP;
        public static double C1_STEP;
        public static double C2_STEP;
        public static double B1_STEP;
        public static double B2_STEP;
        public static double F_STEP;

        public static double SIGMA;
        public static double EPS;
        public static double ALPHA;
        public static double LAMBDA;

    }

    public static class DynamicVibrationDamper {

        public static double m1Min;
        public static double m1Max;
        public static double m2Min;
        public static double m2Max;
        public static double c1Min;
        public static double c1Max;
        public static double c2Min;
        public static double c2Max;
        public static double b1Min;
        public static double b1Max;
        public static double b2Min;
        public static double b2Max;
        public static double fMin;
        public static double fMax;
        public static double omegaMin;
        public static double omegaMax;
        public static double alphaMin;
        public static double alphaMax;
        public static double xi2Max;

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
