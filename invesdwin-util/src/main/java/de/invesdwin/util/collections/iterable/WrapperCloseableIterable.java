package de.invesdwin.util.collections.iterable;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.list.ListCloseableIterable;

@NotThreadSafe
public final class WrapperCloseableIterable<E> implements ICloseableIterable<E> {

    private final Iterable<? extends E> delegate;

    private WrapperCloseableIterable(final Iterable<? extends E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return WrapperCloseableIterator.maybeWrap(delegate.iterator());
    }

    @SuppressWarnings("unchecked")
    public static <T> Iterable<T> maybeUnwrap(final ICloseableIterable<? extends T> iterator) {
        if (iterator instanceof WrapperCloseableIterable) {
            final WrapperCloseableIterable<T> it = (WrapperCloseableIterable<T>) iterator;
            return (Iterable<T>) it.delegate;
        } else {
            return (Iterable<T>) iterator;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> ICloseableIterable<T> maybeWrap(final Iterable<? extends T> iterable) {
        if (iterable instanceof ICloseableIterable) {
            return (ICloseableIterable<T>) iterable;
        } else if (iterable instanceof List) {
            return maybeWrap((List<T>) iterable);
        } else {
            return new WrapperCloseableIterable<T>(iterable);
        }
    }

    public static <T> ICloseableIterable<T> maybeWrap(final List<? extends T> iterable) {
        return new ListCloseableIterable<T>((List<T>) iterable);
    }

}
