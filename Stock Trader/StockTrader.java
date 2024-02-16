package Lab4;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class StockTrader {
    private List<Lab4.Observer> stockTraders = new ArrayList<Lab4.Observer>();

    public void notifyStockTrader(Map<String, Double> stockMap) {
        for (Lab4.Observer trader : stockTraders) {
            trader.update(stockMap);
        }
    }


    public abstract void addStock(String stockName, Double price);

    public abstract void update(String stockName, Double price);
}