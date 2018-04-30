package de.consort.security;

import org.pac4j.core.authorization.authorizer.RequireAnyAttributeAuthorizer;

public class JiraAdapterAttributeAuthorizer extends RequireAnyAttributeAuthorizer {

    public JiraAdapterAttributeAuthorizer(final String attribute, final String valueToMatch) {
        super(valueToMatch);
        setElements(attribute);
    }
}
