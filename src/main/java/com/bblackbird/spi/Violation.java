package com.bblackbird.spi;

import com.bblackbird.violation.ExceptionLevel;
import com.bblackbird.violation.ExceptionSource;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import java.util.Comparator;
import java.util.function.Predicate;

public interface Violation extends Comparable<Violation> {

    long getTimestamp();

    String getKey();

    ViolationType getType();

    ExceptionLevel getSeverity();

    ExceptionSource getSource();

    String getMessage();

    String getContext();

    Exception getException();

    Class<?> getExceptionCausingClass();

    /** Key */
    Function<Violation, ViolationType> KEY = new Function<Violation, ViolationType>() {
        @Override
        public ViolationType apply(Violation input) {
            return input == null ? null : input.getType();
        }
    };

    /** Source */
    Function<Violation, ExceptionSource> SOURCE = new Function<Violation, ExceptionSource>() {
        @Override
        public ExceptionSource apply(Violation input) {
            return input == null ? null : input.getSource();
        }
    };

    /** Severity */
    Function<Violation, ExceptionLevel> SEVERITY = new Function<Violation, ExceptionLevel>() {
        @Override
        public ExceptionLevel apply(Violation input) {
            return input == null ? null : input.getSeverity();
        }
    };

    /** Message */
    Function<Violation, String> MESSAGE = new Function<Violation, String>() {
        @Override
        public String apply(Violation input) {
            return input == null ? null : input.getMessage();
        }
    };

    /** Context */
    Function<Violation, String> CONTEXT = new Function<Violation, String>() {
        @Override
        public String apply(Violation input) {
            return input == null ? null : input.getContext();
        }
    };

    /** Warning */
    Predicate<Violation> WARNING = new Predicate<Violation>() {
        @Override
        public boolean test(Violation input) {
            return input == null ? false : input.getSeverity() == ExceptionLevel.WARNING;
        }
    };

    /** Error */
    Predicate<Violation> ERROR = new Predicate<Violation>() {
        @Override
        public boolean test(Violation input) {
            return input == null ? false : input.getSeverity() == ExceptionLevel.ERROR;
        }
    };

    /** Fatal */
    Predicate<Violation> FATAL = new Predicate<Violation>() {
        @Override
        public boolean test(Violation input) {
            return input == null ? false : input.getSeverity() == ExceptionLevel.FATAL;
        }
    };

    /** Default Comparator */
    Comparator<Violation> DEFAULT_COMPARATOR = //
            Ordering.natural().nullsFirst().onResultOf(SEVERITY).
                    compound(Ordering.natural().nullsFirst().onResultOf(SOURCE).//
                    compound(Ordering.natural().nullsFirst().onResultOf(KEY).//
                    compound(Ordering.natural().nullsFirst().onResultOf(MESSAGE))));


    /** Error */
    Function<Violation, Long> TIMESTAMP = new Function<Violation, Long>() {
        @Override
        public Long apply(Violation input) {
            return input == null ? 0 : input.getTimestamp();
        }
    };

    /** Time Order Comparator */
    Comparator<Violation> TIME_ORDER_COMPARATOR = //
            Ordering.natural().nullsFirst().onResultOf(TIMESTAMP);

}
