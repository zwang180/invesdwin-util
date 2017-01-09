package de.invesdwin.util.time.fdate;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.TimeZone;

import javax.annotation.concurrent.ThreadSafe;

import org.joda.time.DateTimeZone;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public final class FDates {

    private static Calendar templateCalendar;
    private static TimeZone defaultTimeZone;
    private static DateTimeZone defaultDateTimeZone;

    static {
        setDefaultTimeZone(TimeZone.getDefault());
    }

    private FDates() {}

    public static void setDefaultTimeZone(final TimeZone defaultTimeZone) {
        FDates.defaultTimeZone = defaultTimeZone;
        FDates.defaultDateTimeZone = DateTimeZone.forTimeZone(defaultTimeZone);
        //CHECKSTYLE:OFF
        final Calendar cal = Calendar.getInstance();
        //CHECKSTYLE:ON
        cal.clear();
        cal.setTimeZone(defaultTimeZone);
        templateCalendar = cal;
    }

    public static TimeZone getDefaultTimeZone() {
        return defaultTimeZone;
    }

    public static DateTimeZone getDefaultDateTimeZone() {
        return defaultDateTimeZone;
    }

    public static Calendar newCalendar() {
        return (Calendar) templateCalendar.clone();
    }

    public static ICloseableIterable<FDate> iterable(final FDate start, final FDate end, final Duration increment) {
        return new FDateIterable(start, end, increment.getTimeUnit(), increment.intValue());
    }

    public static ICloseableIterable<FDate> iterable(final FDate start, final FDate end, final FTimeUnit timeUnit,
            final int incrementAmount) {
        return new FDateIterable(start, end, timeUnit, incrementAmount);
    }

    static class FDateIterable implements ICloseableIterable<FDate> {
        private final FDate startFinal;
        private final FDate endFinal;
        private final FTimeUnit timeUnit;
        private final int incrementAmount;

        FDateIterable(final FDate startFinal, final FDate endFinal, final FTimeUnit timeUnit,
                final int incrementAmount) {
            this.startFinal = startFinal;
            this.endFinal = endFinal;
            this.timeUnit = timeUnit;
            this.incrementAmount = incrementAmount;
            if (incrementAmount == 0) {
                throw new IllegalArgumentException("incrementAmount must not be 0");
            }
            if (startFinal.isBefore(endFinal) && incrementAmount < 0) {
                throw new IllegalArgumentException("When iterating forward [" + startFinal + " -> " + endFinal
                        + "], incrementAmount [" + incrementAmount + "] needs to be positive.");
            } else if (startFinal.isAfter(endFinal) && incrementAmount > 0) {
                throw new IllegalArgumentException("When iterating backward [" + startFinal + " -> " + endFinal
                        + "], incrementAmount [" + incrementAmount + "] needs to be negative.");
            }
        }

        @Override
        public ICloseableIterator<FDate> iterator() {
            if (incrementAmount > 0) {
                return new ICloseableIterator<FDate>() {

                    private boolean first = true;
                    private FDate spot = startFinal;

                    @Override
                    public boolean hasNext() {
                        return first || spot.isBefore(endFinal);
                    }

                    @Override
                    public FDate next() {
                        if (first) {
                            first = false;
                            return spot;
                        } else {
                            if (spot.isAfter(endFinal)) {
                                throw new NoSuchElementException();
                            }
                            spot = spot.add(timeUnit, incrementAmount);
                            if (spot.isAfter(endFinal)) {
                                return endFinal;
                            } else {
                                return spot;
                            }
                        }
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void close() {
                        spot = endFinal;
                    }
                };
            } else {
                //reverse
                return new ICloseableIterator<FDate>() {

                    private boolean first = true;
                    private FDate spot = startFinal;

                    @Override
                    public boolean hasNext() {
                        return first || spot.isAfter(endFinal);
                    }

                    @Override
                    public FDate next() {
                        if (first) {
                            first = false;
                            return spot;
                        } else {
                            if (spot.isBefore(endFinal)) {
                                throw new NoSuchElementException();
                            }
                            spot = spot.add(timeUnit, incrementAmount);
                            if (spot.isBefore(endFinal)) {
                                return endFinal;
                            } else {
                                return spot;
                            }
                        }
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void close() {
                        spot = endFinal;
                    }
                };

            }

        }
    }

    public static Object toString(final FDate date) {
        if (date == null) {
            return null;
        } else {
            return date.toString();
        }
    }

    public static String toString(final FDate date, final TimeZone timeZone) {
        if (date == null) {
            return null;
        }
        return date.toString(timeZone);
    }

    public static String toString(final FDate date, final String format) {
        if (date == null) {
            return null;
        }
        return date.toString(format);
    }

    public static String toString(final FDate date, final String format, final TimeZone timeZone) {
        if (date == null) {
            return null;
        }
        return date.toString(format, timeZone);
    }

    public static FDate min(final FDate... dates) {
        FDate minDate = null;
        for (final FDate date : dates) {
            minDate = min(minDate, date);
        }
        return minDate;
    }

    public static FDate min(final Iterable<FDate> dates) {
        FDate minDate = null;
        for (final FDate date : dates) {
            minDate = min(minDate, date);
        }
        return minDate;
    }

    public static FDate min(final FDate date1, final FDate date2) {
        if (date1 == null) {
            return date2;
        } else if (date2 == null) {
            return date1;
        }

        if (date1.isBefore(date2)) {
            return date1;
        } else {
            return date2;
        }
    }

    public static FDate max(final Iterable<FDate> dates) {
        FDate maxDate = null;
        for (final FDate date : dates) {
            maxDate = max(maxDate, date);
        }
        return maxDate;
    }

    public static FDate max(final FDate... dates) {
        FDate maxDate = null;
        for (final FDate date : dates) {
            maxDate = max(maxDate, date);
        }
        return maxDate;
    }

    public static FDate max(final FDate date1, final FDate date2) {
        if (date1 == null) {
            return date2;
        } else if (date2 == null) {
            return date1;
        }

        if (date1.isAfter(date2)) {
            return date1;
        } else {
            return date2;
        }
    }

    public static FDate between(final FDate value, final FDate min, final FDate max) {
        return max(min(value, max), min);
    }

    public static boolean isBetween(final FDate value, final FDate min, final FDate max) {
        return between(value, min, max).equals(value);
    }

    public static boolean isSameYear(final FDate date1, final FDate date2) {
        return isSameTruncated(date1, date2, FDateField.Year);
    }

    public static boolean isSameMonth(final FDate date1, final FDate date2) {
        return isSameTruncated(date1, date2, FDateField.Month);
    }

    public static boolean isSameWeek(final FDate date1, final FDate date2) {
        return isSameWeekPart(date1, date2, FWeekday.Monday, FWeekday.Sunday);
    }

    public static boolean isSameWeekPart(final FDate date1, final FDate date2, final FWeekday statOfWeekPart,
            final FWeekday endOfWeekPart) {
        if (date1 == null || date2 == null) {
            return false;
        }
        final FDate startOfWeek = date1.withoutTime().setFWeekday(statOfWeekPart);
        final FDate endOfWeek = date1.withoutTime().setFWeekday(endOfWeekPart).addDays(1).addMilliseconds(-1);
        if (startOfWeek.isAfter(endOfWeek)) {
            throw new IllegalStateException(
                    "startOfWeek [" + startOfWeek + "] should not be after [" + endOfWeek + "]");
        }
        return FDates.isBetween(date2, startOfWeek, endOfWeek);
    }

    public static boolean isWeekdayBetween(final FDate date1, final FDate date2, final FWeekday weekday) {
        final FDate from = date1.withoutTime();
        final FDate to = date2.withoutTime();
        if (to.isBefore(from)) {
            return false;
        }
        for (final FDate day : iterable(from, to, FTimeUnit.DAYS, 1)) {
            if (day.getFWeekday() == weekday) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSameDay(final FDate date1, final FDate date2) {
        return isSameTruncated(date1, date2, FDateField.Day);
    }

    public static boolean isSameHour(final FDate date1, final FDate date2) {
        return isSameTruncated(date1, date2, FDateField.Hour);
    }

    public static boolean isSameMinute(final FDate date1, final FDate date2) {
        return isSameTruncated(date1, date2, FDateField.Minute);
    }

    public static boolean isSameSecond(final FDate date1, final FDate date2) {
        return isSameTruncated(date1, date2, FDateField.Second);
    }

    public static boolean isSameMillisecond(final FDate date1, final FDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        } else {
            return date1.millisValue() == date2.millisValue();
        }
    }

    private static boolean isSameTruncated(final FDate date1, final FDate date2, final FDateField field) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.millisValue() == date2.millisValue()
                || date1.truncate(field).millisValue() == date2.truncate(field).millisValue();
    }

    public static boolean isSamePeriod(final FDate date1, final FDate date2, final FTimeUnit period) {
        switch (period) {
        case MILLISECONDS:
            return isSameMillisecond(date1, date2);
        case SECONDS:
            return isSameSecond(date1, date2);
        case MINUTES:
            return isSameMinute(date1, date2);
        case HOURS:
            return isSameHour(date1, date2);
        case DAYS:
            return isSameDay(date1, date2);
        case WEEKS:
            return isSameWeek(date1, date2);
        case MONTHS:
            return isSameMonth(date1, date2);
        case YEARS:
            return isSameYear(date1, date2);
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, period);
        }
    }

    /**
     * Fast but unprecise variation of isSameDay(). Does not count in daylight saving time.
     */
    public static boolean isSameJulianDay(final FDate date1, final FDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        // Strip out the time part of each date.
        final long julianDayNumber1 = date1.millisValue() / org.apache.commons.lang3.time.DateUtils.MILLIS_PER_DAY;
        final long julianDayNumber2 = date2.millisValue() / org.apache.commons.lang3.time.DateUtils.MILLIS_PER_DAY;

        // If they now are equal then it is the same day.
        return julianDayNumber1 == julianDayNumber2;
    }

    /**
     * Fast but unprecise variation of isSameDay(). Does not count in daylight saving time.
     */
    public static boolean isSameJulianHour(final FDate date1, final FDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        // Strip out the time part of each date.
        final long julianHourNumber1 = date1.millisValue() / org.apache.commons.lang3.time.DateUtils.MILLIS_PER_HOUR;
        final long julianHourNumber2 = date2.millisValue() / org.apache.commons.lang3.time.DateUtils.MILLIS_PER_HOUR;

        // If they now are equal then it is the same day.
        return julianHourNumber1 == julianHourNumber2;
    }

    /**
     * Fast but unprecise variation of isSameDay(). Does not count in daylight saving time.
     */
    public static boolean isSameJulianMinute(final FDate date1, final FDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        // Strip out the time part of each date.
        final long julianMinuteNumber1 = date1.millisValue() / org.apache.commons.lang3.time.DateUtils.MILLIS_PER_MINUTE;
        final long julianMinuteNumber2 = date2.millisValue() / org.apache.commons.lang3.time.DateUtils.MILLIS_PER_MINUTE;

        // If they now are equal then it is the same day.
        return julianMinuteNumber1 == julianMinuteNumber2;
    }

    /**
     * Fast but unprecise variation of isSameDay(). Does not count in daylight saving time.
     */
    public static boolean isSameJulianSecond(final FDate date1, final FDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        // Strip out the time part of each date.
        final long julianSecondNumber1 = date1.millisValue() / org.apache.commons.lang3.time.DateUtils.MILLIS_PER_SECOND;
        final long julianSecondNumber2 = date2.millisValue() / org.apache.commons.lang3.time.DateUtils.MILLIS_PER_SECOND;

        // If they now are equal then it is the same day.
        return julianSecondNumber1 == julianSecondNumber2;
    }

    public static Date toDate(final FDate date) {
        if (date != null) {
            return date.dateValue();
        } else {
            return null;
        }
    }

    public static FDate avg(final FDate first, final FDate second) {
        return new FDate((first.millisValue() + second.millisValue()) / 2);
    }

    public static FDate avg(final FDate... values) {
        long sum = 0;
        for (final FDate value : values) {
            sum += value.millisValue();
        }
        return new FDate(sum / values.length);
    }

    public static FDate avg(final Collection<FDate> values) {
        long sum = 0;
        for (final FDate value : values) {
            sum += value.millisValue();
        }
        return new FDate(sum / values.size());
    }

}
