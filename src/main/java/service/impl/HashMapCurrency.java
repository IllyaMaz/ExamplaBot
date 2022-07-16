package service.impl;

import entity.Currency;
import service.CurrencyModeService;

import java.util.HashMap;
import java.util.Map;

public class HashMapCurrency implements CurrencyModeService {
    private Currency originalCurrency = Currency.UAH;
    private Map<Long, Currency> targetCurrency = new HashMap<>();

    public HashMapCurrency(){
        System.out.println("HASH MAP created!");
    }

    @Override
    public Currency getOriginalCurrency() {
        return originalCurrency;
    }

    @Override
    public Currency getTargetCurrency(long chatId) {
        return targetCurrency.getOrDefault(chatId,Currency.USD);
    }


    @Override
    public void setTargetCurrency(long chatId, Currency currency) {
        targetCurrency.put(chatId,currency);
    }
}
