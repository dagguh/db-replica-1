package com.atlassian.db.replica.internal;


import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public abstract class LazyReference<T> implements Supplier<T> {
    private final AtomicReference<T> reference = new AtomicReference<>();

    protected abstract T create() throws Exception;

    public boolean isInitialized() {
        return reference.get() != null;
    }

    @Override
    public T get() {
        if (!isInitialized()) {
            try {
                reference.compareAndSet(null, create());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return reference.get();
    }

    public void reset() {
        reference.set(null);
    }
}
