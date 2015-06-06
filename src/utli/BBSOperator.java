package utli;

import java.util.ArrayList;
import java.util.List;

import http.HttpClient;
import http.Response;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.util.Log;

public class BBSOperator {
	private HttpClient mClient;
	private static BBSOperator bbsOP = null;
	private static final String TAG = "BBSOperator";
	
	private BBSOperator() {
		mClient = new HttpClient();
	}
	
	public static BBSOperator getInstance(){
		if(null == bbsOP){
			bbsOP = new BBSOperator();
		}
		return bbsOP;
	}
	
	public List<Topic> getTopicList(String url) throws HttpException {
		return Topic.parseTopicList(getJsonSuccess(url));
	}
	
	public JSONObject getJsonSuccess(String url) throws HttpException {
		boolean success = false;
		JSONObject obj = getJson(url);
		try {
			success = obj.getBoolean("success");

			if (success) {
				return obj;
			} else {
				String error = obj.getString("error");
				throw new HttpException(error);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new HttpException(e.getMessage(), e);
		}
	}
	
	public JSONObject getJson(String url) throws HttpException {
		return get(url).asJSONObject();
	}
	
	public JSONObject getJson(String url, List<BasicNameValuePair> params)
			throws HttpException {
		return post(url, params).asJSONObject();
	}
	public Response post(String url, List<BasicNameValuePair> params)
			throws HttpException {
		return mClient.httpRequest(url, null, params);
	}
	
	public Response get(String url) throws HttpException {
		return mClient.httpRequest(url);
	}
	
	public List<List<Board>> getAllBoards(String url) throws HttpException {
		List<List<Board>> allBoardList = new ArrayList<List<Board>>();
		
		try {
			JSONObject obj = getJsonSuccess(url);
			JSONArray groupArray = obj.getJSONArray("boards");
			for (int i = 0, len = groupArray.length(); i < len; i++) {
				JSONObject groupJson = groupArray.getJSONObject(i);
				JSONArray boardsJson = groupJson.getJSONArray("boards");
				List<Board> list = Board.parseBoardArray(boardsJson, false);
			
				allBoardList.add(list);
			}
		} catch (JSONException e) {
			throw new HttpException(e.getMessage(), e);
		}
		return allBoardList;
	}
	
	public User doLogin(String name, String passWord) throws HttpException {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("user", name));
		params.add(new BasicNameValuePair("pass", passWord));
		return User.parseLogin(getJsonSuccess(SBBSURLS.LOGIN_URL, params));
	}
	
	public JSONObject getJsonSuccess(String url, List<BasicNameValuePair> params)
			throws HttpException {
		boolean success = false;
		JSONObject obj = getJson(url, params);
		try {
			success = obj.getBoolean("success");

			if (success) {
				Log.i(TAG, "reply or post success");
				return obj;
			} else {
				String error = obj.getString("error");
				Log.i(TAG, "reply or post error");
				throw new HttpException(error);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new HttpException(e.getMessage(), e);
		}
	}
	
	public User getUserProfile(String url) throws HttpException {
		return User.getUser(getJsonSuccess(url));
	}
}
