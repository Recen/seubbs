package activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.recen.sbbs.R;

import utli.BBSOperator;
import utli.HttpException;
import utli.MyApplication;
import utli.SBBSURLS;
import utli.TaskResult;
import utli.User;

import Task.GenericTask;
import Task.TaskAdapter;
import Task.TaskListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class ViewProfileActivity extends FragmentActivity {
	private User user;
	private String userID,errorCause;
	private TextView userName, nickName, lifeValue, identity, loginTime,
			postTime, performValue, experience, astrology, genderView,
			lsLoginTimeView;
	private GenericTask doRetrieveTask, doAddFriendTask;
	private boolean isLogined;
	protected String token;
	public static final String EXTRA_USER = "userID";
	private static final String TAG = "ViewProfileActivity";
	
	private TaskListener mRetrieveTaskListener = new TaskAdapter() {
		private ProgressDialog pdialog;

		@Override
		public String getName() {
			return "mRetrieveTaskListener";
		}

		@Override
		public void onPreExecute(GenericTask task) {
			super.onPreExecute(task);
			pdialog = new ProgressDialog(ViewProfileActivity.this);
			pdialog.setMessage(getResources().getString(R.string.loading));
			pdialog.show();
			pdialog.setCanceledOnTouchOutside(false);
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			super.onPostExecute(task, result);
			pdialog.dismiss();
			if (getResult(result)) {
				if (null != user) {
					setProfile();
				}
			}
		}

	};

	private TaskListener mAddFriendListener = new TaskAdapter() {

		@Override
		public String getName() {
			return "mAddFriendListener";
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			processAdd(result);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile);
		isLogined = MyApplication.checkLogin();
		if (isLogined) {
			Log.i(TAG, "login");
			token = MyApplication.getInstance().getToken();
			setup();
		} else {
			Log.i(TAG, "unlogin");
			processUnLogin();
		}
		initArgs();
		doRetrieve();
	}



	private void initArgs() {
		try {
			userID = getIntent().getExtras().getString("userID");
		} catch (NullPointerException e) {
			userID = MyApplication.userName;
		}
		userName = (TextView) this.findViewById(R.id.profile_userid);
		nickName = (TextView) this.findViewById(R.id.profile_user_nickname);
		lifeValue = (TextView) this.findViewById(R.id.profile_aliveness);
		identity = (TextView) this.findViewById(R.id.profile_identity);
		genderView = (TextView) this.findViewById(R.id.profile_gender);
		astrology = (TextView) this.findViewById(R.id.profile_astrololgy);
		loginTime = (TextView) this.findViewById(R.id.profile_login_times);
		postTime = (TextView) this.findViewById(R.id.profile_post_number);
		performValue = (TextView) this.findViewById(R.id.profile_user_perform_value);
		experience = (TextView) this.findViewById(R.id.profile_user_experience);
		lsLoginTimeView = (TextView) this.findViewById(R.id.profile_last_login);

	}

	private void doRetrieve() {
		doRetrieveTask = new RetrieveTask();
		doRetrieveTask.setListener(mRetrieveTaskListener);
		doRetrieveTask.execute(userID);
	}

//	private void doAdd() {
//		if (!isLogined) {
////			Toast.makeText(this, R.string.login_indicate, Toast.LENGTH_SHORT).show();
//			return;
//		}
//		doAddFriendTask = new DoAddFriendTask();
//		doAddFriendTask.setListener(mAddFriendListener);
//		doAddFriendTask.execute(userID, token);
//	}

	private void processAdd(TaskResult result) {
		if (TaskResult.IO_ERROR == result) {
//			Toast.makeText(this, R.string.load_io_error, Toast.LENGTH_SHORT).show();
			return;
		}
		if (TaskResult.Failed == result) {
			Toast.makeText(this, R.string.profile_add_friends_failed, Toast.LENGTH_SHORT).show();
			return;
		}
		if (TaskResult.OK == result) {
			Toast.makeText(this, R.string.profile_add_friends_success, Toast.LENGTH_SHORT).show();
			return;
		}
	}

	private boolean getResult(TaskResult result) {

		if (TaskResult.Failed == result) {
			Toast.makeText(this, errorCause, Toast.LENGTH_SHORT)
					.show();
			finish();
			return false;
		}
		return true;

	}

	private void setProfile() {
		userID = user.getId();
		setTitle(userID + getResources().getString(R.string.profile_view_title));
		userName.setText(user.getId());

		if (user.getGender().equals("other")) {
			userName.setTextColor(0xff000000);
			genderView.setText(R.string.profile_user_sex_unknown);
		} else {
			if (user.getGender().equals("M")) {
				userName.setTextColor(0xff0000ff);
				genderView.setText(R.string.profile_user_sex_m);
			} else {
				userName.setTextColor(0xffFF34B3);
				genderView.setText(R.string.profile_user_sex_f);
			}
		}
		nickName.setText(user.getNickName());
		lifeValue.setText(user.getLifeValue());
		identity.setText(user.getIdentity());
		loginTime.setText(user.getLoginTime());
		postTime.setText(user.getPostTime());
		performValue.setText(user.getPerformValue());
		experience.setText(user.getExperience());
		astrology.setText(user.getAstrology());
		lsLoginTimeView.setText(user.getLastLoginTime());
	}

	private class RetrieveTask extends GenericTask {
//		private UserUtils userUtils;

		@Override
		protected TaskResult _doInBackground(String... params) {
			//				userUtils = SBBSSupport.getUserProfileAPI(params[0].trim());
			try {
				user = BBSOperator.getInstance().getUserProfile(SBBSURLS.BASE_URL+"/api/user/"+params[0]+".json");
			} catch (HttpException e) {
				e.printStackTrace();
				errorCause = e.getMessage();
				return TaskResult.Failed;
			}
			return TaskResult.OK;
		}

	}

	

	private void doSearch(String input) {
		if (TextUtils.isEmpty(input.trim())) {
			return;
		}
		Intent intent = new Intent(ViewProfileActivity.this, ViewProfileActivity.class);
		Bundle bundle = new Bundle();
		try {
			bundle.putString("userID", URLEncoder.encode(input.trim(),
					SBBSURLS.SBBS_ENCODING));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
		intent.putExtras(bundle);
		startActivity(intent);
	}

	

//	private class DoAddFriendTask extends GenericTask {
//
//		@Override
//		protected TaskResult _doInBackground(String... params) {
//			String id = params[0];
//			String token = params[1];
//			String url = "http://bbs.seu.edu.cn/api/friends/add.json?token="
//					+ token + "&id=" + id;
////				boolean success = SBBSSupport.dealFriends(url);
//			boolean success = BBSOperator.getInstance().getBoolean(url);
//			if (success) {
//				return TaskResult.OK;
//			} else {
//				return TaskResult.Failed;
//			}
//		}
//	}

	protected void processUnLogin() {
		// TODO Auto-generated method stub
		
	}


	protected void setup() {
		// TODO Auto-generated method stub
	
	}
}
