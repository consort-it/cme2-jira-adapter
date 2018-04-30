package de.consort.it.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpRequest;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.google.common.collect.ImmutableList;

import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.TargetRequestFilter;
import au.com.dius.pact.provider.junit.loader.PactFolder;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import de.consort.it.JiraAdapterApplication;

@RunWith(PactRunner.class)
@Provider("jira-adapter")
@PactFolder("./src/test/resources")
public class JiraAdapterPactTest {

	private JiraAdapter adapter;
	private JiraRestClient jiraRestClient;
	private String key;

	@BeforeClass
	public static void setupService() {
		JiraAdapterApplication.main(new String[] {});
	}

	@State("provider has some issues with tag PACT-TEST-JIRA-TAG")
	public void testGetIssues() throws URISyntaxException, InterruptedException, ExecutionException {
		adapter = new JiraAdapter();
		jiraRestClient = adapter.createRestClient();
		key = createJiraTicket();
	}

	@State("provider has no issues with tag PACT-TEST-JIRA-TAG-NOT-FOUND")
	public void testGetIssuesWithEmptyResult() {
	}

	@State("provider accepts requests")
	public void testGetIssuesWithInternalError() {

	}

	@TestTarget // Annotation denotes Target that will be used for tests
	public final Target target = new HttpTarget(8080); // Out-of-the-box implementation of Target (for more information
														// take a look at Test Target section)

	@TargetRequestFilter
	public void exampleRequestFilter(HttpRequest request) {
		// request.addHeader("Authorization",
		// "Bearer
		// eyJraWQiOiJJbUptODYyYkxQQ3RsaFFReTM2OEJBb0tQYXlEOURMcE53eXdXMmZoWlBJPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJkMmZkODdkNy03Y2E4LTRhMGYtOTM1MS00YWE2YjAwOTMyN2UiLCJldmVudF9pZCI6ImVkZDk4YjFhLTNmMGEtMTFlOC1iZGE4LTEzOGM2M2YwMmQxYyIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4iLCJhdXRoX3RpbWUiOjE1MjM2MTc2ODAsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC5ldS1jZW50cmFsLTEuYW1hem9uYXdzLmNvbVwvZXUtY2VudHJhbC0xX000RnlDMEpQQSIsImV4cCI6MTUyMzYyMTI4MCwiaWF0IjoxNTIzNjE3NjgxLCJqdGkiOiI0MzFhODQwZS1lNDVkLTRjOTItYmRjNS00OGU5MjY3MDMwZjIiLCJjbGllbnRfaWQiOiI1NjdjZ3RqY2h2aDBhZG01dHYyZzB1NHBrNiIsInVzZXJuYW1lIjoibWhpZW1lciJ9.U4DlcWI2FkhU6CLJYmX24-39xJOMoGNJu3DsESVc0hAZrBeJs1P4CsK_VXMWdattJDS4RB9ifmdk5gY6bkpXcZSgRD9XaNmAtSN-cfZZfY_RpfDH6TdgxruJDGox-w5B-BHBMMxHmRHO9OZOd0vDdDYfG9oWnH1nyQ2-ebzLAQ7-ULKTxVPV5-Tse7CTN2BnDy2kQWc3JuqMNFxe1bmfHE4osgnqN10JigxL2Bdv5K_cZXIQVry1r2IEOP4LrQApzZqdG6Ae2fEgtjYaQb944Awqadbx6wg04EPAIgkSSanezHJJpnH9HnsmO1OKfC5YUtRfSFRRRSAmYp4I1gs8Kg");
	}

	@After
	public void tearDown() throws IOException {
		if (key != null) {
			deleteJiraIssue(key);
			jiraRestClient.close();
		}
	}

	private String createJiraTicket() throws InterruptedException, ExecutionException, URISyntaxException {
		IssueRestClient issueClient = jiraRestClient.getIssueClient();

		IssueInputBuilder iib = new IssueInputBuilder();
		iib.setProjectKey("JAT");
		iib.setSummary("Test Summary");
		iib.setIssueType(new IssueType(new URI("https://consort-it.atlassian.net/rest/api/2/issuetype/10103"), 10103L,
				"Bug", false, "A problem which impairs or prevents the functions of the product.",
				new URI("https://consort-it.atlassian.net/secure/viewavatar?size=xsmall&avatarId=10303&avatarType=issuetype")));
		iib.setDescription("Test Description");
		iib.setPriorityId(3L);
		iib.setAssigneeName("gitlab");
		iib.setFieldInput(new FieldInput(IssueFieldId.LABELS_FIELD, ImmutableList.of("PACT-TEST-JIRA-TAG")));

		IssueInput issue = iib.build();
		BasicIssue issueObj = issueClient.createIssue(issue).claim();

		return issueObj.getKey();

	}

	private void deleteJiraIssue(String key) {
		IssueRestClient issueClient = jiraRestClient.getIssueClient();
		issueClient.deleteIssue(key, true).claim();
		key = null;
	}

}
