package ca.yorku.eecs;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class App {
	static int PORT = 8080;
	String url = "bolt://localhost:7687";
	String user = "neo4j";
	String password = "1234";
	Neo4jDB database = new Neo4jDB();

	public static void main(String[] args) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);

		server.createContext("/api/v1/addActor", new AddActor());
		server.createContext("/api/v1/addMovie", new AddMovie());
		server.createContext("/api/v1/addRelationship", new AddRelationship());
		server.createContext("/api/v1/getActor", new GetActor());
		server.createContext("/api/v1/getMovie", new GetMovie());
		server.createContext("/api/v1/hasRelationship", new HasRelationship());
		server.createContext("/api/v1/computeBaconPath", new ComputeBaconPath());
		server.createContext("/api/v1/computeBaconNumber", new ComputeBaconNumber());
		
		// New Implements
		server.createContext("/api/v1/addLocation", new AddLocation());
		server.createContext("/api/v1/addFilmedIn", new AddFilmedIn());
		server.createContext("/api/v1/getActorsStarredTogether", new GetActorsStarredTogether());
		server.createContext("/api/v1/getMovieThisYear", new GetMovieThisYear());
		server.createContext("/api/v1/getMovieThisLocation", new GetMovieThisLocation());
		server.createContext("/api/v1/hasFilmedIn", new HasFilmedIn());

		server.start();
		System.out.printf("Server started on port %d...\n", PORT);
	}
}
