package com.crypto.trade.poloniex.services.analytics;

import com.crypto.trade.poloniex.services.analytics.indicators.CachedDoubleEMAIndicator;
import com.crypto.trade.poloniex.services.analytics.indicators.CachedTripleEMAIndicator;
import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.oscillators.StochasticOscillatorDIndicator;
import eu.verdelhan.ta4j.indicators.oscillators.StochasticOscillatorKIndicator;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.EMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.RSIIndicator;

public class IndicatorFactory {

    public RSIIndicator createRsi14Indicator(ClosePriceIndicator closePrice) {
        return new RSIIndicator(closePrice, 14);
    }

    public StochasticOscillatorKIndicator createStochK14(TimeSeries timeSeries) {
        return new StochasticOscillatorKIndicator(timeSeries, 14);
    }

    public StochasticOscillatorDIndicator createStochD3(StochasticOscillatorKIndicator stochK14) {
        return new StochasticOscillatorDIndicator(stochK14);
    }

    public EMAIndicator createEma5Indicator(ClosePriceIndicator closePrice) {
        return new EMAIndicator(closePrice, 5);
    }

    public EMAIndicator createEma90Indicator(ClosePriceIndicator closePrice) {
        return new EMAIndicator(closePrice, 90);
    }

    public EMAIndicator createEma540Indicator(ClosePriceIndicator closePrice) {
        return new EMAIndicator(closePrice, 540);
    }

    public EMAIndicator createEma100Indicator(ClosePriceIndicator closePrice) {
        return new EMAIndicator(closePrice, 100);
    }

    public EMAIndicator createEmaEma90Indicator(EMAIndicator ema) {
        return new EMAIndicator(ema, 90);
    }

    public Indicator<Decimal> createEmaEmaEma90Indicator(EMAIndicator emaEma) {
        return new EMAIndicator(emaEma, 90);
    }

    public CachedDoubleEMAIndicator createDma90Indicator(ClosePriceIndicator closePrice, EMAIndicator ema, EMAIndicator emaEma) {
        return new CachedDoubleEMAIndicator(closePrice, ema, emaEma);
    }

    public CachedTripleEMAIndicator createTma90Indicator(ClosePriceIndicator closePrice, EMAIndicator ema, EMAIndicator emaEma, EMAIndicator emaEmaEma) {
        return new CachedTripleEMAIndicator(closePrice, ema, emaEma, emaEmaEma);
    }
}
