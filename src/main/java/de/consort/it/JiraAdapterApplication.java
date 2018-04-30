package de.consort.it;

import java.util.HashSet;
import java.util.Set;

import de.consort.it.controller.HealthRouteController;
import de.consort.it.controller.JiraRouteController;
import de.consort.it.controller.RouteController;

public class JiraAdapterApplication {

	private static final Set<RouteController> routeControllers = new HashSet<>();

	public static void main(final String[] args) {

		registerRouteControllers();

		for (final RouteController routeController : routeControllers) {
			routeController.initRoutes();
		}
	}

	private static void registerRouteControllers() {
		routeControllers.add(new JiraRouteController());
		routeControllers.add(new HealthRouteController());
	}
}
