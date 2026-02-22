package com.minecraft.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.DynamicConverter;

public class ColoredLevelConverter extends DynamicConverter<ILoggingEvent> {

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";

    @Override
    public String convert(ILoggingEvent event) {
        Level level = event.getLevel();
        String color = getForegroundColor(level);
        String levelStr = level.toString();
        return color + levelStr + RESET;
    }

    private String getForegroundColor(Level level) {
        switch (level.toInt()) {
            case Level.ERROR_INT:
                return RED;
            case Level.WARN_INT:
                return YELLOW;
            case Level.INFO_INT:
                return GREEN;
            case Level.DEBUG_INT:
                return CYAN;
            case Level.TRACE_INT:
                return BLUE;
            default:
                return MAGENTA;
        }
    }
}