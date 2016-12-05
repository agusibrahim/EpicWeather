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
					doSearch(inputdaerah.getText().toString());
				}
			});
    }
	private void doSearch(String kw) {
		client.cancelAllRequests(true);
		client.get("https://query.yahooapis.com/v1/public/yql?q=select%20woeid%2Cname%2Cadmin1.content%2Cadmin2.content%20from%20geo.places(100)%20where%20text%3D%22"+kw+"*%22%20and%20lang%3D%22id%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys", null, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject res) {
					if(res.toString().contains(":0,")){
						out.setText("Tidak ditemukan");
						return;
					}
					StringBuilder sb=new StringBuilder();
					try {
						JSONArray results=res.getJSONObject("query").getJSONObject("results").getJSONArray("place");
						for(int i=0;i<results.length();i++){
							JSONObject jo=results.getJSONObject(i);
							sb.append(jo.getString("name")+" ("+jo.getString("admin1")+")"+"\n\n");
						}
						out.setText(sb.toString());
					}
					catch(JSONException e) {}
				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
				}
			});
	}
}
