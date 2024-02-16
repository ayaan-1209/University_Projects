package ca.yorku.eecs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class GetMovieThisYear implements HttpHandler {
	public void handle(HttpExchange r) throws IOException {
		try {
			if (r.getRequestMethod().equals("GET")) {
				handleGet(r);
			} else {
				throw new BadRequestException("Bad Request");
			}
		} catch (BadRequestException exception1) {
			r.sendResponseHeaders(400, -1);
		} catch (JSONException exception2) {
			r.sendResponseHeaders(400, -1);
		} catch (NotFoundException exception3) {
			r.sendResponseHeaders(404, -1);
		} catch (Exception exception4) {
			r.sendResponseHeaders(500, -1);
		}
	}

	private void handleGet(HttpExchange r) throws Exception {

		String body = Utils.convert(r.getRequestBody());
		JSONObject d = new JSONObject(body);
		int year = 0;
		String movieId = "";
		String result;
		if (d.has(String.valueOf(year))) {
			result = d.getString("movieId");
		} else {
			throw new JSONException("Missing information in JSON");
		}
		Neo4jDB nDatabase = new Neo4jDB();
		String acJS = nDatabase.getMovie(movieId);
		r.sendResponseHeaders(200, acJS.length());
		OutputStream outstream = r.getResponseBody();
		outstream.write(acJS.getBytes());
		outstream.close();
	}

}