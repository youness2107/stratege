package stratege;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Parses JSON Data from Alpha Advantage
 * @author youne
 *
 */
public class AplhaVantageParser {
	
	// filter by date if not null
	public static List<TickDTO> parseIntradayJSONData(String jsonString, String date) {
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			obj = parser.parse(jsonString);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		JSONObject jsonObject = (JSONObject) obj;
		JSONObject jsonticks = (JSONObject) jsonObject.get("Time Series (1min)");
		Object[] arr = jsonticks.keySet().toArray();
		List<TickDTO> ticks = new ArrayList<TickDTO>();
		Arrays.sort(arr);
		for (Object key : arr) {
			String dateTimeStr = (String) key;
			if (date != null && !dateTimeStr.contains(date)) {
				continue;
			}
			ZonedDateTime endTime = toZonedDateTime(dateTimeStr);
			JSONObject tick = (JSONObject) jsonticks.get(key);
			String open = (String) tick.get("1. open");
			String high = (String) tick.get("2. high");
			String low = (String) tick.get("3. low");
			String close = (String) tick.get("4. close");
			String volume = (String) tick.get("5. volume");
			TickDTO tickDTO = new TickDTO(Duration.ofMinutes(1), endTime, open, close, high, low, volume);
			ticks.add(tickDTO);
		}
		return ticks;
	}

	public static void main(String[] args) throws ParseException, IOException {
		//yyyy-MM-dd
		List<TickDTO> ticks = parseIntradayJSONData(StockPriceFetcher.getStock1minData("MSFT"), "2017-08-23");
		for (TickDTO tick : ticks) {
			System.out.println(tick);
		}
	}
	
    private static ZonedDateTime toZonedDateTime(String dateStr) {
    	// example 2017-08-10 09:30:00
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date date = null;
    	ZonedDateTime endTime = null;
    	try {
    		date = df.parse(dateStr);
         	Long epochTime = date.toInstant().toEpochMilli();
            endTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochTime), ZoneId.systemDefault());
    	}
    	catch (Exception e) {
    		
    	}
    	return endTime;
    }
	
}
