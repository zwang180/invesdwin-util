package de.invesdwin.util.math.decimal.config;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.scaled.Percent;

@NotThreadSafe
public class LoessInterpolationConfig extends InterpolationConfig {

    private Percent smoothness = Percent.FIFTY_PERCENT;

    public Percent getSmoothness() {
        return smoothness;
    }

    public LoessInterpolationConfig withSmoothness(final Percent smoothness) {
        this.smoothness = smoothness;
        return this;
    }

    @Override
    public LoessInterpolationConfig withPunishEdges(final boolean punishEdges, final boolean higherIsBetter) {
        return (LoessInterpolationConfig) super.withPunishEdges(punishEdges, higherIsBetter);
    }

}
