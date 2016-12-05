package ai.agusibrahim.weather1.Model;
import java.util.List;

public class ForecastModel
{
	public Query query;
	public class Query{
		public int count;
		public String created, lang;
		public Results results;
	}
	public class Results{
		public List<Channel> channel;
	}
	public class Channel{
		public Item item;
	}
	public class Item{
		public Forecast forecast;
	}
	public static class Forecast{
		public String code, date, day, high, low, text;
	}
}

