package stratege;

import eu.verdelhan.ta4j.TimeSeries;

public class StockPriceFetcherTest {

	/*
	 * 
	 */
	public static void main(String[] args) {
		
		TimeSeries ts = StockPriceFetcher.getTimeSeries("MSFT", 5, "2017-08-23");
		for (int i = 0; i < ts.getTickCount(); i++) {
			System.out.println(ts.getTick(i));
		}

		
	}
	
}
