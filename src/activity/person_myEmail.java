package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.recen.sbbs.R;


public class person_myEmail extends FragmentActivity{
	private Button writeLetterButton;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.person_myemail);
		writeLetterButton = (Button)findViewById(R.id.writeLetter);
        writeLetterButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(person_myEmail.this, person_writeLetter.class);  
	            person_myEmail.this.startActivity(intent);  
			}
						
		 	});
	
	}
	}
	


