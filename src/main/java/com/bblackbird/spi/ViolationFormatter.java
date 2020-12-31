package com.bblackbird.spi;


import com.google.common.base.Function;

/**
 * ViolationFormatter
 */
enum ViolationFormatter implements Function<Violation, String> {

    INSTANCE {
        @Override
        public String apply(Violation v) {
            return String.format("[ %s | %s | %s | %s ]", v.getKey(), v.getSeverity().name(), v.getType().name(), v.getMessage());
        }
    }

}
