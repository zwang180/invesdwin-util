package de.invesdwin.util.math.decimal;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.dfp.Dfp;
import org.assertj.core.description.TextDescription;

import de.invesdwin.norva.marker.IDecimal;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.decimal.internal.impl.ADecimalImpl;

@Immutable
public abstract class ADecimal<E extends ADecimal<E>> extends Number implements Comparable<Object>, IDecimal {

    public static final String NEGATIVE_SIGN = "-";
    public static final String POSITIVE_SIGN = "+";
    /**
     * Using HALF_UP here for commercial rounding.
     */
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;
    public static final int DEFAULT_ROUNDING_SCALE = 9;
    public static final ADelegateComparator<ADecimal<?>> COMPARATOR = new ADelegateComparator<ADecimal<?>>() {
        @Override
        protected Comparable<?> getCompareCriteria(final ADecimal<?> e) {
            return e;
        }
    };

    protected transient Boolean isZero;
    protected transient Boolean isPositive;

    public abstract ADecimalImpl getImpl();

    /**
     * http://stackoverflow.com/questions/2170872/does-java-casting-introduce-overhead-why
     */
    protected abstract E getGenericThis();

    protected abstract E newValueCopy(ADecimalImpl value);

    public abstract E fromDefaultValue(Decimal value);

    public List<E> fromDefaultValue(final List<Decimal> values) {
        if (values == null) {
            return null;
        }
        final List<E> converted = new ArrayList<E>(values.size());
        for (final Decimal value : values) {
            converted.add(fromDefaultValue(value));
        }
        return converted;
    }

    @SuppressWarnings("unchecked")
    public E[] fromDefaultValue(final Decimal[] values) {
        if (values == null) {
            return null;
        }
        final E[] converted = (E[]) Array.newInstance(getGenericThis().getClass(), values.length);
        return fromDefaultValue(values, converted);
    }

    public E[] fromDefaultValue(final Decimal[] values, final E[] destination) {
        for (int i = 0; i < values.length; i++) {
            destination[i] = fromDefaultValue(values[i]);
        }
        return destination;
    }

    public abstract Decimal getDefaultValue();

    @Override
    public int hashCode() {
        return Objects.hashCode(getClass(), getImpl());
    }

    @Override
    public int compareTo(final Object other) {
        return getImpl().compareTo(other);
    }

    @Override
    public boolean equals(final Object other) {
        return other != null && getClass().isAssignableFrom(other.getClass()) && Objects.equals(getImpl(), other);
    }

    @Override
    public String toString() {
        return getImpl().toString();
    }

    @Override
    public int intValue() {
        return getImpl().intValue();
    }

    @Override
    public long longValue() {
        return getImpl().longValue();
    }

    @Override
    public float floatValue() {
        return getImpl().floatValue();
    }

    @Override
    public double doubleValue() {
        return getImpl().doubleValue();
    }

    /**
     * This gives the raw value, thus not getting rounded for precision.
     */
    public double doubleValueRaw() {
        return getImpl().doubleValueRaw();
    }

    @Override
    public byte byteValue() {
        return getImpl().byteValue();
    }

    @Override
    public short shortValue() {
        return getImpl().shortValue();
    }

    public BigDecimal bigDecimalValue() {
        return getImpl().bigDecimalValue();
    }

    public BigInteger bigIntegerValue() {
        return getImpl().bigIntegerValue();
    }

    public Dfp dfpValue() {
        return getImpl().dfpValue();
    }

    public Number numberValue() {
        return getImpl().numberValue();
    }

    /**
     * This value denotes how many digits are after the decimal point. E.g.: 12.0001 results in 4
     * 
     * Also called decimal scale.
     * 
     * Returns the real scale without trailing zeros.
     * 
     * WARNING: make sure this values gets cached, because this operation causes a large performance overhead!
     */
    public int getDecimalDigits() {
        return getImpl().getDecimalDigits();
    }

    /**
     * This value denotes how many digits are before the decimal point. E.g.: 12.0001 results in 2
     * 
     * Also called decimal precision minus decimal scale.
     * 
     * Returns the real scale without trailing zeros.
     * 
     * WARNING: make sure this values gets cached, because this operation causes a large performance overhead!
     */
    public int getWholeNumberDigits() {
        return getImpl().getWholeNumberDigits();
    }

    /**
     * This value denotes how many digits are after the decimal point. E.g.: 12.0001 results in 6
     * 
     * Also called decimal precision.
     * 
     * Returns the real precision without trailing zeros.
     * 
     * WARNING: make sure this values gets cached, because this operation causes a large performance overhead!
     */
    public int getDigits() {
        return getImpl().getDigits();
    }

    public boolean isZero() {
        if (isZero == null) {
            isZero = getImpl().isZero();
        }
        return isZero;
    }

    public final boolean isNotZero() {
        return !isZero();
    }

    /**
     * 0 is counted as positive as well here to make things simpler.
     */
    public boolean isPositive() {
        if (isPositive == null) {
            isPositive = getImpl().isPositive();
        }
        return isPositive;
    }

