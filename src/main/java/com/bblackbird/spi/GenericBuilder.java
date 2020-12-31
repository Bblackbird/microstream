package com.bblackbird.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class GenericBuilder<T> {

    private final Supplier<T> instantiator;

    private List<Consumer<T>> instanceModifiers = new ArrayList<>();

    private List<Predicate<T>> validationPredicates = new ArrayList<>();

    public GenericBuilder(Supplier<T> instantiator) {
        checkNotNull(instantiator);
        this.instantiator = instantiator;
    }

    public static <T> GenericBuilder<T> of(Supplier<T> instantiator) {
        return new GenericBuilder<T>(instantiator);
    }

    public <U> GenericBuilder<T> with(BiConsumer<T, U> consumer, U value) {
        Consumer<T> c = instance -> consumer.accept(instance, value);
        instanceModifiers.add(c);
        return this;
    }

    public GenericBuilder<T> with(Consumer<T> consumer) {
        instanceModifiers.add(consumer);
        return this;
    }

    public GenericBuilder<T> with(Predicate<T> predicate) {
        validationPredicates.add(predicate);
        return this;
    }

    public GenericBuilder<T> clear() {
        instanceModifiers.clear();
        validationPredicates.clear();
        return this;
    }

    public T build() {
        T value = instantiator.get();
        instanceModifiers.forEach(modifier -> modifier.accept(value));
        //instanceModifiers.clear();
        return value;
    }

    public T buildAndValidate() {
        T value = instantiator.get();
        instanceModifiers.forEach(modifier -> modifier.accept(value));
        verifyPredicates(value);
        //instanceModifiers.clear();
        return value;
    }

    private void verifyPredicates(T value) {

        List<Predicate<T>> violated = validationPredicates.stream()
                .filter(e -> !e.test(value)).collect(Collectors.toList());
        if (!violated.isEmpty()) {
            throw new IllegalStateException(value.toString()
                    + " violates predicates " + violated);
        }
    }

    private GenericBuilder(GenericBuilder<T> builder) {
        checkNotNull(builder);
        this.instantiator = builder.instantiator;
        this.instanceModifiers.addAll(builder.instanceModifiers);
        this.validationPredicates.addAll(builder.validationPredicates);
    }

    public GenericBuilder<T> fork() {
        return new GenericBuilder(this);
    }



}
