package activity;

import java.util.ArrayList;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.recen.sbbs.R;

public class discussion extends Fragment{
	
	GridView myGridView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.discussion, null);
		
		myGridView = (GridView)view.findViewById(R.id.disGridView);
		final String[]  section = {"��վϵͳ","���ϴ�ѧ","���Լ���","ѧ����ѧ","�����Ļ�","����У��",
							 "��������","֪�Ը���","������Ϣ","��̳�籩","У������","����Ⱥ��"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.disgradview_item, section);
		myGridView.setAdapter(adapter);
		
		myGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
					//Toast.makeText(getActivity(), section[position]+"�����", 20).show();
					
					Intent intent = new Intent();
					intent.putExtra("boardPosition", position);
					intent.setClass(getActivity(), BoardSubSection.class);
					getActivity().startActivity(intent);
										
			}
		});
		return view;
	}	
}
