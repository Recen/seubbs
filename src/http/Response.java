package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import utli.SBBSURLS;

import android.util.Log;



public class Response {
	private final HttpResponse mResponse;
	private static final String TAG = "response";
	public Response(HttpResponse res){
		mResponse = res;
	}
	
	/**from fanfoudroid
	 * Convert Response to inputStream
	 * 
	 * @return InputStream or null
	 * @throws ResponseException
	 */
	public InputStream asStream() throws ResponseException {
		try {
			final HttpEntity entity = mResponse.getEntity();
			if (entity != null) {
				return entity.getContent();
			}
		} catch (IllegalStateException e) {
			throw new ResponseException(e.getMessage(), e);
		} catch (IOException e) {
			throw new ResponseException(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * Convert Response to Context String
	 * 
	 * @return response context string or null
	 * @throws ResponseException
	 */
	public String asString() throws ResponseException {
		try {
			return entityToString(mResponse.getEntity());
			/**
			 * DEBUG code 
			 * TODO  DEL
			 */
//			String newString = entityToString(mResponse.getEntity());
//			Log.i("SpecialResponse", newString);
//			return newString;
		} catch (IOException e) {
			throw new ResponseException(e.getMessage(), e);
		}
	}

	/**
	 * EntityUtils.toString(entity, "UTF-8");
	 * 
	 * @param entity
	 * @return
	 * @throws IOException
	 * @throws ResponseException
	 */
	public String entityToString(final HttpEntity entity) throws IOException,
			ResponseException {
		if (null == entity) {
			throw new IllegalArgumentException("HTTP entity may not be null");
		}
//		InputStream instream = entity.getContent();
//		// InputStream instream = asStream(entity);
//		if (instream == null) {
//			return "";
//		}
//		if (entity.getContentLength() > Integer.MAX_VALUE) {
//			throw new IllegalArgumentException(
//					"HTTP entity too large to be buffered in memory");
//		}
//
//		int i = (int) entity.getContentLength();
//		if (i < 0) {
//			i = 4096;
//		}
//		Log.i("LDS", i + " content length");
//
//		Reader reader = new BufferedReader(new InputStreamReader(instream,
//				"UTF-8"));
//		CharArrayBuffer buffer = new CharArrayBuffer(i);
//		try {
//			char[] tmp = new char[1024];
//			int l;
//			while ((l = reader.read(tmp)) != -1) {
//				buffer.append(tmp, 0, l);
//			}
//		} finally {
//			reader.close();
//		}
//
//		Log.i(TAG, buffer.toString());
//		return buffer.toString();
//		String content = EntityUtils.toString(entity, SBBSConstants.SBBS_ENCODING);
//		return content;
		Header[] headers = mResponse.getHeaders("Content-Encoding");
		boolean isgzip = false;
		String content = "";
		if (headers != null && headers.length != 0) {
			for (Header header : headers) {
				String s = header.getValue();
				if (s.contains("gzip")) {
					isgzip = true;
				}
			}
		}
		if (isgzip) {
			InputStream inStream = entity.getContent();
			BufferedReader br = new java.io.BufferedReader(
					new InputStreamReader(new GZIPInputStream(inStream),
							SBBSURLS.SBBS_ENCODING));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			br.close();
			content = sb.toString();
		} else {
			content = EntityUtils.toString(entity, SBBSURLS.SBBS_ENCODING);
		}
		Log.i(TAG, content);
		return content;
	}
	
	public JSONObject asJSONObject() throws ResponseException {
		try {
//			String mes = asString();
//			Log.i("RecordTheMessage", mes);
			return new JSONObject(asString());
		} catch (JSONException jsone) {
			throw new ResponseException(jsone.getMessage() + ":" + "",
					jsone);
		}
	}
}