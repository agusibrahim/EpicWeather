package ai.agusibrahim.weather1;

import android.app.*;
import android.os.*;
import android.widget.*;
import com.loopj.android.http.*;
import org.apache.http.*;
import org.json.*;
import android.view.View.*;
import android.view.*;
import android.text.*;
import com.google.gson.*;
import ai.agusibrahim.weather1.CuacaModel.*;
import java.util.*;
import ai.agusibrahim.weather1.ForecastModel.*;

public class MainActivity extends Activity 
{
	Button caribtn;
	EditText inputdaerah;
	TextView out;

	private AsyncHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		caribtn=(Button) findViewById(R.id.cari_btn);
		inputdaerah=(EditText) findViewById(R.id.input_daerah);
		out=(TextView) findViewById(R.id.result);
		client=new AsyncHttpClient();
		caribtn.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1) {
					getWeather(inputdaerah.getText().toString());
				}
			});
    }
	private void getWeather(final String woeid) {
		client.cancelAllRequests(true);
		client.get("https://query.yahooapis.com/v1/public/yql?q=select%20item.condition%20from%20weather.forecast%20where%20woeid%3D%22"+woeid+"%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys", null, new TextHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, Header[] headers, String res) {
					if(res.toString().contains(":0,")){
						out.setText("Tidak ditemukan");
						return;
					}
					Gson gson=new GsonBuilder().create();
					CuacaModel data=gson.fromJson(res, CuacaModel.class);
					CuacaModel.Condition cuaca=data.query.results.channel.item.condition;
					out.setText("Cuaca: "+cuaca.text+"\nTemp: "+cuaca.temp+"\nDiperbarui: "+cuaca.date);
					out.append("\n===========================\n");
					getForecast(woeid);
				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
				}
			});
	}
	private void getForecast(String woeid){
		client.cancelAllRequests(true);
		client.get("https://query.yahooapis.com/v1/public/yql?q=select%20item.forecast%20from%20weather.forecast%20where%20woeid%3D%22"+woeid+"%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys", null, new TextHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, Header[] headers, String res) {
					if(res.toString().contains(":0,")){
						out.append("\n\nTidak diketahui");
						return;
					}
					Gson gson=new GsonBuilder().create();
					ForecastModel data=gson.fromJson(res, ForecastModel.class);
					List<ForecastModel.Channel> cuaca=data.query.results.channel;
					StringBuilder sb=new StringBuilder();
					for(ForecastModel.Channel ch:cuaca){
						ForecastModel.Forecast fc=ch.item.forecast;
						sb.append(fc.day+", "+fc.date+"\nCuaca: "+fc.text+"\nSuhu: "+fc.low+" - "+fc.high+" °C\n\n");
					}
					out.append("\n\n"+sb.toString());
				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
				}
			});
	}
}
