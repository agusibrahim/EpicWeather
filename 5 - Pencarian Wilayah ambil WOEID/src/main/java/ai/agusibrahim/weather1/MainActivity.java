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
import java.util.List;
import java.util.ArrayList;
import org.apache.http.impl.client.*;
import java.io.*;
import org.apache.http.entity.*;
import android.util.*;

public class MainActivity extends Activity 
{
	Button clrbtn;
	AutoCompleteTextView  inputdaerah;
	TextView out;
	//ArrayAdapter<String> adapter;
	
	private AsyncHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		clrbtn=(Button) findViewById(R.id.clr_btn);
		inputdaerah=(AutoCompleteTextView ) findViewById(R.id.input_daerah);
		out=(TextView) findViewById(R.id.result);
		client=new AsyncHttpClient();
		
		clrbtn.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1) {
					inputdaerah.setText("");
				}
			});
		inputdaerah.addTextChangedListener(new TextWatcher(){
				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
					// TODO: Implement this method
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
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
					out.setText("Nama Wilayah: "+place.name+"\nAdmin1 (Provinsi): "+place.admin1+"\nAdmin2 (Kabupaten): "+place.admin2+"\nNegara: "+place.country+"\nWOEID: "+place.woeid+"\n\n");
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
}
