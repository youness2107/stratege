package stratege;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
/**
 * 
 * @author youne
 *
 */

public class StockPriceFetcher {

	//https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=MSFT&interval=15min&outputsize=full&apikey=demo
	
	private static final String API_URL_BASE = "https://www.alphavantage.co/query?";
	private static final String API_KEY = "8IAP5B610W1O18H0";
	private static final String FUNCTION = "TIME_SERIES_INTRADAY";
	private static final String SYMBOL = "MSFT";
	private static final String INTERVAL = "1min";
	private static final String OUTPUT_SIZE = "full";
	
	public static String buildAPICall(String base, String apiKey, String function, 
			String symbol, String interval, String outputSize) {
		String apiCallURL = API_URL_BASE + "function=" + FUNCTION + "&" + "symbol=" + SYMBOL + "&" 
		+ "interval=" + INTERVAL + "&" + "outputsize="+ OUTPUT_SIZE + "&" +
		"apikey=" + apiKey;
		return apiCallURL;
	}
	
	public static String getStock1minData(String ticker) {
		String apiCallURL = buildAPICall(API_URL_BASE, API_KEY, FUNCTION, SYMBOL, INTERVAL, OUTPUT_SIZE);
		System.out.println("URL: " + apiCallURL);
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
		      .url(apiCallURL)
		      .build();
		Response response = null;
		String data = null;
		try {
			response = client.newCall(request).execute();
			data = response.body().string();
		}
		catch (Exception e) {
			return null;
		}
		return data;
	}
		
	// get time series for a day
	public static TimeSeries getTimeSeries(String ticker, int intraday, String date) {
		String jsonStr = getStock1minData(ticker);
		List<TickDTO> tickDTOs = AplhaVantageParser.parseIntradayJSONData(jsonStr, date); 
		TimeSeries series = new TimeSeries();
		for (TickDTO tickDTO : tickDTOs) {
			Tick tick = new Tick(tickDTO.getTimePeriod(), tickDTO.getEndTime(), 
					tickDTO.getOpenPrice(), tickDTO.getMaxPrice(), tickDTO.getMinPrice(), 
					tickDTO.getClosePrice(), tickDTO.getVolume());
			series.addTick(tick);
			
		}
		if (intraday == 1) {
			return series;
		}
		series = TickAggregation.getIntradaySeries(series, intraday);
		return series;
	}
	
	
	public static void main(String[] args) throws IOException {
		Long t1 = System.currentTimeMillis();
		System.out.println(getStock1minData("MSFT"));
		Long t2= System.currentTimeMillis();
		System.out.println("Took: " + (t2-t1) + "ms on avg");
	}
	
	
	
}
