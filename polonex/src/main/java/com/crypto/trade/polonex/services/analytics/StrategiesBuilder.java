package com.crypto.trade.polonex.services.analytics;

import com.crypto.trade.polonex.dto.PoloniexTick;
import com.crypto.trade.polonex.storage.TickersStorage;
import com.opencsv.CSVReader;
import eu.verdelhan.ta4j.*;
import eu.verdelhan.ta4j.indicators.helpers.CrossIndicator;
import eu.verdelhan.ta4j.indicators.oscillators.StochasticOscillatorDIndicator;
import eu.verdelhan.ta4j.indicators.oscillators.StochasticOscillatorKIndicator;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.RSIIndicator;
import eu.verdelhan.ta4j.trading.rules.CrossedDownIndicatorRule;
import eu.verdelhan.ta4j.trading.rules.StopLossRule;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StrategiesBuilder {

    /**
     * RSI 14, StochasticK 14, StochasticD 3
     * Buy on RSI < 30, K intersects D, K < 20
     * Sell +2.5%
     */
    public Strategy buildShortBuyStrategy(TimeSeries timeSeries) {

        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        RSIIndicator rsi = new RSIIndicator(closePrice, 14);
        StochasticOscillatorKIndicator stochK = new StochasticOscillatorKIndicator(timeSeries, 14);
        StochasticOscillatorDIndicator stochD = new StochasticOscillatorDIndicator(stochK);
        CrossIndicator kdCross = new CrossIndicator(stochK, stochD);

        // Entry rule
        Rule entryRule = new CrossedDownIndicatorRule(rsi, Decimal.valueOf(30)) // RSI < 30
                .and(new CrossedDownIndicatorRule(stochK, Decimal.valueOf(20))) // StochasticK < 20
                /*.and(new BooleanIndicatorRule(kdCross))*/; // K intersects D ???

        // Exit rule
        Rule exitRule = new StopLossRule(closePrice, Decimal.valueOf(1));
        Strategy strategy = new Strategy(entryRule, exitRule);
        strategy.setUnstablePeriod(14);
        return strategy;
    }

    public static void main(String[] args) {
        StrategiesBuilder strategiesBuilder = new StrategiesBuilder();
        TickersStorage tickersStorage = new TickersStorage();
        // Reading all lines of the CSV file

        loadTicks(tickersStorage);

        TimeSeries oneMinuteSeries = tickersStorage.generateCandles("BTC_ETH", TimeFrame.ONE_MINUTE);
        Strategy shortBuyStrategy = strategiesBuilder.buildShortBuyStrategy(oneMinuteSeries);

        // Initializing the trading history
        TradingRecord tradingRecord = new TradingRecord();
        System.out.println("************************************************************");

        for (int i = 0; i < oneMinuteSeries.getTickCount(); i++) {
            int endIndex = i;
            Tick newTick = oneMinuteSeries.getTick(i);
            if (shortBuyStrategy.shouldEnter(endIndex)) {
                // Our strategy should enter
                System.out.println("Strategy should ENTER on " + endIndex);
                boolean entered = tradingRecord.enter(endIndex, newTick.getClosePrice(), Decimal.TEN);
                if (entered) {
                    Order entry = tradingRecord.getLastEntry();
                    System.out.println("Entered on " + entry.getIndex()
                            + " (price=" + entry.getPrice().toDouble()
                            + ", amount=" + entry.getAmount().toDouble() + ")");
                }
            } else if (shortBuyStrategy.shouldExit(endIndex, tradingRecord)) {
                // Our strategy should exit
                System.out.println("Strategy should EXIT on " + endIndex);
                boolean exited = tradingRecord.exit(endIndex, newTick.getClosePrice(), Decimal.TEN);
                if (exited) {
                    Order exit = tradingRecord.getLastExit();
                    System.out.println("Exited on " + exit.getIndex()
                            + " (price=" + exit.getPrice().toDouble()
                            + ", amount=" + exit.getAmount().toDouble() + ")");
                }
            }
        }

        /*// Running the strategy
        TradingRecord tradingRecord = oneMinuteSeries.run(shortBuyStrategy, Order.OrderType.BUY, Decimal.ONE);
        System.out.println("Number of trades for the strategy: " + tradingRecord.getTradeCount());

        // Analysis
        System.out.println("Total profit for the strategy: " + new TotalProfitCriterion().calculate(oneMinuteSeries, tradingRecord));*/
    }

    private static void loadTicks(TickersStorage tickersStorage) {
        InputStream stream = StrategiesBuilder.class.getClassLoader().getResourceAsStream("ticks/poloniex_ticks_2017-07-11.csv");
        CSVReader csvReader = null;
        List<String[]> lines = null;
        try {
            csvReader = new CSVReader(new InputStreamReader(stream, Charset.forName("UTF-8")), ',');
            lines = csvReader.readAll();
            lines.remove(0); // Removing header line
        } catch (IOException ioe) {
            Logger.getLogger(StrategiesBuilder.class.getName()).log(Level.SEVERE, "Unable to load trades from CSV", ioe);
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (IOException ioe) {
                }
            }
        }

        if ((lines != null) && !lines.isEmpty()) {
            for (String[] tradeLine : lines) {
                ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(tradeLine[1]) * 1000), ZoneId.of("GMT+0"));
                PoloniexTick tick = new PoloniexTick(time, "BTC_ETH", tradeLine[2], "", "", "", "", "", false, "", "");
                tickersStorage.addTicker(tick);
            }
        }
    }
}
