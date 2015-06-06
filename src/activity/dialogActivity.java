package activity;

import java.util.ArrayList;
import java.util.List;

import utli.BBSOperator;
import utli.HttpException;
import utli.MyListView;
import utli.PostHelper;
import utli.SBBSURLS;
import utli.TaskResult;
import utli.Topic;

import com.recen.sbbs.R;

import Adapter.TopReplyAdapter;
import Task.GenericTask;
import Task.TaskAdapter;
import Task.TaskListener;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class dialogActivity extends FragmentActivity{
	
	private static final String TAG = "dialogActivity";
	public MyListView myListView;
	private boolean forceLoad = false, isLoaded = false;
	private View moreView;
	private TextView moreBtn;
	private LinearLayout progressbar;
	private GenericTask mRetrieveTask;
	private String title, boardID,url;
	private boolean isFirstLoad = true, hasMoreData = true;
	private int headPosition = 0;
	private String baseUrl, errorCause;
	private int id,start = 0;
	private List<Topic> dialoglist;
	private TopReplyAdapter dialogAdapter;
	
	private static final int LOADNUM = 20;

	
	private TaskListener getContenTaskListener = new TaskAdapter() {
		
		private ProgressDialog pdialog;
		/* (non-Javadoc)
		 * @see Task.TaskAdapter#onPreExecute(Task.GenericTask)
		 */
		@Override
		public void onPreExecute(GenericTask task) {
			// TODO Auto-generated method stub
			super.onPreExecute(task);
			pdialog = new ProgressDialog(dialogActivity.this);
			pdialog.setMessage(getResources().getString(R.string.loading));
			pdialog.show();
			pdialog.setCanceledOnTouchOutside(false);
		}

		/* (non-Javadoc)
		 * @see Task.TaskAdapter#onPostExecute(Task.GenericTask, utli.TaskResult)
		 */
		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			// TODO Auto-generated method stub
			super.onPostExecute(task, result);
			pdialog.dismiss();
			isLoaded = true;
			myListView.onRefreshComplete();
			handleRetrieveResult(result);
			processResult(result);
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return "getContenTaskListener";
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
		setContentView(R.layout.list_without_header);
		myListView = (MyListView)findViewById(R.id.my_list);
		initAtgs();
		bindListener();
		myListView.addFooterView(moreView);
	
		
	}
	
	private void initAtgs(){
		moreView = getLayoutInflater().inflate(R.layout.moredata, null);
		moreBtn = (TextView) moreView.findViewById(R.id.load_more_btn);
		progressbar = (LinearLayout) moreView.findViewById(R.id.more_progress);
		
		id = getIntent().getExtras().getInt(PostHelper.EXTRA_ID);
		boardID = getIntent().getExtras().getString(PostHelper.EXTRA_BOARD);
		baseUrl = SBBSURLS.BASE_API_URL+"/topic/" + boardID + "/" + id
				+ ".json?limit=" + LOADNUM;
		
		dialogAdapter = new TopReplyAdapter(this);
		myListView.setAdapter(dialogAdapter);
		doRetrieve();
	}
	
	private void bindListener(){
		moreBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doLoadMore();
			}
		});
		
		myListView.setonRefreshListener(new MyListView.OnRefreshListener() {

			@Override
			public void onRefresh() {
				start = 0;
				isFirstLoad = true;
				doRetrieve();
			}
		});
		
		
	}
	private void doLoadMore() {
		moreBtn.setVisibility(View.GONE);
		progressbar.setVisibility(View.VISIBLE);
		doRetrieve();
	}
	
	private void doRetrieve() {
		if (null != mRetrieveTask
				&& GenericTask.Status.RUNNING == mRetrieveTask.getStatus()) {
			return;
		}
		mRetrieveTask = new TopicTask();
		mRetrieveTask.setListener(getContenTaskListener);
		url = baseUrl.concat("&start=" + start);
		Log.i(TAG, "campus url = "+url);
		mRetrieveTask.execute(url);
	}
	
	private class TopicTask extends GenericTask{

		@Override
		protected TaskResult _doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<Topic> newList = new ArrayList<Topic>();
			try {
				newList = BBSOperator.getInstance().getTopicList(params[0]);
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				e.getMessage();
				return TaskResult.Failed;
			}
			if (isFirstLoad) {
				dialoglist = newList;
				headPosition = 0;
			} else {
				headPosition = dialoglist.size()-1;
				if (0 == dialoglist.size()) {
					return TaskResult.NO_DATA;
				} else {
					dialoglist.addAll(newList);
				}
			}
			if (newList.size() < LOADNUM) {
				hasMoreData = false;
			} else {
				hasMoreData = true;
			}
			isFirstLoad = false;
			start = dialoglist.size();
		if (null == dialoglist || dialoglist.size() == 0) {
			return TaskResult.NO_DATA;
		}
		return TaskResult.OK;
		}
		
	}
	private void processResult(TaskResult result) {
		if (TaskResult.Failed == result) {
			Toast.makeText(getApplicationContext(), "加载失败",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (result == TaskResult.NO_DATA) {
			Toast.makeText(getApplicationContext(), "已经到底啦！",
					Toast.LENGTH_SHORT).show();
			return;
		}
		draw();

	}	
	
	private void draw() {
	    dialogAdapter.refresh(dialoglist);
	}
	
	private void handleRetrieveResult(TaskResult result) {
		moreBtn.setVisibility(View.VISIBLE);
		progressbar.setVisibility(View.GONE);
		if (result == TaskResult.IO_ERROR || TaskResult.Failed == result) {
			Toast.makeText(this, errorCause, Toast.LENGTH_SHORT).show();
			return;
		}
		if(result == TaskResult.OK){
			isFirstLoad = false;
			draw();
		}
		if (result == TaskResult.NO_DATA) {
			Toast.makeText(getApplicationContext(), "已经到底啦！",
					Toast.LENGTH_SHORT).show();
			moreBtn.setVisibility(View.INVISIBLE);
			return;
		}
	}

}
