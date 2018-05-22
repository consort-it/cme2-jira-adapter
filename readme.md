# jira-adapter

The jira-adapter microservice provides all open Jira issues to a given microservice (using the tag parameter in Jira).

## Prerequisites

In order to function correctly you need to create you own .env-file below src/main/resources.
After creation you also need to provide the following parameters:

* jiraUserPassword
* jwk_kid

## Paths

When started the service provides following paths:

* Port 8080:
GET: /jira/issues?tag=xxx with xxx as String
* Port 8081:
GET: /health
