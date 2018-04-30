package de.consort.it.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

import de.consort.it.domain.JiraResponse;
import de.consort.it.util.EnvironmentContext;
import de.consort.it.util.MissingRequiredParameterException;

public class JiraAdapter {

	final Logger logger = LoggerFactory.getLogger(JiraAdapter.class);
	private static final String JIRA_URL = "https://consort-it.atlassian.net";
	private static final String JIRA_USERNAME = "gitlab";
	private static final String JIRA_PASSWORD = "jiraUserPassword";

	public List<JiraResponse> query(JiraRestClient client, String tag, String status)
			throws MissingRequiredParameterException, IOException {
		if (tag == null) {
			logger.error("Der Pflichtparameter \"tag\" ist nicht vorhanden.");
			throw new MissingRequiredParameterException("Der Pflichtparameter \"tag\" ist nicht vorhanden.");
		}

		StringBuilder jql = new StringBuilder("labels = ");
		jql.append(tag);
		if (status != null) {
			jql.append(" AND status = " + status);
		} else {
			jql.append(" AND status != Closed");
		}
		jql.append(" ORDER BY updated DESC");

		Promise<SearchResult> searchJqlPromise = client.getSearchClient().searchJql(jql.toString());

		List<JiraResponse> response = new ArrayList<>();

		for (Issue issue : searchJqlPromise.claim().getIssues()) {
			response.add(new JiraResponse(issue.getKey(), issue.getSummary(), issue.getUpdateDate().toString(),
					issue.getStatus().getName(), JIRA_URL + "/browse/" + issue.getKey()));
			if (response.size() > 199) {
				break;
			}
		}
		client.close();
		return response;
	}

	public JiraRestClient createRestClient() throws URISyntaxException {

		EnvironmentContext context = EnvironmentContext.getInstance();
		System.out.println("!!!!!!!!!!!!!!!!!! BREAKPOINT !!!!!!!!!!!!!!!!");
		System.out.println(context.getenv(JIRA_PASSWORD));

		return new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(new URI(JIRA_URL),
				JIRA_USERNAME, context.getenv(JIRA_PASSWORD));
	}

}
