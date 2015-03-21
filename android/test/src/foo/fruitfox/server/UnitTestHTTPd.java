package foo.fruitfox.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import foo.fruitfox.helpers.DebugHelper;

public class UnitTestHTTPd extends NanoHTTPD {

	public UnitTestHTTPd(String hostname, int port) {
		super(hostname, port);
	}

	public UnitTestHTTPd(int port) {
		super("localhost", port);
	}

	public UnitTestHTTPd() {
		super("localhost", 8080);
	}

	@Override
	public Response serve(IHTTPSession session) {
		Method method = session.getMethod();
		Map<String, String> parameters = session.getParms();
		Map<String, String> files = new HashMap<String, String>();
		String uri = session.getUri();

		Response response = null;

		switch (method.name()) {
		case "GET":
			if (uri.equals("/test200")) {
				// Generates a normal 200 response
				response = generateTest200response(uri, parameters);
			} else if (uri.equals("/test404")) {
				// Generates a not found 404 response
				response = generateTest404response(uri, parameters);
			} else if (uri.equals("/testQueryString")) {
				// Generates a string based on the Query Passed
				response = generateTestQueryStringresponse(uri, parameters);
			}

			break;

		case "POST":
			// Generate the POST body
			try {
				session.parseBody(files);
			} catch (IOException ioe) {
				return new Response(Response.Status.INTERNAL_ERROR,
						MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: "
								+ ioe.getMessage());
			} catch (ResponseException re) {
				return new Response(re.getStatus(), MIME_PLAINTEXT,
						re.getMessage());
			}

			if (uri.equals("/testPost")) {
				// Generates a string based on the Query Passed
				response = generateTestPostresponse(uri, files);
			}
			break;

		default:
			DebugHelper.ShowMessage.d("other request");
			break;

		}

		return response;
	}

	private Response generateTestPostresponse(String uri,
			Map<String, String> files) {
		String name = files.get("postData");
		String responseString = "Hello, " + name;

		Response response = new NanoHTTPD.Response(Response.Status.OK,
				"application/json", responseString);

		return response;
	}

	private Response generateTestQueryStringresponse(String uri,
			Map<String, String> parameters) {
		String name = parameters.get("fname") + " " + parameters.get("lname");

		String responseString = "Hello, " + name;

		Response response = new NanoHTTPD.Response(Response.Status.OK,
				"application/json", responseString);

		return response;
	}

	private Response generateTest404response(String uri,
			Map<String, String> parameters) {
		String responseString = "Not Found!";

		Response response = new NanoHTTPD.Response(Response.Status.NOT_FOUND,
				"application/json", responseString);

		return response;
	}

	private Response generateTest200response(String uri,
			Map<String, String> parameters) {

		String responseString = "Hello, World!";

		Response response = new NanoHTTPD.Response(Response.Status.OK,
				"application/json", responseString);
		return response;
	}
}
