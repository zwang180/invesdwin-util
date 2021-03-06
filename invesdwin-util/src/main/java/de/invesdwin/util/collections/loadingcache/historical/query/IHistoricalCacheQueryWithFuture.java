package de.invesdwin.util.collections.loadingcache.historical.query;

import java.util.Map.Entry;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalCacheQueryWithFuture<V> extends IHistoricalCacheQuery<V> {

    @Override
    IHistoricalCacheQueryWithFuture<V> withElementFilter(IHistoricalCacheQueryElementFilter<V> elementFilter);

    /**
     * Default is true. Filters key and thus values that have already been added to the result list. Thus the result
     * list might contain less values than shiftBackUnits specified.
     */
    @Override
    IHistoricalCacheQueryWithFuture<V> withFilterDuplicateKeys(boolean filterDuplicateKeys);

    @Override
    IHistoricalCacheQueryWithFuture<V> withFutureNull();

    @Override
    IHistoricalCacheQueryWithFuture<V> withFuture();

    /**
     * Jumps the specified shiftForwardUnits to the future instead of only one unit.
     */
    FDate getNextKey(FDate key, int shiftForwardUnits);

    /**
     * Skips null values for keys.
     * 
     * Fills the list with keys from the future.
     */
    ICloseableIterable<FDate> getNextKeys(FDate key, int shiftForwardUnits);

    Entry<FDate, V> getNextEntry(FDate key, int shiftForwardUnits);

    ICloseableIterable<V> getNextValues(FDate key, int shiftForwardUnits);

    /**
     * Skips null values for keys.
     * 
     * Fills the list with values from the future.
     */
    ICloseableIterable<Entry<FDate, V>> getNextEntries(FDate key, int shiftForwardUnits);

    V getNextValue(FDate key, int shiftForwardUnits);

}
