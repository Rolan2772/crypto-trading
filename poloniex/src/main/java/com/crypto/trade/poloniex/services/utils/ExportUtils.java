package com.crypto.trade.poloniex.services.utils;

import com.crypto.trade.poloniex.storage.PoloniexTradingRecord;

public class ExportUtils {

    public static String getTradingRecordName(PoloniexTradingRecord tradingRecord) {
        return tradingRecord.getStrategyName() + "-tr-" + tradingRecord.getId();
    }
}