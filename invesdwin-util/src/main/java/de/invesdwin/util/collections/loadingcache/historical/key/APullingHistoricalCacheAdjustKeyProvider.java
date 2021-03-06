package de.invesdwin.util.collections.loadingcache.historical.key;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.BooleanUtils;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.HistoricalCacheForClear;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.time.fdate.FDate;
import io.netty.util.concurrent.FastThreadLocal;

@ThreadSafe
public abstract class APullingHistoricalCacheAdjustKeyProvider implements IHistoricalCacheAdjustKeyProvider {

    private final FastThreadLocal<Boolean> alreadyAdjustingKey = new FastThreadLocal<Boolean>();

    private volatile FDate curHighestAllowedKey;
    private final Set<FDate> keysToRemoveOnNewHighestAllowedKey = Collections.synchronizedSet(new HashSet<FDate>());
    private final Set<HistoricalCacheForClear> historicalCachesForClear = Collections
            .synchronizedSet(new HashSet<HistoricalCacheForClear>());
    private final AHistoricalCache<?> parent;

    public APullingHistoricalCacheAdjustKeyProvider(final AHistoricalCache<?> parent) {
        this.parent = parent;
    }

    @Override
    public AHistoricalCache<?> getParent() {
        return parent;
    }

    @Override
    public FDate adjustKey(final FDate key) {
        if (BooleanUtils.isNotTrue(alreadyAdjustingKey.get())) {
            alreadyAdjustingKey.set(true);
            try {
                final FDate newHighestAllowedKey = getHighestAllowedKeyUpdateCached();
                if (newHighestAllowedKey != null) {
                    if (key.millisValue() > newHighestAllowedKey.millisValue()) {
                        return newHighestAllowedKey;
                    }
                }
            } finally {
                alreadyAdjustingKey.remove();
            }
        } else {
            rememberKeyToRemove(key);
        }
        return key;
    }

    @SuppressWarnings("deprecation")
    private FDate getHighestAllowedKeyUpdateCached() {
        final FDate newHighestAllowedKey = innerGetHighestAllowedKey();
        if (newHighestAllowedKey != null) {
            final FDate curHighestAllowedKeyCopy = curHighestAllowedKey;
            final boolean purge = curHighestAllowedKeyCopy == null;
            if (purge) {
                //purge maybe already remembered keys above curHighestAllowedKey
                clear();
            }

            if (purge || curHighestAllowedKeyCopy.isBefore(newHighestAllowedKey)) {
                for (final FDate keyToRemove : keysToRemoveOnNewHighestAllowedKey) {
                    //only parent will actually be used to search without being adjusted
                    //and we don't want to keep references to all those others using this properly
                    parent.remove(keyToRemove);
                }
                curHighestAllowedKey = newHighestAllowedKey;
                keysToRemoveOnNewHighestAllowedKey.clear();
            }
        }
        return newHighestAllowedKey;
    }

    @Override
    public FDate getHighestAllowedKey() {
        if (curHighestAllowedKey == null && BooleanUtils.isNotTrue(alreadyAdjustingKey.get())) {
            alreadyAdjustingKey.set(true);
            try {
                final FDate newHighestAllowedKey = getHighestAllowedKeyUpdateCached();
                curHighestAllowedKey = newHighestAllowedKey;
            } finally {
                alreadyAdjustingKey.remove();
            }
        }
        return curHighestAllowedKey;
    }

    private void rememberKeyToRemove(final FDate key) {
        if (curHighestAllowedKey != null && key.isAfter(curHighestAllowedKey)) {
            keysToRemoveOnNewHighestAllowedKey.add(key);
        }
    }

    protected abstract FDate innerGetHighestAllowedKey();

    @Override
    public void clear() {
        keysToRemoveOnNewHighestAllowedKey.clear();
        //        curHighestAllowedKey = null; // dont clear highestallowedkey or else backtests might get confused
        if (!historicalCachesForClear.isEmpty()) {
            //make copy to prevent recusion
            final List<HistoricalCacheForClear> historicalCachesForClearCopy = new ArrayList<HistoricalCacheForClear>(
                    historicalCachesForClear);
            //remove references to prevent memory leaks
            historicalCachesForClear.clear();
            for (final HistoricalCacheForClear c : historicalCachesForClearCopy) {
                c.clear();
            }
        }
    }

    @Override
    public boolean registerHistoricalCache(final AHistoricalCache<?> historicalCache) {
        if (curHighestAllowedKey == null) {
            return historicalCachesForClear.add(new HistoricalCacheForClear(historicalCache));
        } else {
            historicalCache.clear();
            return true;
        }
    }

    @Override
    public FDate newAlreadyAdjustedKey(final FDate key) {
        return AdjustedFDate.newAdjustedKey(this, key);
    }

    @Override
    public FDate maybeAdjustKey(final FDate key) {
        return AdjustedFDate.maybeAdjustKey(this, key);
    }

    @Override
    public final <T> IHistoricalCacheQuery<T> newQuery(
            final de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore<T> queryCore) {
        return new de.invesdwin.util.collections.loadingcache.historical.query.internal.adj.AdjustingHistoricalCacheQuery<T>(
                queryCore);
    }

}
