package activity;

import utli.BBSOperator;
import utli.HttpException;
import utli.MyApplication;
import utli.Preferences;
import utli.TaskResult;
import utli.User;

import com.recen.sbbs.R;

import Task.GenericTask;
import Task.TaskAdapter;
import Task.TaskListener;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends FragmentActivity{

	private static final String TAG = "LoginActivity";
	private EditText userNameText, passwdText;
	private Button mLoginButton;
	private GenericTask mLoginTask;
	private MyApplication application;
	
	private User user;
	private String mUserName, mPasswd, mToken,errorCause;
	private SharedPreferences mPreferences;
	
	private TaskListener mLoginTaskListener = new TaskAdapter() {
		ProgressDialog pdialog;
		@Override
		public String getName() {
			
			return "mLoginTaskListener";
		}
		
		@Override
		public void onPreExecute(GenericTask task) {
			pdialog = new ProgressDialog(Login.this);
			pdialog.setMessage(getString(R.string.login_message));
			pdialog.show();
			pdialog.setCanceledOnTouchOutside(false);
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			super.onPostExecute(task, result);
			pdialog.dismiss();
			onLoginComplete(result);
		}
		

	};
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		application = (MyApplication) getApplication();
		initArgs();
		initEvents();
		
		
	}
	
	private void initArgs(){
		userNameText = (EditText)findViewById(R.id.userName);
		passwdText = (EditText)findViewById(R.id.password);
		mLoginButton = (Button)findViewById(R.id.login);

		mPreferences = MyApplication.mPreference;
		
	}
	
	private void initEvents(){
		boolean remember = mPreferences.getBoolean(Preferences.REMEMBER_ME,
				false);

		if (remember) {
			mUserName = mPreferences.getString(Preferences.USER_NAME, "");
			mPasswd = mPreferences.getString(Preferences.USER_PWD, "");
			userNameText.setText(mUserName);
			passwdText.setText(mPasswd);
		}
		mLoginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "mLoginButton pressed!!!");
				doLogin();
			}
		});
	
		boolean isLogined = MyApplication.checkLogin();
		if (isLogined) {
			Log.i(TAG, "isLogined is true");
			boolean autoLogin = application.isAutoLogin();
			Log.i(TAG, "autoLogin is "+autoLogin);
			if (autoLogin) {
				MyApplication.loginUser = new User(mUserName,mPasswd);
				onLoginFinish(false);
			}
		}
		passwdText.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				if(KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN){
					doLogin();
					return true;
				}
				return false;
			}
		});
	}
	private void doLogin() {
		mUserName = userNameText.getText().toString().trim();
		mPasswd = passwdText.getText().toString().trim();
		if (validate(mUserName, mPasswd)) {
			mLoginTask = new LoginTask();
			mLoginTask.setListener(mLoginTaskListener);
			mLoginTask.execute(mUserName, mPasswd);
		}
	}
	
	private boolean validate(String userID, String passwd) {
		if (TextUtils.isEmpty(userID) || TextUtils.isEmpty(passwd)) {
			return false;
		}
		return true;
	}
	private void onLoginComplete(TaskResult taskResult) {
		if (TaskResult.Failed == taskResult) {
			showFailure();
			return;
		}
			showSuccess();
			mToken = user.getToken();
			onLoginFinish(true);
	}
	
	private void onLoginFinish(boolean isLogined){
		
		Intent intent = new Intent(this,personalCenter.class);
		if(isLogined){
			updateInfo();
		}
		String idString = user.getId();
		String namString = user.getNickName();
		Log.i(TAG, "idString--->"+idString);
		Log.i(TAG, "idString--->"+namString);
		intent.putExtra("nickname", namString);
		startActivity(intent);
		finish();
	}
	
	private void showSuccess() {
		Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
	}

	private void showFailure() {
		Toast.makeText(this, errorCause, Toast.LENGTH_SHORT).show();
	}
	
	private void updateInfo() {
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putString(Preferences.USER_NAME, mUserName);
		editor.putString(Preferences.USER_PWD, mPasswd);
		editor.putString(Preferences.USER_TOKEN, mToken);
		editor.commit();
		MyApplication.userName = mUserName;
		application.setToken(mToken);
		application.setLogined(true);
		MyApplication.loginUser = new User(mUserName,mPasswd);
		Log.i(TAG, mUserName+" login success");
	}
	
	private class LoginTask extends GenericTask{
		@Override
		protected TaskResult _doInBackground(String... params) {
			//				result = SBBSSupport.doLogin(mUserName, mPasswd);
			try {
				user = BBSOperator.getInstance().doLogin(mUserName, mPasswd);
			} catch (HttpException e) {
				e.printStackTrace();
				errorCause = e.getMessage();
				return TaskResult.Failed;
			}
			return TaskResult.OK;
			
		}
	}

}
