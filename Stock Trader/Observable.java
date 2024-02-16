package Lab4;
import java.util.Iterator;
import java.util.Map;

public class Observable implements Lab4.Observer {

    public void update(Map<String, Double> stocks) {
        stocks.forEach((symbol, value) -> System.out.println(symbol + " - $" + value));
    }
}






