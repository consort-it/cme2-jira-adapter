package de.consort.it.controller;

import static spark.Service.ignite;

import de.consort.it.domain.Status;
import de.consort.it.util.JsonTransformer;
import spark.Service;

public class HealthRouteController implements RouteController {

	@Override
	public void initRoutes() {
		final Service http = ignite().port(8081);
		http.get("/health", (request, response) -> {
			response.status(200);
			return new Status("Running!");
		}, new JsonTransformer());

	}
}
