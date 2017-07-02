package ai.agusibrahim.weather1;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.*;
import android.support.v7.widget.SearchView;
import android.widget.ArrayAdapter;
import android.graphics.drawable.*;
import com.loopj.android.http.*;
import org.apache.http.Header;
import com.google.gson.*;
import ai.agusibrahim.weather1.Model.*;
import ai.agusibrahim.weather1.Adapter.*;
import android.support.v7.widget.SearchView.*;
import android.widget.*;
import android.support.v7.widget.*;
import ai.agusibrahim.weather1.Utils.*;
import android.graphics.*;
import android.support.v4.view.*;
import java.util.*;
import android.support.v4.widget.SwipeRefreshLayout;
import android.content.*;
import android.content.SharedPreferences.*;
import android.support.v7.app.AlertDialog;


// edited by agusmibrahim
public class MainActivity extends AppCompatActivity {
	Toolbar toolbar;
	private AsyncHttpClient client;
	TextView cuaca_txt, cuaca_wil, cuaca_icon, cuaca_suhu, emptyv;
	List<ForecastModel.Channel> forecastData = new ArrayList<ForecastModel.Channel>();
	private CuacaAdapter adapter;
	LinearLayout current_cond;
	ListView forecastview;
	SwipeRefreshLayout refresher;
	private SharedPreferences pref;

	private MenuItem shareMenu;
	
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
	   pref=getSharedPreferences("mywilayah", MODE_PRIVATE);
       setContentView(R.layout.main_activity);
       toolbar = (Toolbar) findViewById(R.id.toolbar);
	   cuaca_txt=(TextView) findViewById(R.id.cuaca_txt);
	   cuaca_wil=(TextView) findViewById(R.id.cuaca_wilayah);
	   cuaca_icon=(TextView) findViewById(R.id.cuaca_icon);
	   cuaca_suhu=(TextView) findViewById(R.id.cuaca_temp);
	   refresher=(SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
	   emptyv=(TextView) findViewById(R.id.empty_view);
	   forecastview=(ListView) findViewById(R.id.forecastview);
	   current_cond=(LinearLayout) findViewById(R.id.current_cond);
       setSupportActionBar(toolbar);
	   getSupportActionBar().setDisplayShowTitleEnabled(false);
	   client=new AsyncHttpClient();
	   adapter=new CuacaAdapter(this, forecastData);
	   forecastview.setAdapter(adapter);
	   cuaca_icon.setTypeface(Typeface.createFromAsset(getAssets(), "weathericons-regular-webfont.ttf"));
	   refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
			   @Override
			   public void onRefresh() {
				   getWeather((String)cuaca_wil.getTag(), true);
			   }
		   });
	   String mywoeid=pref.getString("woeid", null);
	   if(mywoeid!=null){
		   if(!isNetworkAvailable()){
			   emptyv.setText("No Network");
		   }else{
			   emptyv.setText("Wait...");
			   cuaca_wil.setText(pref.getString("wilayah", "Unknown"));
			   getWeather(mywoeid, true);
		   }
	   }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
	   getMenuInflater().inflate(R.menu.main_menu,menu);
	   MenuItem searchMenuItem = menu.findItem(R.id.action_search);
	   SearchView mSearchView=(SearchView) searchMenuItem.getActionView();
	   shareMenu=menu.findItem(R.id.action_share);
	   shareMenu.setVisible(false);
	   setupSearchAutoComplete(mSearchView, searchMenuItem);
	   return super.onCreateOptionsMenu(menu);
   }

	private void setupSearchAutoComplete(final SearchView mSearchView, final MenuItem menu) {
		final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
		ColorDrawable cd = new ColorDrawable(0xFFFFFFFF);
		searchAutoComplete.setDropDownBackgroundDrawable(cd);
		searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
					PlaceModel.Place place= (PlaceModel.Place) parent.getItemAtPosition(position);
					MenuItemCompat.collapseActionView(menu);
					getWeather(place.woeid, false);
					cuaca_wil.setText(place.name);
					SharedPreferences.Editor edit=pref.edit();
					edit.putString("woeid", place.woeid);
					edit.putString("wilayah", place.name);
					edit.commit();
				}
			});
		mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
				@Override
				public boolean onQueryTextSubmit(String p1) {
					return false;
				}

				@Override
				public boolean onQueryTextChange(String p1) {
					if(!isNetworkAvailable()){
						Toast.makeText(MainActivity.this, "No Network", Toast.LENGTH_SHORT).show();
						return false;
					}
					if(p1.length() > 2) {
						searchAutoComplete.setTag(true);
						doSearch(p1, searchAutoComplete);
					}
					return false;
				}
			});
	}
	private void doSearch(String kw, final SearchView.SearchAutoComplete svac) {
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
					svac.setAdapter(adapter);
					svac.showDropDown();
				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
				}
			});
	}
	private void getWeather(final String woeid, final boolean showupdatedNotif) {
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
					cuaca_txt.setText(cond.text);
					cuaca_suhu.setText(cond.temp);
					//tgl.setText(cond.date);
					cuaca_icon.setText(WeatherConditionCodes.fromInt(Integer.parseInt( cond.code)).toString());
					//cuaca_icon.setImageUrl("http://l.yimg.com/a/i/us/we/52/"+cond.code+".gif");
					current_cond.setVisibility(View.VISIBLE);
					emptyv.setVisibility(View.GONE);
					cuaca_wil.setTag(woeid);
					refresher.setRefreshing(false);
					if(showupdatedNotif){
						Toast.makeText(MainActivity.this, "Diperbarui "+cond.date, Toast.LENGTH_LONG).show();
					}
					shareMenu.setVisible(true);
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_about:
				AlertDialog.Builder d=new AlertDialog.Builder(MainActivity.this);
				d.setTitle(R.string.app_name);
				d.setMessage("EpicWeather adalah Aplikasi perkiraan cuaca yang keren dan sederhana, data diambil dari Yahoo! Weather.\nTerimakasih telah menggunakan Aplikasi ini.\n\nKursus Online Aplikasi Android bisa menghubungi\nAgus Ibrahim");
				d.setNeutralButton("Contact Me", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2) {
							String url = "http://fb.me/mynameisagoes";
							Intent i = new Intent(Intent.ACTION_VIEW);
							i.setData(android.net.Uri.parse(url));
							startActivity(i);
						}
					});
				d.setPositiveButton("Oh, OK", null);
				d.show();
				break;
			case R.id.action_share:
				Intent i = new Intent(android.content.Intent.ACTION_SEND);
				i.setType("text/plain");  
				i.putExtra(android.content.Intent.EXTRA_TEXT, String.format("Cuaca di %s diperkirakan %s dengan suhu sekitar %s °C",cuaca_wil.getText(), cuaca_txt.getText(), cuaca_suhu.getText()));
				startActivity(i);
				break;
			case R.id.menu_exit:
				MainActivity.this.finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	private boolean isNetworkAvailable() {
		android.net.ConnectivityManager connectivityManager 
			= (android.net.ConnectivityManager)MainActivity.this. getSystemService(Context.CONNECTIVITY_SERVICE);
		android.net.NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
