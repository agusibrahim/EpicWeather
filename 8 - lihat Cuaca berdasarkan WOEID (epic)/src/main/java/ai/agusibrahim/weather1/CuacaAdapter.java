package ai.agusibrahim.weather1;
import android.widget.*;
import java.util.List;
import android.content.Context;
import android.view.*;
import com.loopj.android.image.SmartImageView;

public class CuacaAdapter extends ArrayAdapter<ForecastModel.Channel>
{
	public CuacaAdapter(Context ctx, List<ForecastModel.Channel> data){
		super(ctx, 0, data);
	}
	public static class ViewHolder{
		TextView cuaca, suhu, tgl;
		SmartImageView cuaca_icon;
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
			holder.cuaca_icon=(SmartImageView) convertView.findViewById(R.id.cuaca_icon);
			convertView.setTag(holder);
		}else{
			holder=(CuacaAdapter.ViewHolder) convertView.getTag();
		}
		holder.cuaca.setText(fc.text);
		holder.cuaca_icon.setImageUrl("http://l.yimg.com/a/i/us/we/52/"+fc.code+".gif");
		holder.suhu.setText(fc.low+" - "+fc.high+" °C");
		holder.tgl.setText(fc.date);
		convertView.setEnabled(false);
		return convertView;
	}
}

