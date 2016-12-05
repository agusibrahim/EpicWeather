package ai.agusibrahim.weather1.Adapter;
import android.widget.*;
import java.util.List;
import android.content.Context;
import android.view.*;
import ai.agusibrahim.weather1.Model.*;

public class WilAdapter extends ArrayAdapter<PlaceModel.Place>
{
	public WilAdapter(Context ctx, List<PlaceModel.Place> data){
		super(ctx, 0, data);
	}
	public static class ViewHolder{
		TextView nama_wilayah;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PlaceModel.Place wil=getItem(position);
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
			holder.nama_wilayah=(TextView) convertView.findViewById(android.R.id.text1);
			convertView.setTag(holder);
		}else{
			holder=(WilAdapter.ViewHolder) convertView.getTag();
		}
		holder.nama_wilayah.setText(wil.name+" ("+wil.admin1+")");
		return convertView;
	}
}
