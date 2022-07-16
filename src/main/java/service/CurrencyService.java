package service;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import entity.Currency;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class CurrencyService {
    private static final String CURRENCY_VALUE = "https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=11";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

    public DaoCurrency getCurrency(Currency target) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CURRENCY_VALUE))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request,HttpResponse.BodyHandlers.ofString());
        List<DaoCurrency> list = GSON.fromJson(response.body(),new TypeToken<List<DaoCurrency>>(){}.getType());
        DaoCurrency daoCurrency = null;
        if (target.name().equals("USD")){
            daoCurrency = list.get(0);
        } else if (target.name().equals("EUR")){
            daoCurrency = list.get(1);
        } else if (target.name().equals("RUB")){
            daoCurrency = list.get(2);
        }

        return daoCurrency;


    }
}
