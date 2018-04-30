package de.consort.it.domain;

public class JiraResponse {
	private String key;
	private String title;
	private String date;
	private String status;
	private String url;

	public JiraResponse() {
	}

	public JiraResponse(String key, String title, String date, String status, String url) {
		this.key = key;
		this.title = title;
		this.date = date;
		this.status = status;
		this.url = url;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
