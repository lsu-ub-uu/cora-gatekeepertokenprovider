/*
 * Copyright 2017, 2024 Uppsala University Library
 * Copyright 2019 Olov McKie
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.gatekeepertokenprovider;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.gatekeepertokenprovider.authentication.AuthenticationException;
import se.uu.ub.cora.gatekeepertokenprovider.http.HttpHandlerFactorySpy;
import se.uu.ub.cora.gatekeepertokenprovider.http.HttpHandlerSpy;
import se.uu.ub.cora.gatekeepertokenprovider.log.LoggerFactorySpy;
import se.uu.ub.cora.logger.LoggerProvider;

public class GatekeeperTokenProviderTest {
	private static final String TOKEN_ID = "someTokenId";
	private static final String TOKEN = "someToken";
	private HttpHandlerSpy httpHandler;
	private HttpHandlerFactorySpy httpHandlerFactory;
	private GatekeeperTokenProvider tokenProvider;
	private UserInfo userInfo;
	private String baseUrl;
	private LoggerFactorySpy loggerFactorySpy;
	private String testedClassName = "GatekeeperTokenProviderImp";

	@BeforeMethod
	public void setUp() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		userInfo = UserInfo.withLoginIdAndLoginDomain("someLoginId", "someLoginDomain");
		httpHandlerFactory = new HttpHandlerFactorySpy();
		baseUrl = "http://localhost:8080/gatekeeper/";
		tokenProvider = GatekeeperTokenProviderImp.usingBaseUrlAndHttpHandlerFactory(baseUrl,
				httpHandlerFactory);

		httpHandlerFactory.jsonAnswer = authTokenAsJson();
	}

	@Test
	public void testLogginGatekeeperUrlOnStartup() {
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 0),
				"Using http://localhost:8080/gatekeeper/ as " + "gatekeeperUrl.");
	}

	@Test
	public void testGetGatekeeperUrl() {
		GatekeeperTokenProviderImp tokenProviderImp = (GatekeeperTokenProviderImp) tokenProvider;
		assertEquals(tokenProviderImp.onlyForTestGetGatekeeperUrl(), baseUrl);
	}

	@Test
	public void testGetHttpHandlerFactory() {
		GatekeeperTokenProviderImp tokenProviderImp = (GatekeeperTokenProviderImp) tokenProvider;
		assertEquals(tokenProviderImp.onlyForTersGetHttpHandlerFactory(), httpHandlerFactory);
	}

	@Test
	public void testHttpHandlerCalledCorrectly() {
		tokenProvider.getAuthTokenForUserInfo(userInfo);
		httpHandler = httpHandlerFactory.getFactored(0);

		assertEquals(httpHandler.outputString,
				"{\"children\":[" + "{\"name\":\"loginId\",\"value\":\"someLoginId\"},"
						+ "{\"name\":\"domainFromLogin\",\"value\":\"someLoginDomain\"}"
						+ "],\"name\":\"userInfo\"}");

		assertEquals(httpHandler.requestProperties.get("Accept"),
				"application/vnd.uub.record+json");
		assertEquals(httpHandler.requestProperties.get("Content-Type"),
				"application/vnd.uub.record+json");
		assertEquals(httpHandler.requestProperties.size(), 2);
		assertEquals(httpHandler.requestMetod, "POST");
		assertEquals(httpHandler.url, "http://localhost:8080/gatekeeper/rest/authToken");
	}

	@Test
	public void testReturnedAuthToken() {
		AuthToken authToken = tokenProvider.getAuthTokenForUserInfo(userInfo);

		httpHandler = httpHandlerFactory.getFactored(0);
		assertEquals(authToken.token(), TOKEN);
		assertEquals(authToken.tokenId(), TOKEN_ID);
		assertEquals(authToken.loginId(), "loginId");
		assertEquals(authToken.validUntil(), 100L);
		assertEquals(authToken.renewUntil(), 200L);
	}

	@Test
	public void testReturnedAuthTokenWithName() {
		AuthToken authToken = tokenProvider.getAuthTokenForUserInfo(userInfo);

		httpHandler = httpHandlerFactory.getFactored(0);
		assertEquals(authToken.token(), TOKEN);
		assertEquals(authToken.tokenId(), TOKEN_ID);
		assertEquals(authToken.loginId(), "loginId");
		assertEquals(authToken.validUntil(), 100L);
		assertEquals(authToken.renewUntil(), 200L);
		assertEquals(authToken.firstName().get(), "someFirstName");
		assertEquals(authToken.lastName().get(), "someLastName");
	}

	private String authTokenAsJson() {
		return """
				{"children":[{"name":"token","value":"someToken"},
				{"name":"tokenId","value":"someTokenId"},
				{"name":"validUntil","value":"100"},
				{"name":"renewUntil","value":"200"},
				{"name":"loginId","value":"loginId"},
				{"name":"firstName","value":"someFirstName"},
				{"name":"lastName","value":"someLastName"}
				],"name":"authToken"}""".replace("\n", "");
	}

	@Test(expectedExceptions = AuthenticationException.class, expectedExceptionsMessageRegExp = ""
			+ "AuthToken cannot be created")
	public void testUnauthorizedToken() {
		httpHandlerFactory.setResponseCode(401);
		tokenProvider.getAuthTokenForUserInfo(userInfo);
	}

	@Test
	public void testRenewAuthTokenHttpCall() throws Exception {
		tokenProvider.renewAuthToken(TOKEN_ID, TOKEN);

		httpHandler = httpHandlerFactory.getFactored(0);
		assertEquals(httpHandler.outputString, TOKEN);
		assertEquals(httpHandler.requestProperties.size(), 0);
		assertEquals(httpHandler.requestMetod, "POST");
		assertEquals(httpHandler.url,
				"http://localhost:8080/gatekeeper/rest/authToken/someTokenId");
	}

	@Test
	public void testRenewAuthTokenOk() throws Exception {
		AuthToken authToken = tokenProvider.renewAuthToken(TOKEN_ID, TOKEN);

		assertEquals(authToken.token(), TOKEN);
		assertEquals(authToken.tokenId(), TOKEN_ID);
		assertEquals(authToken.loginId(), "loginId");
		assertEquals(authToken.firstName().get(), "someFirstName");
		assertEquals(authToken.lastName().get(), "someLastName");
		assertEquals(authToken.validUntil(), 100L);
		assertEquals(authToken.renewUntil(), 200L);
	}

	@Test(expectedExceptions = AuthenticationException.class, expectedExceptionsMessageRegExp = ""
			+ "AuthToken could not be renewed")
	public void testRenewAuthTokenUnauthorized() throws Exception {
		httpHandlerFactory.setResponseCode(401);
		tokenProvider.renewAuthToken(TOKEN_ID, TOKEN);
	}

	@Test
	public void testRemoveAuthTokenForUser() {
		tokenProvider.removeAuthToken(TOKEN_ID, "someAuthToken");

		httpHandler = httpHandlerFactory.getFactored(0);

		assertEquals(httpHandler.outputString, "someAuthToken");
		assertEquals(httpHandler.requestProperties.size(), 0);
		assertEquals(httpHandler.requestMetod, "DELETE");
		assertEquals(httpHandler.url,
				"http://localhost:8080/gatekeeper/rest/authToken/someTokenId");
	}

	@Test(expectedExceptions = AuthenticationException.class, expectedExceptionsMessageRegExp = ""
			+ "AuthToken could not be removed")
	public void testRemoveAuthTokenForUserNotOk() {
		httpHandlerFactory.setResponseCode(404);

		String tokenId = TOKEN_ID;
		String authToken = "someAuthToken";
		tokenProvider.removeAuthToken(tokenId, authToken);
	}
}
