package de.consort.it.domain;

public class Status {

	private String serviceStatus;

	public Status() {
	}

	public Status(final String serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

	public String getStatus() {
		return serviceStatus;
	}

	public void setStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

}
