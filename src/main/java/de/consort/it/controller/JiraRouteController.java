package de.consort.it.controller;

import java.net.URISyntaxException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.consort.it.domain.ErrorResponse;
import de.consort.it.domain.JiraResponse;
import de.consort.it.service.JiraAdapter;
import de.consort.it.util.JsonTransformer;
import de.consort.it.util.MissingRequiredParameterException;
import de.consort.security.AuthorizationFilter;
import spark.Service;

public class JiraRouteController implements RouteController {

	private static final Logger logger = LoggerFactory.getLogger(JiraRouteController.class);

	private static final String AUTHORIZER_NAME = "scope";
	private static final String ROLE_ADMIN = "aws.cognito.signin.user.admin";

	@Override
	public void initRoutes() {
		final Service http = Service.ignite().port(8080);
		enableCORS(http, "*", "GET, POST, OPTIONS", "Content-Type, Authorization");
		http.before("/api/v1/jira-adapter/*", new AuthorizationFilter(AUTHORIZER_NAME, ROLE_ADMIN));
		initJiraRoute(http);
	}

	private void initJiraRoute(final Service http) {
		logger.info("Jira-Adapter Microservice started");

		http.get("/api/v1/jira-adapter/issues", (request, response) -> {

			JiraAdapter adapter = new JiraAdapter();
			List<JiraResponse> jiraResponse = null;

			try {
				jiraResponse = adapter.query(adapter.createRestClient(), request.queryParams("tag"),
						request.queryParams("status"));
			} catch (URISyntaxException e) {
				response.status(500);
				return new ErrorResponse("Testcode1", e.getMessage());
			} catch (MissingRequiredParameterException e) {
				response.status(400);
				return new ErrorResponse("Testcode2", e.getMessage());
			}

			response.status(200);
			if (jiraResponse.isEmpty()) {
				return new JiraResponse[0];
			}
			return jiraResponse;

		}, new JsonTransformer());
	}

	private static void enableCORS(final Service http, final String origin, final String methods,
			final String headers) {

		http.options("/*", (req, res) -> {

			final String acRequestHeaders = req.headers("Access-Control-Request-Headers");
			if (acRequestHeaders != null) {
				res.header("Access-Control-Allow-Headers", acRequestHeaders);
			}

			final String accessControlRequestMethod = req.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null) {
				res.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}

			return "OK";
		});

		http.before((req, res) -> {
			res.header("Access-Control-Allow-Origin", origin);
			res.header("Access-Control-Request-Method", methods);
			res.header("Access-Control-Allow-Headers", headers);
			res.type("application/json");
			res.header("Server", "-");
		});
	}
}
