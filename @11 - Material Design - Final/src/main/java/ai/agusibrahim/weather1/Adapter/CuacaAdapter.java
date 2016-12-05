package ai.agusibrahim.weather1.Adapter;
import android.widget.*;
import java.util.List;
import android.content.Context;
import android.view.*;
import android.graphics.Typeface;
import ai.agusibrahim.weather1.Model.*;
import ai.agusibrahim.weather1.Utils.*;
import ai.agusibrahim.weather1.R;

public class CuacaAdapter extends ArrayAdapter<ForecastModel.Channel>
{
	Context mContext;
	public CuacaAdapter(Context ctx, List<ForecastModel.Channel> data){
		super(ctx, 0, data);
		mContext=ctx;
	}
	public static class ViewHolder{
		TextView cuaca, suhu, tgl;
		TextView cuaca_icon;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ForecastModel.Channel ch=getItem(position);
		ForecastModel.Forecast fc=ch.item.forecast;
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=LayoutInflater.from(getContext()).inflate(R.layout.card, parent, false);
			holder.cuaca=(TextView) convertView.findViewById(R.id.cuaca_txt);
			holder.suhu=(TextView) convertView.findViewById(R.id.cuaca_suhu);
			holder.tgl=(TextView) convertView.findViewById(R.id.cuaca_tgl);
			holder.cuaca_icon=(TextView) convertView.findViewById(R.id.cuaca_icon);
			convertView.setTag(holder);
		}else{
			holder=(CuacaAdapter.ViewHolder) convertView.getTag();
		}
		holder.cuaca_icon.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "weathericons-regular-webfont.ttf"));
		holder.cuaca.setText(fc.text);
		holder.cuaca_icon.setText(WeatherConditionCodes.fromInt(Integer.parseInt( fc.code)).toString());
		holder.suhu.setText(fc.low+" - "+fc.high+" °C");
		holder.tgl.setText(fc.date);
		return convertView;
	}
}

