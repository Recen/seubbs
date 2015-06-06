package utli;

public class HttpException extends Exception{
	private static final long serialVersionUID = 1L;
	private int statusCode = -1;
	private String msg;

	public HttpException(String msg) {
		super(msg);
		this.msg = msg;
	}

	public HttpException(Exception cause) {
		super(cause);
	}

	public HttpException(String msg, int statusCode) {
		super(msg);
		this.statusCode = statusCode;
		this.msg = msg;
	}

	public HttpException(String msg, Exception cause) {
		super(msg, cause);
		this.msg = msg;
	}

	public HttpException(String msg, Exception cause, int statusCode) {
		super(msg, cause);
		this.statusCode = statusCode;
		this.msg = msg;
	}

	public int getStatusCode() {
		return this.statusCode;
	}
	
	@Override
	public String getMessage(){
		if(null == this.msg){
			return "Sorry,sth is WRONG!!!";
		}else{
			return this.msg;
		}
	}
}
