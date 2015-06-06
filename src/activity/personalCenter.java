package activity;

import utli.MyApplication;
import utli.Preferences;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.recen.sbbs.R;

public class personalCenter extends Fragment{
	
	private static final String TAG = "personalCenter";
	private View view;
	private Button loginButton;
	private TextView nickName;
	private TextView myEmail;
	private TextView setting;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		loginButton = (Button)view.findViewById(R.id.loginButton);
		myEmail = (TextView)view.findViewById(R.id.myEmail);
		setting = (TextView)view.findViewById(R.id.setting);
		
		boolean isLogined = MyApplication.checkLogin();
		if(!isLogined){
			loginButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(), Login.class);
					startActivity(intent);
				}
			});
		}else {
			Log.i(TAG, "logined");
			loginButton.setVisibility(View.GONE);
			nickName = (TextView)view.findViewById(R.id.personName);
			nickName.setVisibility(View.VISIBLE);
			String nickname = MyApplication.mPreference.getString(Preferences.USER_NAME, "");
			nickName.setText(nickname);
			bindListener();
		}
		
		
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.person, null);
		return view;
	}
	
	private void bindListener(){
		nickName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i(TAG, "dasdasdjhsajdh");
				
				Intent intent = new Intent(getActivity(), ViewProfileActivity.class);
				startActivity(intent);
			}
		});
		
	
	myEmail.setOnClickListener(new OnClickListener() {
			
		@Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
	    Intent intent = new Intent(getActivity(), person_myEmail.class);
		startActivity(intent);
			}
		});
	
	
	setting.setOnClickListener(new OnClickListener() {
			
		@Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
	    Intent intent = new Intent(getActivity(), preference.class);
		startActivity(intent);
			}
		});
	}
	}
