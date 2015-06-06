package activity;

import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

import utli.BBSOperator;
import utli.Board;
import utli.HttpException;
import utli.SBBSURLS;
import utli.TaskResult;

import com.recen.sbbs.R;

import Adapter.SectionAdapter;
import Adapter.SectionAdapter;
import Task.GenericTask;
import Task.TaskAdapter;
import Task.TaskListener;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

public class BoardSubSection extends FragmentActivity{
	
	
	private ListView secListView;
	private String boardUrl;
	private List<List<Board>> boardList;
	private List<Board> secList;
	private GenericTask mRetrieveTask;
	private SectionAdapter secAdapter;
	private int boardPosition;
	private static final int MODE = 2;

	private static final String TAG= "BoardSubSection";
	private boolean forceLoad = false;
	private TaskListener mRetrieveHotTaskListener = new TaskAdapter() {
		
		private ProgressDialog pdialog;
		/* (non-Javadoc)
		 * @see Task.TaskAdapter#onPreExecute(Task.GenericTask)
		 */
		@Override
		public void onPreExecute(GenericTask task) {
			// TODO Auto-generated method stub
			super.onPreExecute(task);
			pdialog = new ProgressDialog(BoardSubSection.this);
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.boardsubsection);
		super.onCreate(arg0);
				
		secListView = (ListView)findViewById(R.id.secList);
		boardUrl = SBBSURLS.BOARD_SECTIONS;
		
		secAdapter = new SectionAdapter(BoardSubSection.this);
		secListView.setAdapter(secAdapter);
		
		Intent intent = getIntent();
		boardPosition = intent.getExtras().getInt("boardPosition");

		doRetrieve();
		
		secListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Board board = secList.get(position);
			
				Intent intent = new Intent(BoardSubSection.this,TopicList.class);
				intent.putExtra("MODE", MODE);
				intent.putExtra("boardID", board.getId());
				startActivity(intent);				
			}
		});
		
		
		
	}
	
	private void doRetrieve() {
		if (null != mRetrieveTask
				&& GenericTask.Status.RUNNING == mRetrieveTask.getStatus()) {
			return;
		}
		mRetrieveTask = new RetrieveSecTask();
		Log.i(TAG, "abilitdsadasdasdasdy");
		mRetrieveTask.setListener(mRetrieveHotTaskListener);
		mRetrieveTask.execute(boardUrl);

		Log.i(TAG, TAG + "-->doRetrieve");
	}
	
	private class RetrieveSecTask extends GenericTask{
		@Override
		protected TaskResult _doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				Log.i(TAG, params[0]);
				boardList = BBSOperator.getInstance().getAllBoards(params[0]);
				Log.i(TAG, "---->"+boardList.get(2).get(2).getTitle());
				} catch (HttpException e) {
					// TODO Auto-gsenerated catch block
					e.printStackTrace();
					e.getMessage();
					return TaskResult.Failed;
				}
			if (null == boardList || boardList.size() == 0) {
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
		forceLoad = false;
		draw();
		//goTop();

	}	
	
	private void draw() {
		secList = boardList.get(boardPosition);
		secAdapter.refresh(secList);
	}

	

}
