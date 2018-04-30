package de.consort.it.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.google.common.collect.ImmutableList;

import de.consort.it.domain.JiraResponse;
import de.consort.it.util.EnvironmentContext;
import de.consort.it.util.MissingRequiredParameterException;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest({ Dotenv.class })
public class JiraAdapterTest {

	private JiraAdapter adapter;
	private Dotenv dotEnv;
	private JiraRestClient jiraRestClient;

	@Mock
	private DotenvBuilder dotEnvBuilder;
	@Mock
	private EnvironmentContext envContext;
	@Mock
	private JiraRestClientFactory jiraRestClientFactory;

	@Before
	public void setUp() throws Exception {
		adapter = new JiraAdapter();
		jiraRestClient = adapter.createRestClient();
	}

	@Test
	public void testCreateRestClient() throws Exception {
		/*
		 * mockStatic(Dotenv.class); when(Dotenv.configure()).thenReturn(dotEnvBuilder);
		 * when(dotEnvBuilder.load()).thenReturn(dotEnv);
		 * when(envContext.getenv("JIRA_URL")).thenReturn("www.jira.com");
		 * when(envContext.getenv("JIRA_USERNAME")).thenReturn("username");
		 * when(envContext.getenv("JIRA_PASSWORD")).thenReturn("password");
		 * PowerMockito.whenNew(JiraRestClientFactory.class).withNoArguments().
		 * thenReturn(jiraRestClientFactory);
		 * when(jiraRestClientFactory.createWithBasicHttpAuthentication(new
		 * URI("www.jira.com"), "username", "password")) .thenReturn(jiraRestClient);
		 */

		assertNotNull("Client darf nicht null sein", adapter.createRestClient());
	}

	@Test
	public void testQuery() throws URISyntaxException, MissingRequiredParameterException, IOException,
			InterruptedException, ExecutionException {
		String key = createJiraTicket();

		List<JiraResponse> response = adapter.query(adapter.createRestClient(), "DiesIstDerJiraAdapterTest", null);

		assertFalse("Liste an Vorgängen ist leer", response.isEmpty());
		deleteJiraIssue(key);
	}

	@Test
	public void testQueryKeineErgebnisse() throws URISyntaxException, MissingRequiredParameterException, IOException {

		List<JiraResponse> response = adapter.query(adapter.createRestClient(), "DiesenTagGibtEsBestimmtNicht", null);

		assertTrue("Liste an Vorgängen ist nicht leer", response.isEmpty());
	}

	@Test(expected = MissingRequiredParameterException.class)
	public void testQueryProjectNull() throws URISyntaxException, MissingRequiredParameterException, IOException {

		adapter.query(adapter.createRestClient(), null, null);
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
		iib.setFieldInput(new FieldInput(IssueFieldId.LABELS_FIELD, ImmutableList.of("DiesIstDerJiraAdapterTest")));

		IssueInput issue = iib.build();
		BasicIssue issueObj = issueClient.createIssue(issue).claim();

		return issueObj.getKey();

	}

	private void deleteJiraIssue(String key) {
		IssueRestClient issueClient = jiraRestClient.getIssueClient();
		issueClient.deleteIssue(key, true).claim();
	}

	@After
	public void tearDown() throws IOException {
		jiraRestClient.close();
	}

}
