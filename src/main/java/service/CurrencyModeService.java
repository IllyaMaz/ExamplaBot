package service;

import entity.Currency;
import service.impl.HashMapCurrency;

public interface CurrencyModeService {

    static CurrencyModeService getInstance(){
        return new HashMapCurrency();
    }

    Currency getOriginalCurrency();

    Currency getTargetCurrency(long chatId);

    void setTargetCurrency(long chatId, Currency currency);



}
