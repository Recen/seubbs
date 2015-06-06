package Task;

import utli.TaskResult;



public abstract class TaskAdapter implements TaskListener {

	@Override
	public abstract String getName();

	@Override
	public void onPreExecute(GenericTask task) {
	};

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
	};

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
	};

	@Override
	public void onCancelled(GenericTask task) {
	};
}
