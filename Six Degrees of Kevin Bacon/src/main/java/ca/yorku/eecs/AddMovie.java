package ca.yorku.eecs;

import java.io.IOException;
import java.io.OutputStream;
import org.json.JSONException;
import org.json.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class AddMovie implements HttpHandler {

	@Override
	public void handle(HttpExchange r) throws IOException {
		try {
			if (r.getRequestMethod().equals("PUT")) {
				handlePut(r);
			} else {
				throw new BadRequestException("Invalid Request");
			}
		} catch (BadRequestException ea) {
			r.sendResponseHeaders(400, -1);
		} catch (JSONException eb) {
			r.sendResponseHeaders(400, -1);
		} catch (Exception ec) {
			r.sendResponseHeaders(500, -1);
		}
	}

	/**
	 * 
	 * @param r
	 * @throws Exception
	 * @throws IOException
	 * @throws BadRequestException
	 * @throws JSONException
	 */
	private void handlePut(HttpExchange r) throws Exception, IOException, BadRequestException, JSONException {
		String body = Utils.convert(r.getRequestBody());
		JSONObject d = new JSONObject(body);
		String name = "";
		String movieId = "";
		int year = 0;

		if (d.has("name") && d.has("movieId")) {
			name = d.getString("name");
			movieId = d.getString("movieId");
		} else {
			throw new JSONException("Missing information in JSON");
		}

		Neo4jDB nDB = new Neo4jDB();
		nDB.addMovie(name, movieId, year);
		r.sendResponseHeaders(200, -1);
		nDB.close();
	}
}
