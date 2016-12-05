package ai.agusibrahim.weather1;
import java.util.List;

public class PlaceModel
{
	public Query query;
	public class Query{
		public int count;
		public String created, lang;
		public Results results;
	}
	public class Results{
		public List<Place> place;
	}
	public static class Place{
		public String name, admin1, admin2, country, woeid;
	}
}
