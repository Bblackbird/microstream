package com.bblackbird.spi;

public abstract class AbstractProcessorFactory<T, C> implements ProcessorFactory<T, C> {

    static String removePrefix(String identifier) {
        int pos = identifier.indexOf(':');
        if (pos != 1) {
            if (pos == identifier.length() - 1) {
                throw new IllegalArgumentException("Invalid identifier: " + identifier);
            }
            return identifier.substring(pos + 1);
        } else {
            return identifier;
        }
    }

    protected ClassLoader getCurrentClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = getClass().getClassLoader();
        }
        return cl;
    }

}
