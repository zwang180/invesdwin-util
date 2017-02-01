package de.invesdwin.util.math.decimal.internal.resample;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.decimal.internal.DecimalAggregate;
import de.invesdwin.util.math.decimal.internal.resample.blocklength.StationaryOptimalBlockLength;

@ThreadSafe
public class StationaryResampler<E extends ADecimal<E>> extends CircularResampler<E> {

    private final double divisor;

    public StationaryResampler(final DecimalAggregate<E> parent) {
        super(parent);
        final int superBlockLength = super.nextBlockLength(null);
        divisor = Math.log(1D - (1D / superBlockLength)) * -1D;
    }

    @Override
    protected int newOptimalBlockLength(final IDecimalAggregate<E> parent) {
        return new StationaryOptimalBlockLength<E>(parent).getBlockLength();
    }

    @Override
    protected int nextBlockLength(final RandomGenerator random) {
        //we randomize the block length for the stationary bootstrap
        final int newBlockLength = Math.round((float) (random.nextDouble() / divisor));
        return Math.max(1, newBlockLength);
    }

}