package activity;

import java.util.List;

import utli.BBSOperator;
import utli.HttpException;
import utli.MyListView;
import utli.PostHelper;
import utli.TaskResult;
import utli.Topic;

import com.recen.sbbs.R;

import Adapter.ToptenAdapter;
import Task.GenericTask;
import Task.TaskAdapter;
import Task.TaskListener;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TopicList extends FragmentActivity{

	private static final String TAG = "TopicList";
	
	private View moreView;
	private TextView moreBtn;
	private LinearLayout progressbar;
	private MyListView topicListView;
	private GenericTask mRetrieveTask;
	private String boardID;
	private String baseURL;
	private String url,errorCause;
	private boolean isFirstLoad = true, hasMoreData = true;
	private boolean forceLoad = false, isLoaded = false;
	private int headPosition = 0;
	private static final int LOADNUM = 20;
	private int start = 0;
	private int mode = 2;
	private List<Topic> topicList;
	private ToptenAdapter topicAdapter;
	private TaskListener getContenTaskListener = new TaskAdapter() {
		
		private ProgressDialog pdialog;
		/* (non-Javadoc)
		 * @see Task.TaskAdapter#onPreExecute(Task.GenericTask)
		 */
		@Override
		public void onPreExecute(GenericTask task) {
			// TODO Auto-generated method stub
			super.onPreExecute(task);
			pdialog = new ProgressDialog(TopicList.this);
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
			topicListView.onRefreshComplete();
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
		
		topicListView = (MyListView)findViewById(R.id.my_list);
		initArgs();
		bindListener();
		topicListView.addFooterView(moreView);
		doRetrieve();
		
		
	}
	private void initArgs(){
		Intent intent = getIntent();
		boardID = intent.getStringExtra("boardID");
		intent.getExtras().getInt("MODE");
		
		baseURL = "http://bbs.seu.edu.cn/api/board/" + boardID + ".json?"
				+ "limit=" + LOADNUM;
		
		
		
		moreView = getLayoutInflater().inflate(R.layout.moredata, null);
		moreBtn = (TextView) moreView.findViewById(R.id.load_more_btn);
		progressbar = (LinearLayout) moreView.findViewById(R.id.more_progress);
		
		topicAdapter = new ToptenAdapter(TopicList.this);
		topicListView.setAdapter(topicAdapter);		
	}
	
	private void bindListener(){
		topicListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Topic topic = getContextItemTopic(position);
				Log.i(TAG, "--->"+topic.getTitle());
				Intent intent = new Intent(TopicList.this, dialogActivity.class);
				intent.putExtra(PostHelper.EXTRA_ID, topic.getId());
				intent.putExtra(PostHelper.EXTRA_TITLE, topic.getTitle());
				intent.putExtra(PostHelper.EXTRA_BOARD, boardID);
				startActivity(intent);
			}
		});
		
		moreBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doLoadMore();
			}
		});
		
		topicListView.setonRefreshListener(new MyListView.OnRefreshListener() {

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
	
	private Topic getContextItemTopic(int position) {
		if (position >= 1 && position <= topicAdapter.getCount()) {
			return (Topic) topicAdapter.getItem(position-1);
		}
		return null;
	}
	
	private void doRetrieve() {
		if (null != mRetrieveTask
				&& GenericTask.Status.RUNNING == mRetrieveTask.getStatus()) {
			return;
		}
		mRetrieveTask = new TopicTask();
		mRetrieveTask.setListener(getContenTaskListener);
		url = baseURL.concat("&start=" + start + "&mode=" + mode);
		Log.i(TAG, "topicList-->"+url);
		mRetrieveTask.execute(url);

		Log.i(TAG, TAG + "-->doRetrieve");
	}
	
	private class TopicTask extends GenericTask{

		@Override
		protected TaskResult _doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<Topic> newList;
			try {
				newList = BBSOperator.getInstance().getTopicList(params[0]);
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorCause = e.getMessage();
				return TaskResult.Failed;
			}
			if (isFirstLoad) {
				topicList = newList;
				headPosition = 1;
			} else {
				headPosition = topicList.size()-1;
				if (0 == topicList.size()) {
					return TaskResult.NO_DATA;
				} else {
					topicList.addAll(newList);
				}
			}
			if (newList.size() < LOADNUM) {
				hasMoreData = false;
				
			} else {
				hasMoreData = true;
			}
			isFirstLoad = false;
			start = topicList.size();
		if (null == topicList || topicList.size() == 0) {
			return TaskResult.NO_DATA;
		}
		return TaskResult.OK;
		}
		
	}
	private void processResult(TaskResult result) {
		if (TaskResult.Failed == result) {
//			Toast.makeText(MyApplication.mContext, errorCause,
//					Toast.LENGTH_SHORT).show();
			Log.i(TAG, "Failed");
			return;
		}
		if (result == TaskResult.NO_DATA) {
//			Toast.makeText(MyApplication.mContext, R.string.hot_no_data,
//					Toast.LENGTH_SHORT).show();
			Log.i(TAG, "No Data");
			return;
		}
		draw();
		//goTop();

	}	
	
	private void draw() {
	    topicAdapter.refresh(topicList);
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
	}

}
