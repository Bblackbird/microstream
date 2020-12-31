package com.bblackbird.spi;

import com.bblackbird.violation.ExceptionLevel;
import com.bblackbird.violation.ExceptionSource;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.*;

import java.io.Serializable;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Violation Collection
 */
public class Violations implements Iterable<Violation>, Serializable {

    /** Empty Violations */
    public static final Violations EMPTY = new Violations();

    private static final long serialVersionUID = 2363440284829614585L;

    private final Multimap<String, Violation> violationMap;

    public Violations() {
        this(Collections.<Violation> emptyList());
    }

    public Violations(Violation... violations) {
        this(Lists.newArrayList(violations));
    }

    public Violations(Violations violations) {
        this(violations.getViolations());
    }

    public Violations(Collection<? extends Violation> violations) {
        violationMap = ArrayListMultimap.create();
        for (Violation violation : violations) {
            violationMap.put(violation.getKey(), violation);
        }
    }

    public void clear() {
        violationMap.clear();
    }


    /** {@inheritDoc} */
    @Override
    public Iterator<Violation> iterator() {
        return getViolations().iterator();
    }

    public boolean isEmpty() {
        return violationMap.isEmpty();
    }

    public boolean hasViolations() {
        return !isEmpty();
    }

    public boolean hasFatalErrors() {
        return !getFatalErrors().isEmpty();
    }

    public boolean hasGreaterOrEqualViolations(ExceptionLevel level) {
        return !getGreaterOrEqualViolations(level).isEmpty();
    }

    private List<Violation> getGreaterOrEqualViolations(ExceptionLevel level) {
        return FluentIterable.from(getViolations()).filter(v -> v == null ? false : v.getSeverity().ordinal() >= level.ordinal()).toList();
    }

    public boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    public boolean hasWarnings() {
        return !getWarnings().isEmpty();
    }

    public List<Violation> getViolations() {
        return sortedCopyOf(Lists.newArrayList(violationMap.values()));
    }

    public List<Violation> getTimeOrderedViolations() {
        return sortedCopyOf(Lists.newArrayList(violationMap.values()), Violation.TIME_ORDER_COMPARATOR);
    }

    public List<Violation> getWarnings() {
        return getViolations(ExceptionLevel.WARNING);
    }

    public List<Violation> getErrors() {
        return getViolations(ExceptionLevel.ERROR);
    }

    public List<Violation> getFatalErrors() {
        return getViolations(ExceptionLevel.FATAL);
    }    

    public List<Violation> getViolations(String key) {
        return sortedCopyOf(Lists.newArrayList(violationMap.get(key)));
    }

    public List<Violation> getViolations(final ExceptionLevel severity) {
        return FluentIterable.from(getViolations()).filter(new Predicate<Violation>() {
            @Override
            public boolean apply(Violation violation) {
                return violation == null ? false : violation.getSeverity() == severity;
            }
        }).toList();
    }

    public Optional<Violation> first() {
        return FluentIterable.from(getViolations()).first();
    }

    public Optional<Violation> first(final ExceptionLevel severity) {
        return FluentIterable.from(getViolations()).filter(v -> v == null? false : v.getSeverity() == severity).first();
    }

    public Optional<Violation> firstWithGreaterOrEqualViolations(ExceptionLevel level) {
        return FluentIterable.from(getViolations()).filter(v -> v == null ? false : v.getSeverity().ordinal() >= level.ordinal()).first();
    }

    public Violations addViolation(String key, ViolationType type, ExceptionSource source, ExceptionLevel severity, String message, String context,
                                   Exception exception, Class<?> klazz) {
        return addViolation(new SimpleViolation(key, type, source, severity, message, context, exception, klazz));
    }

    public Violations addViolation(Violation violation) {
        violationMap.put(checkNotNull(violation).getKey(), violation);
        return this;
    }

    public Violations addViolations(Violations violations) {
        violationMap.putAll(checkNotNull(violations).violationMap);
        return this;
    }

    public Violations addViolations(final String key, Violations violations) {
        return addViolations(checkNotNull(violations).transform(new Function<Violation, Violation>() {
            @Override
            public Violation apply(Violation violation) {
                return new SimpleViolation(String.format("%s.%s", key, violation.getKey()), violation.getType(), violation.getSource(),
                        violation.getSeverity(), violation.getMessage(), violation.getContext(), violation.getException(), violation.getExceptionCausingClass());
            }
        }));
    }

    public Violations transform(Function<Violation, Violation> transformer) {
        return new Violations(FluentIterable.from(getViolations()).transform(transformer).toList());
    }

    public Violations subset(final String key) {
        return new Violations(FluentIterable.from(getViolations()).filter(new Predicate<Violation>() {
            @Override
            public boolean apply(Violation violation) {
                return violation != null && violation.getKey() != null && violation.getKey().startsWith(key);
            }
        }).toList());
    }

    private List<Violation> sortedCopyOf(List<Violation> violations) {
        return sortedCopyOf(violations, Violation.DEFAULT_COMPARATOR);
    }

    private List<Violation> sortedCopyOf(List<Violation> violations, Comparator<Violation> comparator) {
        Collections.sort(violations, comparator);
        return ImmutableList.copyOf(violations);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        List<String> buffer = Lists.newArrayList();
        buffer.add("Violations: {");
        for (Violation violation : getViolations()) {
            buffer.add(String.format("  %s", violation));
        }
        buffer.add("}");
        return Joiner.on(System.lineSeparator()).join(buffer);
    }

    public static String quickFormatViolations(List<Violation> vList) {
        List<String> buffer = Lists.newArrayList();
        startViolations(buffer);
        addViolations(vList, buffer);
        closeViolations(buffer);
        return formatViolations(buffer);
    }

    private static void addViolations(List<Violation> vList, List<String> buffer) {
        for(Violation v : vList) {
            buffer.add(String.format(" %s", v));
        }
    }

    private static String formatViolations(List<String> buffer) {
        return Joiner.on(System.lineSeparator()).join(buffer);
    }

    private static void closeViolations(List<String> buffer) {
        buffer.add("}");
    }

    private static void startViolations(List<String> buffer) {
        buffer.add("Violations: {");
    }


}