    /**
     * This one excludes 0 from positive.
     */
    public boolean isPositiveNonZero() {
        return isPositive() && !isZero();
    }

    public boolean isNegative() {
        return !isPositive();
    }

    public boolean isNegativeOrZero() {
        return !isPositiveNonZero();
    }

    public boolean isGreaterThan(final Number o) {
        if (o == null) {
            return false;
        }
        return compareTo(o) > 0;
    }

    public boolean isGreaterThanOrEqualTo(final Number o) {
        return !isLessThan(o);
    }

    public boolean isLessThan(final Number o) {
        if (o == null) {
            return false;
        }
        return compareTo(o) < 0;
    }

    public boolean isLessThanOrEqualTo(final Number o) {
        return !isGreaterThan(o);
    }

    public boolean isBetween(final Number lowerBound, final Number upperBound) {
        return isGreaterThanOrEqualTo(lowerBound) && isLessThanOrEqualTo(upperBound);
    }

    public E scaleByPowerOfTen(final int n) {
        return newValueCopy(getImpl().scaleByPowerOfTen(n));
    }

    public E round() {
        return round(DEFAULT_ROUNDING_SCALE);
    }

    public E round(final RoundingMode roundingMode) {
        return round(DEFAULT_ROUNDING_SCALE, roundingMode);
    }

    public E round(final int scale) {
        return round(scale, DEFAULT_ROUNDING_MODE);
    }

    public E round(final int scale, final RoundingMode roundingMode) {
        return newValueCopy(getImpl().round(scale, roundingMode));
    }

    /**
     * <ul>
     * <li>[growth rate] = ( [current] - [previous] ) / |[previous]|</li>
     * <ul>
     * 
     * @see <a href="http://www.chemieonline.de/forum/showthread.php?t=101560">Source</a>
     */
    public E growthRate(final ADecimal<E> nextValue) {
        return nextValue.subtract(getGenericThis()).divide(abs());
    }

    public E abs() {
        return newValueCopy(getImpl().abs());
    }

    /**
     * Returns the natural logarithm.
     */
    public E log() {
        if (isNegativeOrZero()) {
            return zero();
        }
        return newValueCopy(getImpl().log());
    }

    /**
     * Returns the logarithm base 10.
     */
    public E log10() {
        if (isNegativeOrZero()) {
            return zero();
        }
        return newValueCopy(getImpl().log10());
    }

    /**
     * Returns Euler's number <i>e</i> raised to the power of this value.
     */
    public E exp() {
        return newValueCopy(getImpl().exp());
    }

    public E cos() {
        return newValueCopy(getImpl().cos());
    }

    public E sin() {
        return newValueCopy(getImpl().sin());
    }

    /**
     * Returns 10 raised to the power of this value.
     */
    public E exp10() {
        return newValueCopy(getImpl().exp10());
    }

    public E subtract(final ADecimal<E> subtrahend) {
        if (subtrahend == null) {
            return getGenericThis();
        }
        return newValueCopy(getImpl().subtract(subtrahend));
    }

    public E add(final ADecimal<E> augend) {
        if (augend == null) {
            return getGenericThis();
        }
        return newValueCopy(getImpl().add(augend));
    }

    public E multiply(final ADecimal<E> multiplicant) {
        if (isZero()) {
            return getGenericThis();
        } else if (multiplicant == null) {
            return multiply(0);
        } else {
            return newValueCopy(getImpl().multiply(multiplicant));
        }
    }

    /**
     * returns the remainder of the division.
     */
    public E remainder(final ADecimal<E> divisor) {
        if (isZero()) {
            return getGenericThis();
        } else if (divisor == null || divisor.isZero()) {
            return remainder(0);
        } else {
            return newValueCopy(getImpl().remainder(divisor));
        }
    }

    public E remainder(final Number divisor) {
        if (isZero()) {
            return getGenericThis();
        } else if (divisor == null || divisor.doubleValue() == 0D) {
            //results in 0, thus multiply by 0
            return newValueCopy(getImpl().remainder(0));
        } else {
            if (divisor instanceof AScaledDecimal) {
                throw new IllegalArgumentException(new TextDescription(
                        "Division between different types of %ss [%s=%s / %s=%s] does not make any sense. Please be more specific.",
                        AScaledDecimal.class.getSimpleName(), this.getClass().getSimpleName(), this,
                        divisor.getClass().getSimpleName(), divisor).toString());
            }
            return newValueCopy(getImpl().remainder(divisor));
        }
    }

    public E multiply(final Number multiplicant) {
        if (isZero()) {
            return getGenericThis();
        } else if (multiplicant == null) {
            return newValueCopy(getImpl().multiply(0));
        } else {
            if (multiplicant instanceof AScaledDecimal) {
                throw new IllegalArgumentException(new TextDescription(
                        "Multiplication between different types of %ss [%s=%s * %s=%s] does not make any sense. Please be more specific.",
                        AScaledDecimal.class.getSimpleName(), this.getClass().getSimpleName(), this,
                        multiplicant.getClass().getSimpleName(), multiplicant).toString());
            }
            return newValueCopy(getImpl().multiply(multiplicant));
        }
    }

