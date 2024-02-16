package Lab4;
import java.util.HashMap;
import java.util.Map;

public class StockService extends Lab4.StockTrader {

    private Map<String, Double> stockMap = new HashMap<>();

    public void addStock(String stockName, Double price) {
        stockMap.put(stockName, price);
        System.out.println(stockMap);
    }

    public double getStockPrice(String stockName){
        System.out.println(stockMap.get(stockName));
        return stockMap.get(stockName);
    }

    public void update(String stockName, Double price) {
        stockMap.put(stockName, price);
        notifyStockTrader(stockMap);
        System.out.println(stockMap);
    }
}

