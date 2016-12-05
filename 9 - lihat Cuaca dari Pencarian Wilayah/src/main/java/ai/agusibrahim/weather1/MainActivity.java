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
import com.loopj.android.image.*;

public class MainActivity extends Activity 
{
	Button caribtn;
	AutoCompleteTextView inputdaerah;
	ListView out;
	TextView cuaca, suhu, tgl;
	SmartImageView cuaca_icon;
	RelativeLayout condition;
	String curwoeid, curwilayah;
	
	List<ForecastModel.Channel> forecastData = new ArrayList<ForecastModel.Channel>();
	private AsyncHttpClient client;
	private CuacaAdapter adapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		caribtn=(Button) findViewById(R.id.cari_btn);
		inputdaerah=(AutoCompleteTextView) findViewById(R.id.input_daerah);
		out=(ListView) findViewById(R.id.forecast_render);
		cuaca=(TextView) findViewById(R.id.cuaca_txt);
		suhu=(TextView) findViewById(R.id.cuaca_suhu);
		tgl=(TextView) findViewById(R.id.cuaca_tgl);
		cuaca_icon=(SmartImageView) findViewById(R.id.cuaca_icon);
		condition=(RelativeLayout) findViewById(R.id.condition);
		
		adapter=new CuacaAdapter(this, forecastData);
		out.setAdapter(adapter);
		client=new AsyncHttpClient();
		caribtn.setEnabled(false);
		caribtn.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1) {
					getWeather(curwoeid);
					MainActivity.this.setTitle(curwilayah);
					inputdaerah.dismissDropDown();
				}
			});
		inputdaerah.addTextChangedListener(new TextWatcher(){
				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
					// TODO: Implement this method
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
					caribtn.setEnabled(false);
					if(p1.length()>2){
						doSearch(p1.toString());
					}
				}

				@Override
				public void afterTextChanged(Editable p1) {
					// TODO: Implement this method
				}
			});
		inputdaerah.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
					PlaceModel.Place place= (PlaceModel.Place) parent.getItemAtPosition(position);
					inputdaerah.setText(place.name);
					inputdaerah.setSelection(inputdaerah.length());
					curwoeid=place.woeid;
					curwilayah=place.name+" ("+place.admin1+")";
					caribtn.setEnabled(true);
				}
			});
    }
	private void doSearch(String kw) {
		client.cancelAllRequests(true);
		client.get("https://query.yahooapis.com/v1/public/yql?q=select%20woeid%2Ccountry.content%2Cname%2Cadmin1.content%2Cadmin2.content%20from%20geo.places(100)%20where%20text%3D%22" + kw + "*%22%20and%20lang%3D%22id%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys", null, new TextHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, Header[] headers, String res) {
					if(res.toString().contains(":0,")||res.toString().contains(":1,")){
						return;
					}
					Gson gson = new GsonBuilder().create();
					PlaceModel results = gson.fromJson(res, PlaceModel.class);
					WilAdapter adapter = new WilAdapter(MainActivity.this, results.query.results.place);
					inputdaerah.setAdapter(adapter);
					inputdaerah.showDropDown();
				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
				}
			});
	}
	
	private void getWeather(final String woeid) {
		client.cancelAllRequests(true);
		client.get("https://query.yahooapis.com/v1/public/yql?q=select%20item.condition%20from%20weather.forecast%20where%20woeid%3D%22"+woeid+"%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys", null, new TextHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, Header[] headers, String res) {
					if(res.toString().contains(":0,")){
						Toast.makeText(MainActivity.this, "Tidak diketahui", Toast.LENGTH_LONG).show();
						return;
					}
					Gson gson=new GsonBuilder().create();
					CuacaModel data=gson.fromJson(res, CuacaModel.class);
					CuacaModel.Condition cond=data.query.results.channel.item.condition;
					cuaca.setText(cond.text);
					suhu.setText(cond.temp+" °C");
					tgl.setText(cond.date);
					cuaca_icon.setImageUrl("http://l.yimg.com/a/i/us/we/52/"+cond.code+".gif");
					condition.setVisibility(View.VISIBLE);
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
						Toast.makeText(MainActivity.this, "Tidak diketahui", Toast.LENGTH_LONG).show();
						return;
					}
					Gson gson=new GsonBuilder().create();
					ForecastModel data=gson.fromJson(res, ForecastModel.class);
					List<ForecastModel.Channel> cuaca=data.query.results.channel;
					forecastData.clear();
					for(ForecastModel.Channel ch:cuaca){
						forecastData.add(ch);
					}
					adapter.notifyDataSetChanged();
				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
				}
			});
	}
}
