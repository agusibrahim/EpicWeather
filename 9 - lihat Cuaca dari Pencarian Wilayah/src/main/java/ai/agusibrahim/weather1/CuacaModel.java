package ai.agusibrahim.weather1;
import java.util.List;

public class CuacaModel
{
	public Query query;
	public class Query{
		public int count;
		public String created, lang;
		public Results results;
	}
	public class Results{
		public Channel channel;
	}
	public class Channel{
		public Item item;
	}
	public class Item{
		public Condition condition;
	}
	public static class Condition{
		public String code, date, temp, text;
	}
}

