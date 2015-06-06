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

public class SectionAdapter extends BaseAdapter{
	private List<Board> boardList;
	private Context context;
	private LayoutInflater mInflater;
	private static final String TAG = "CopyOfTen";
	
	public SectionAdapter(List<Board>list,Context context){
		this.boardList = list;
		this.context = context;
		
	}
	
	public SectionAdapter(Context context) {
		this.boardList = new ArrayList<Board>();
		this.context = context;
	}
	
	public SectionAdapter(LayoutInflater mInflater){
		this.boardList = new ArrayList<Board>();
		this.mInflater = mInflater;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return boardList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return boardList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void refresh(List<Board> list) {
		this.boardList = list;
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
			convertView = mInflater.inflate(com.recen.sbbs.R.layout.fav_item,
					null);
			holder.tv = (TextView) convertView
					.findViewById(com.recen.sbbs.R.id.fav_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Board board = boardList.get(position);
		holder.tv.setText(board.getTitle());
	
		Log.i(TAG, board.getTitle()+"("+board.getId()+")");
		return convertView;
	}
	
	private static class ViewHolder {
		TextView tv;
	}

}