    /**
     * If the divisor is 0, 0 is returned. This goes against the mathematical rules, but makes a developers life easier.
     */
    public E divide(final ADecimal<E> divisor) {
        if (isZero()) {
            //prevent NaN
            return getGenericThis();
        } else if (divisor == null || divisor.isZero()) {
            return divide(0);
        } else {
            return newValueCopy(getImpl().divide(divisor));
        }
    }

    /**
     * If the divisor is 0, 0 is returned. This goes against the mathematical rules, but makes a developers life easier.
     */
    public E divide(final Number divisor) {
        if (isZero()) {
            //prevent NaN
            return getGenericThis();
        } else if (divisor == null || divisor.doubleValue() == 0D) {
            //results in 0, thus multiply by 0
            return newValueCopy(getImpl().multiply(0));
        } else {
            if (divisor instanceof AScaledDecimal) {
                throw new IllegalArgumentException(new TextDescription(
                        "Division between different types of %ss [%s=%s / %s=%s] does not make any sense. Please be more specific.",
                        AScaledDecimal.class.getSimpleName(), this.getClass().getSimpleName(), this,
                        divisor.getClass().getSimpleName(), divisor).toString());
            }
            return newValueCopy(getImpl().divide(divisor));
        }
    }

    /**
     * With a step of 0.5: (Math.ceil(x * 2) / 2)
     */
    public E roundToStep(final ADecimal<E> step) {
        return roundToStep(step, DEFAULT_ROUNDING_MODE);
    }

    public E roundToStep(final ADecimal<E> step, final RoundingMode roundingMode) {
        final E stepReciprocal = step.reciprocal();
        return multiply(stepReciprocal).round(0, roundingMode).divide(stepReciprocal);
    }

    public E reciprocal() {
        return newValueCopy(Decimal.ONE.getImpl()).divide(getGenericThis());
    }

    public E orHigher(final E other) {
        if (other == null) {
            return getGenericThis();
        }

        if (compareTo(other) > 0) {
            return getGenericThis();
        } else {
            return other;
        }
    }

    public E orLower(final E other) {
        if (other == null) {
            return getGenericThis();
        }

        if (compareTo(other) < 0) {
            return getGenericThis();
        } else {
            return other;
        }
    }

    public E between(final E min, final E max) {
        if (min.isGreaterThan(max)) {
            throw new IllegalArgumentException("min [" + min + "] must not be larger than max [" + max + "]");
        }
        return orLower(max).orHigher(min);
    }

    public E pow(final ADecimal<E> exponent) {
        return newValueCopy(getImpl().pow(exponent));
    }

    public E pow(final Number exponent) {
        return newValueCopy(getImpl().pow(exponent));
    }

    public E sqrt() {
        return newValueCopy(getImpl().sqrt());
    }

    /**
     * Root = Value^1/n
     * 
     * @see <a href="http://www.ee.ucl.ac.uk/~mflanaga/java/Stat.html#geom2">Source with BigDecimal</a>
     */
    public E root(final Number n) {
        return newValueCopy(getImpl().root(n));
    }

    public E distance(final ADecimal<E> to) {
        return subtract(to).abs();
    }

    public E negate() {
        return multiply(-1);
    }

    public String getSign() {
        return getSign(false);
    }

    public String getSignInverted() {
        return getSign(true);
    }

    public String getSign(final boolean inverted) {
        if (isPositive()) {
            if (!inverted) {
                return POSITIVE_SIGN;
            } else {
                return NEGATIVE_SIGN;
            }
        } else {
            if (!inverted) {
                return NEGATIVE_SIGN;
            } else {
                return POSITIVE_SIGN;
            }
        }
    }

    public abstract E zero();

    public static <T extends ADecimal<T>> T zeroToNull(final T value) {
        if (value == null || value.isZero()) {
            return null;
        } else {
            return value;
        }
    }

    public static <T extends ADecimal<T>> T sum(final T value1, final T value2) {
        if (value1 == null) {
            return value2;
        } else {
            return value1.add(value2);
        }
    }

    public static <T extends ADecimal<T>> T max(final T value1, final T value2) {
        if (value1 == null) {
            return value2;
        } else {
            return value1.orHigher(value2);
        }
    }

    public static <T extends ADecimal<T>> T min(final T value1, final T value2) {
        if (value1 == null) {
            return value2;
        } else {
            return value1.orLower(value2);
        }
    }

    public static <T extends ADecimal<T>> T negate(final T value) {
        if (value == null) {
            return null;
        } else {
            return value.negate();
        }
    }

    public static <T extends ADecimal<T>> T abs(final T value) {
        if (value == null) {
            return null;
        } else {
            return value.abs();
        }
    }

    public abstract String toFormattedString();

    public abstract String toFormattedString(final String format);

}