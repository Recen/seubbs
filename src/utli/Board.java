package utli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class Board {

	private String title;// 版面描述
	private String id;// 版面英文�?	
	private String url;// 版面地址
	private boolean isDirectory;
	private List<Board> childBoards;
	private boolean hasUnread; 

	public Board() {
		isDirectory = false;
	}

	public Board(String id, String title) {
		super();
		this.title = title;
		this.id = id;
		isDirectory = false;
		childBoards = new ArrayList<Board>();
	}

	public String getTitle() {
		return title;
	}

	public Board setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getId() {
		return id;
	}

	public Board setId(String id) {
		this.id = id;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public Board setUrl(String url) {
		this.url = url;
		return this;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public Board setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
		return this;
	}

	public void add(Board board) {
		this.childBoards.add(board);
	}

	public List<Board> getChildBoards() {
		return childBoards;
	}

	public void setChildBoards(List<Board> childBoards) {
		this.childBoards = childBoards;
	}

	public void setHasUnread(boolean hasUnread) {
		this.hasUnread = hasUnread;
	}

	public boolean isHasUnread() {
		return hasUnread;
	}

	/**
	 * 解析json文本，获取版面列�?	 * @param array
	 * @param needsDirectory
	 * @return
	 * @throws HttpException
	 */
	public static List<Board> parseBoardArray(JSONArray array,
			boolean needsDirectory) throws HttpException {
		List<Board> favList = new ArrayList<Board>();
		Board board;
		try {
			for (int i = 0, len = array.length(); i < len; i++) {
				board = new Board();
				JSONObject boardJson = array.getJSONObject(i);
				boolean isDirectory = false;
				if (boardJson.has("leaf")) {
					isDirectory = !boardJson.getBoolean("leaf");
					board.setDirectory(isDirectory);
				}
				List<Board> childList;

				if (isDirectory) {
					childList = parseBoardArray(
							boardJson.getJSONArray("boards"), true);
					board.setChildBoards(childList);
					String name = boardJson.getString("name");
					board.setId(name);
				} else {
					String boardID = boardJson.getString("name");
					String boardName = boardJson.getString("description");
					board.setId(boardID);
					board.setTitle(boardName);
				}
				if (boardJson.has("unread")) {
					boolean unread = boardJson.getBoolean("unread");
					board.setHasUnread(unread);
				}
				if (!needsDirectory && board.isDirectory) {
					favList.addAll(board.getChildBoards());
					continue;
				}
				favList.add(board);
			}
		} catch (JSONException jse) {
			throw new HttpException(jse.getMessage(), jse);
		}

		return favList;

	}

	public static List<Board> getBoardList(JSONObject obj,boolean needsDirectory) throws HttpException {
		List<Board> boardList;
		try {
			JSONArray array = obj.getJSONArray("boards");
			boardList = parseBoardArray(array,needsDirectory);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new HttpException(e.getMessage(), e);
		}
		return boardList;
	}

	public static List<Board> getSortedBoardList(JSONObject obj)
			throws HttpException {
		List<Board> boardList = getBoardList(obj,true);
		Comparator<Board> com = new Comparator<Board>() {
			@Override
			public int compare(Board obj0, Board obj1) {

				if (obj0.isHasUnread() == true && obj1.isHasUnread() == false) {
					return -1;
				} else if (obj0.isHasUnread() == false
						&& obj1.isHasUnread() == true) {
					return 1;
				} else
					return obj0.getId().compareTo(obj1.getId());
			}
		};
		Collections.sort(boardList, com);
		return boardList;
	}

}
