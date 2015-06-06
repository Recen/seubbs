package activity;

import com.recen.sbbs.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public class person_writeLetter extends FragmentActivity{

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.writeletter);
	}

}
