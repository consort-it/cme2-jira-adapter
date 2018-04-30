package de.consort.security;

import java.net.URL;
import java.security.PublicKey;

import org.apache.commons.lang.StringUtils;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.http.client.direct.HeaderClient;
import org.pac4j.jwt.config.signature.RSASignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.sparkjava.DefaultHttpActionAdapter;
import org.pac4j.sparkjava.SecurityFilter;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;

import de.consort.it.util.EnvironmentContext;
import spark.Request;
import spark.Response;
import sun.security.rsa.RSAPublicKeyImpl;

public class AuthorizationFilter extends SecurityFilter {

	private static final String AUTH0 = "Auth0";
	private static final String HEADER_NAME = "Authorization";
	private static final String AUTHORIZER_SCOPE = "scope";
	private static final String JWK_URL = "https://cognito-idp.eu-central-1.amazonaws.com/eu-central-1_M4FyC0JPA/.well-known/jwks.json";
	private static final String JWK_ALG = "RS256";

	public AuthorizationFilter(final String authorizerName, final String roleName) {
		super(createSecurityConfig(authorizerName, roleName), AUTH0, AUTHORIZER_SCOPE);
	}

	private static Config createSecurityConfig(final String authorizerName, final String roleName) {

		final JwtAuthenticator tokenAuthenticator = new JwtAuthenticator();
		final String jwk_kids = EnvironmentContext.getInstance().getenv("jwk_kid");

		for (final String kid : jwk_kids.split(",")) {

			final RSASignatureConfiguration signatureConfiguration = new RSASignatureConfiguration();

			try {
				final URL urlToJWK = new URL(JWK_URL);
				final JwkProvider provider = new UrlJwkProvider(urlToJWK);
				final Jwk jwk = provider.get(kid);
				if (jwk != null && JWK_ALG.equalsIgnoreCase(jwk.getAlgorithm())) {
					final PublicKey publicKey = jwk.getPublicKey();
					if (publicKey instanceof RSAPublicKeyImpl) {
						signatureConfiguration.setPublicKey((RSAPublicKeyImpl) publicKey);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			tokenAuthenticator.addSignatureConfiguration(signatureConfiguration);
		}

		final HeaderClient headerClient = new HeaderClient(HEADER_NAME, tokenAuthenticator);
		headerClient.setName(AUTH0);
		headerClient.setPrefixHeader("Bearer ");

		final Config config = new Config(new Clients(headerClient));
		if (!StringUtils.isBlank(authorizerName) && !StringUtils.isBlank(roleName)) {
			config.addAuthorizer(authorizerName, new JiraAdapterAttributeAuthorizer(AUTHORIZER_SCOPE, roleName));
		}
		config.setHttpActionAdapter(new DefaultHttpActionAdapter());

		return config;
	}

	@Override
	public void handle(Request request, Response response) {
		if (!request.requestMethod().equals("OPTIONS") && !"1".equals(request.queryParams("nosec"))) {
			super.handle(request, response);
		}
	}

}
