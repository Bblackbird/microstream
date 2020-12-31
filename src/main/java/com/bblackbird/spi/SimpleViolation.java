package com.bblackbird.spi;


import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.bblackbird.violation.ExceptionLevel;
import com.bblackbird.violation.ExceptionSource;
import com.google.common.base.Objects;

/**
 * SimpleViolation
 */
public class SimpleViolation implements Violation, Serializable {

    private static final long serialVersionUID = 7249463011040889894L;

    private final long timestamp;

    private final String key;

    private final ViolationType type;

    private final ExceptionSource source;

    private final ExceptionLevel severity;

    private final String message;

    private final String context;

    private final Exception exception;

    private final Class<?> exceptionCausingClass;

    /**
     * Constructor.
     *
     * @param severity the severity to set
     * @param message the message to set
     */
    public SimpleViolation(ExceptionSource source, ExceptionLevel severity, String message, String context,
                           Exception e, Class<?> klazz) {
        this(null, ViolationType.ProcessingError, source, severity, message, context, e, klazz);
    }

    public SimpleViolation(String key, ViolationType type, ExceptionSource source, ExceptionLevel severity, String message, String context,
                           Exception e, Class<?> klazz) {
        this(System.nanoTime(), key, type, source, severity, message, context, e, klazz);
    }


    public SimpleViolation(long timestamp, String key, ViolationType type, ExceptionSource source, ExceptionLevel severity, String message, String context,
                           Exception e, Class<?> klazz) {
        this.timestamp = timestamp;
        this.key = key;
        this.type = checkNotNull(type);
        this.source = checkNotNull(source);
        this.severity = checkNotNull(severity);
        this.message = checkNotNull(message);
        this.context = checkNotNull(context);
        this.exception = e;
        this.exceptionCausingClass = klazz;
    }

    private SimpleViolation(Builder builder) {
        timestamp = builder.timestamp;
        key = builder.key;
        type = builder.type;
        source = builder.source;
        severity = builder.severity;
        message = builder.message;
        context = builder.context;
        exception = builder.exception;
        exceptionCausingClass = builder.exceptionCausingClass;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(SimpleViolation copy) {
        Builder builder = new Builder();
        builder.timestamp = copy.getTimestamp();
        builder.key = copy.getKey();
        builder.type = copy.getType();
        builder.source = copy.getSource();
        builder.severity = copy.getSeverity();
        builder.message = copy.getMessage();
        builder.context = copy.getContext();
        builder.exception = copy.getException();
        builder.exceptionCausingClass = copy.getExceptionCausingClass();
        return builder;
    }

    public long getTimestamp() {
        return timestamp;
    }

    /** {@inheritDoc} */
    @Override
    public String getKey() {
        return key;
    }

    @Override
    public ViolationType getType() {
        return type;
    }

    /** {@inheritDoc} */
    @Override
    public ExceptionSource getSource() {
        return source;
    }

    /** {@inheritDoc} */
    @Override
    public ExceptionLevel getSeverity() {
        return severity;
    }

    /** {@inheritDoc} */
    @Override
    public String getMessage() {
        return message;
    }

    /** {@inheritDoc} */
    @Override
    public String getContext() {
        return context;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public Class<?> getExceptionCausingClass() {
        return exceptionCausingClass;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(Violation o) {
        return DEFAULT_COMPARATOR.compare(this, o);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SimpleViolation) {
            SimpleViolation o = (SimpleViolation) obj;
            return Objects.equal(severity, o.severity) //
                    && Objects.equal(key, o.key) //
                    && Objects.equal(type, o.type) //
                    && Objects.equal(message, o.message);
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hashCode(severity, key, message);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return ViolationFormatter.INSTANCE.apply(this);
    }


    public static final class Builder {
        private long timestamp;
        private String key;
        private ViolationType type;
        private ExceptionSource source;
        private ExceptionLevel severity;
        private String message;
        private String context;
        private Exception exception;
        private Class<?> exceptionCausingClass;

        private Builder() {
        }

        public Builder withTimestamp(long val) {
            timestamp = val;
            return this;
        }

        public Builder withKey(String val) {
            key = val;
            return this;
        }

        public Builder withType(ViolationType val) {
            type = val;
            return this;
        }

        public Builder withSource(ExceptionSource val) {
            source = val;
            return this;
        }

        public Builder withSeverity(ExceptionLevel val) {
            severity = val;
            return this;
        }

        public Builder withMessage(String val) {
            message = val;
            return this;
        }

        public Builder withContext(String val) {
            context = val;
            return this;
        }

        public Builder withException(Exception val) {
            exception = val;
            return this;
        }

        public Builder withExceptionCausingClass(Class<?> val) {
            exceptionCausingClass = val;
            return this;
        }

        public SimpleViolation build() {
            return new SimpleViolation(this);
        }
    }
}
