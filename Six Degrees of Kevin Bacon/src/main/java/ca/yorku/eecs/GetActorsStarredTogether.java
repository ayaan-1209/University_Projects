package ca.yorku.eecs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class GetActorsStarredTogether implements HttpHandler {
	
	@Override
	public void handle(HttpExchange r) throws IOException {
		try {
			if (r.getRequestMethod().equals("GET")) {
				handleGet(r);
			} else {
				throw new BadRequestException("Invalid Request");
			}

		} catch (BadRequestException e1) {
			r.sendResponseHeaders(400, -1);
		} catch (JSONException e2) {
			r.sendResponseHeaders(400, -1);
		} catch (NotFoundException e3) {
			r.sendResponseHeaders(404, -1);
		} catch (Exception e4) {
			r.sendResponseHeaders(500, -1);
		}
	}

	/**
	 * 
	 * @param r
	 * @throws IOException
	 * @throws NotFoundException
	 * @throws JSONException
	 * @throws BadRequestException
	 * @throws Exception
	 */
	private void handleGet(HttpExchange r)
			throws IOException, NotFoundException, JSONException, BadRequestException, Exception {
		String body = Utils.convert(r.getRequestBody());
		JSONObject d = new JSONObject(body);
		String actorId1 = "";
		String actorId2 = "";
		if (d.has("actorId1") && d.has("actorId2")) {
			actorId1 = d.getString("actorId1");
			actorId2 = d.getString("actorId2");
		} else {
			throw new JSONException("Missing information");
		}
		Neo4jDB nDB = new Neo4jDB();
		String StarredJSON = String.valueOf(nDB.getActorsStarredTogether(actorId1, actorId2));
		r.sendResponseHeaders(200, StarredJSON.length());
		OutputStream outstream = r.getResponseBody();
		outstream.write(StarredJSON.getBytes());
		outstream.close();
	}

}