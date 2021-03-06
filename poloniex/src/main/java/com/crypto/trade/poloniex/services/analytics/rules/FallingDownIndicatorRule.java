package com.crypto.trade.poloniex.services.analytics.rules;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.TradingRecord;
import eu.verdelhan.ta4j.trading.rules.AbstractRule;

public class FallingDownIndicatorRule extends AbstractRule {

    private Indicator<Decimal> indicator;
    private int depth;

    public FallingDownIndicatorRule(Indicator<Decimal> indicator) {
        this.indicator = indicator;
        this.depth = 0;
    }

    public FallingDownIndicatorRule(Indicator<Decimal> indicator, int depth) {
        this.indicator = indicator;
        this.depth = depth;
    }

    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord) {
        boolean satisfied = false;
        int prevIndex = index - depth - 1;
        int currIndex = index - depth;
        if (prevIndex > -1) {
            satisfied = indicator.getValue(prevIndex).isGreaterThan(indicator.getValue(currIndex));
        }
        traceIsSatisfied(currIndex, satisfied);
        return satisfied;
    }
}
