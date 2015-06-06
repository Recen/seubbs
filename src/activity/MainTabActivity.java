package activity;
import com.recen.sbbs.R;

import android.R.integer;
import android.app.Fragment;
import android.app.TabActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabHost;
import android.widget.TextView;


public class MainTabActivity extends FragmentActivity{
	
	private static final String TAG = "FragmentActivity";
	private FragmentTabHost mTabHost;
	private LayoutInflater layoutInflater;
	private Class fragmentArray[] = {recommendPage.class,discussion.class,personalCenter.class};
	
	private int mImageViewArray[] = {R.drawable.tab_home_btn,R.drawable.tab_dis_btn,R.drawable.tab_person_btn};
	private String mTextViewArray[] = {"推荐","讨论区","个人中心"};
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.maintab);		
		initView();
		
	}
	
	private void initView(){
		layoutInflater = LayoutInflater.from(this);
		
		mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		
		int count = fragmentArray.length;
		
		for(int i=0;i<count; i++){
			TabSpec tabSpec = mTabHost.newTabSpec(mTextViewArray[i]).setIndicator(getTabItemView(i));
			mTabHost.addTab(tabSpec,fragmentArray[i],null);
			mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_background);
		}
	}
	
	private View getTabItemView(int index){
		View view = layoutInflater.inflate(R.layout.tab_item, null);
		ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
		imageView.setImageResource(mImageViewArray[index]);
		TextView textView = (TextView)view.findViewById(R.id.textview);
		textView.setText(mTextViewArray[index]);
		
		return view;
	}
 } 

