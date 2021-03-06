package de.invesdwin.util.math.decimal.scaled;

import java.nio.ByteBuffer;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.AScaledDecimal;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@SuppressWarnings("serial")
@Immutable
public class Percent extends AScaledDecimal<Percent, PercentScale> implements IPercentData {

    public static final PercentScale DEFAULT_SCALE;
    public static final Percent THREE_HUNDRED_PERCENT;
    public static final Percent TWO_HUNDRED_PERCENT;
    public static final Percent ONE_HUNDRED_PERCENT;
    public static final Percent NINETY_PERCENT;
    public static final Percent SEVENTYFIVE_PERCENT;
    public static final Percent FIFTY_PERCENT;
    public static final Percent TWENTYFIVE_PERCENT;
    public static final Percent TEN_PERCENT;
    public static final Percent FIVE_PERCENT;
    public static final Percent TWO_PERCENT;
    public static final Percent ONE_PERCENT;
    public static final Percent ZERO_PERCENT;
    public static final Percent MINUS_ONE_PERCENT;
    public static final Percent MINUS_TWO_PERCENT;
    public static final Percent MINUS_FIVE_PERCENT;
    public static final Percent MINUS_TEN_PERCENT;

    static {
        DEFAULT_SCALE = PercentScale.RATE;
        THREE_HUNDRED_PERCENT = new Percent(Decimal.THREE, PercentScale.RATE);
        TWO_HUNDRED_PERCENT = new Percent(Decimal.TWO, PercentScale.RATE);
        ONE_HUNDRED_PERCENT = new Percent(Decimal.ONE, PercentScale.RATE);
        NINETY_PERCENT = new Percent(new Decimal("90"), PercentScale.PERCENT);
        SEVENTYFIVE_PERCENT = new Percent(new Decimal("75"), PercentScale.PERCENT);
        FIFTY_PERCENT = new Percent(new Decimal("50"), PercentScale.PERCENT);
        TWENTYFIVE_PERCENT = new Percent(new Decimal("25"), PercentScale.PERCENT);
        TEN_PERCENT = new Percent(new Decimal("10"), PercentScale.PERCENT);
        FIVE_PERCENT = new Percent(new Decimal("5"), PercentScale.PERCENT);
        TWO_PERCENT = new Percent(new Decimal("2"), PercentScale.PERCENT);
        ONE_PERCENT = new Percent(Decimal.ONE, PercentScale.PERCENT);
        ZERO_PERCENT = new Percent(Decimal.ZERO, PercentScale.RATE);
        MINUS_ONE_PERCENT = ONE_PERCENT.negate();
        MINUS_TWO_PERCENT = TWO_PERCENT.negate();
        MINUS_FIVE_PERCENT = FIVE_PERCENT.negate();
        MINUS_TEN_PERCENT = TEN_PERCENT.negate();
    }

    public Percent(final Decimal value, final PercentScale scale) {
        super(value, scale, DEFAULT_SCALE);
    }

    public Percent(final Number dividend, final Number divisor) {
        this(Decimal.valueOf(dividend), Decimal.valueOf(divisor));
    }

    /**
     * Use default values of the scaled decimal instead! This constructor is functioning as a compiler warning for a
     * programming issue.
     */
    @Deprecated
    public Percent(final AScaledDecimal<?, ?> dividend, final Number divisor) throws Exception {
        super(Decimal.ZERO, PercentScale.PERCENT, DEFAULT_SCALE);
        throw new UnsupportedOperationException();
    }

    /**
     * Use default values of the scaled decimal instead! This constructor is functioning as a compiler warning for a
     * programming issue.
     */
    @Deprecated
    public Percent(final Number dividend, final AScaledDecimal<?, ?> divisor) throws Exception {
        super(null, null, DEFAULT_SCALE);
        throw new UnsupportedOperationException();
    }

    public Percent(final ADecimal<?> dividend, final ADecimal<?> divisor) {
        this(dividend.getDefaultValue().divide(divisor.getDefaultValue()), PercentScale.RATE);
    }

    public Percent(final Duration dividend, final Duration divisor) {
        this(dividend.doubleValue(FTimeUnit.MILLISECONDS), divisor.doubleValue(FTimeUnit.MILLISECONDS));
    }

    public <T extends AScaledDecimal<T, ?>> Percent(final AScaledDecimal<T, ?> dividend,
            final AScaledDecimal<T, ?> divisor) {
        this(dividend.getDefaultValue().divide(divisor.getDefaultValue()), PercentScale.RATE);
    }

    public Percent(final Percent percent) {
        this(percent.getValue(percent.getScale()), percent.getScale());
    }

    @Override
    protected Percent getGenericThis() {
        return this;
    }

    @Override
    protected Percent newValueCopy(final Decimal value, final PercentScale scale) {
        return new Percent(value, scale);
    }

    @Override
    public Percent zero() {
        return ZERO_PERCENT;
    }

    public static Percent nullToZero(final Percent value) {
        if (value == null) {
            return ZERO_PERCENT;
        } else {
            return value;
        }
    }

    @Override
    public Decimal getRate() {
        return getDefaultValue();
    }

    /**
     * (newValue - oldValue) / abs(oldValue)
     */
    public static <T extends ADecimal<T>> Percent relativeDifference(final ADecimal<T> oldValue,
            final ADecimal<T> newValue) {
        return new Percent(newValue.subtract(oldValue), oldValue.abs());
    }

    public static void putPercent(final ByteBuffer buffer, final Percent value) {
        if (value == null) {
            buffer.putDouble(Double.MIN_VALUE);
        } else {
            buffer.putDouble(value.getRate().doubleValueRaw());
        }
    }

    public static Percent extractPercent(final ByteBuffer buffer, final int index) {
        final double value = buffer.getDouble(index);
        if (value == Double.MIN_VALUE) {
            return null;
        } else {
            return new Percent(new Decimal(value), PercentScale.RATE);
        }
    }

}
