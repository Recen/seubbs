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

import Adapter.ToptenAdapter;
import Task.GenericTask;
import Task.TaskAdapter;
import Task.TaskListener;
import android.R.integer;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;

public class campus_fragment extends Fragment{

	private static final String TAG = "campus_fragment";
	private boolean isFirstLoad = true, hasMoreData = true;
	private boolean forceLoad = false, isLoaded = false;
	private int start = 0;
	private MyListView campusListView;
	private LayoutInflater mInflater;
	private ToptenAdapter myAdapter;
	private String campusurl;
	private List<Topic> campusList;
	private Button refreshButton;
	private View view;
	private GenericTask mRetrieveTask;
	private int headPosition = 0;
	private static final int LOADNUM = 20;
    
	private TaskListener mRetrieveHotTaskListener = new TaskAdapter() {
		ProgressDialog pdialog;

		@Override
		public String getName() {
			return "mRetrieveHotTaskListener";
		}

		@Override
		public void onPreExecute(GenericTask task) {
			super.onPreExecute(task);
			pdialog = new ProgressDialog(getActivity());
			pdialog.setMessage(getResources().getString(R.string.loading));
			pdialog.show();
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			super.onPostExecute(task, result);
			pdialog.cancel();
			isLoaded = true;
			campusListView.onRefreshComplete();
			processResult(result);
		}

	};


	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initArgs();
		bindListener();
		doRetrieve();
		
	}


	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		view = inflater.inflate(com.recen.sbbs.R.layout.list_without_header, null);

		return view;
	}
	
	private void initArgs() {
		campusListView = (MyListView)view.findViewById(R.id.my_list);
		//hotListView.setVerticalScrollBarEnabled(true);
		myAdapter = new ToptenAdapter(getActivity());
		campusListView.setAdapter(myAdapter);
		campusurl = "http://bbs.seu.edu.cn/api/board/AcademicEvents.json?limit="+LOADNUM +"&start=0&mode=2";		
	}
	
	private void bindListener(){
		campusListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Topic topic = getContextItemTopic(position);
				Log.i(TAG, "--->"+topic.getTitle());
				Intent intent = new Intent(getActivity(), dialogActivity.class);
				intent.putExtra(PostHelper.EXTRA_ID, topic.getId());
				intent.putExtra(PostHelper.EXTRA_TITLE, topic.getTitle());
				intent.putExtra(PostHelper.EXTRA_BOARD, topic.getBoardName());
				startActivity(intent);
			}
		});
		
		campusListView.setonRefreshListener(new MyListView.OnRefreshListener() {

			@Override
			public void onRefresh() {
				start = 0;
				isFirstLoad = true;
				doRetrieve();
			}
		});
		
	}
	
	private void doRetrieve() {
		if (null != mRetrieveTask
				&& GenericTask.Status.RUNNING == mRetrieveTask.getStatus()) {
			return;
		}
		mRetrieveTask = new RetrieveCampusTask();
		mRetrieveTask.setListener(mRetrieveHotTaskListener);
		mRetrieveTask.execute(campusurl);

		Log.i(TAG, TAG + "-->doRetrieve");
	}
	
	private class RetrieveCampusTask extends GenericTask{
		@Override
		protected TaskResult _doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<Topic>newList = new ArrayList<Topic>();
			try {
				newList = BBSOperator.getInstance().getTopicList(params[0]);
				
				} catch (HttpException e) {
					// TODO Auto-gsenerated catch block
					e.printStackTrace();
					e.getMessage();
					return TaskResult.Failed;
				}
			if (isFirstLoad) {
				campusList = newList;
				headPosition = 0;
			} else {
				headPosition = campusList.size()-1;
				campusList.addAll(newList);
			}
			if (newList.size() < LOADNUM) {
				hasMoreData = false;
			} else {
				hasMoreData = true;
			}
			isFirstLoad = false;
			start = campusList.size();
			if (campusList.size() == 0) {
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
		myAdapter.refresh(campusList);
	}

	private Topic getContextItemTopic(int position) {
		if (position >= 1 && position <= myAdapter.getCount()) {
			return (Topic) myAdapter.getItem(position-1);
		}
		return null;
	}


	
}
