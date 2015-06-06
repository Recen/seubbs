package Adapter;

import java.util.ArrayList;
import java.util.List;

import utli.*;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ToptenAdapter extends BaseAdapter{
	private List<Topic> hotList;
	private Context context;
	private LayoutInflater mInflater;
	
	public ToptenAdapter(List<Topic>list,Context context){
		this.hotList = list;
		this.context = context;
		
	}
	
	public ToptenAdapter(Context context) {
		this.hotList = new ArrayList<Topic>();
		this.context = context;
	}
	
	public ToptenAdapter(LayoutInflater mInflater){
		this.hotList = new ArrayList<Topic>();
		this.mInflater = mInflater;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return hotList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return hotList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void refresh(List<Topic> list) {
		this.hotList = list;
		notifyDataSetChanged();
	}

	public void refresh() {
		notifyDataSetChanged();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (null == mInflater) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(com.recen.sbbs.R.layout.listview_topten_item,
					null);
			holder.txt_author = (TextView) convertView
					.findViewById(com.recen.sbbs.R.id.list_top_ten_textAuthor);
			holder.txt_title = (TextView) convertView
					.findViewById(com.recen.sbbs.R.id.list_top_ten_textTitle);
			holder.txt_board = (TextView) convertView
					.findViewById(com.recen.sbbs.R.id.list_top_ten_textBoard);
			holder.readView = (TextView) convertView
					.findViewById(com.recen.sbbs.R.id.list_top_ten_pop);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Topic topic = hotList.get(position);
		holder.txt_author.setText(topic.getAuthor());
		holder.txt_title.setText(topic.getTitle());
		holder.txt_board.setText(topic.getBoardName()+"°æ");
		holder.readView.setText(topic.getReplies() + "/"
				+ topic.getPopularity());
		return convertView;
	}
	
	private static class ViewHolder {
		TextView txt_author;
		TextView txt_title;
		TextView txt_board;
		TextView readView;
	}

}
