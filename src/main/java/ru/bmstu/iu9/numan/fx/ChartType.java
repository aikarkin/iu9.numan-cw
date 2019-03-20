package ru.bmstu.iu9.numan.fx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ChartType {
    PLATFORM_COORD("x₁(t)"),
    BLOCK_COORD("x₂(t)"),
    PLATFORM_SPEED("v₁(t)"),
    BLOCK_SPEED("v₂(t)")
    ;
    private String label;
    private Matcher funcMatcher;

    ChartType(String label) {
        this.label = label;
        this.funcMatcher = Pattern.compile("(.*?)\\((.*?)\\)").matcher(label);
        funcMatcher.matches();
    }

    public String label() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    public String xAxis() {
        return funcMatcher.group(2);
    }

    public String yAxis() {
        return funcMatcher.group(1);
    }

}
