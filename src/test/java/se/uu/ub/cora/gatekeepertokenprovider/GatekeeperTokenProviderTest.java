/*
 * Copyright 2017 Uppsala University Library
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

public class GatekeeperTokenProviderTest {
	private HttpHandlerSpy httpHandler;
	private HttpHandlerFactorySpy httpHandlerFactory;
	private GatekeeperTokenProvider tokenProvider;
	private UserInfo userInfo;

	@BeforeMethod
	public void setUp() {
		userInfo = UserInfo.withLoginIdAndLoginDomain("someLoginId", "someLoginDomain");
		httpHandlerFactory = new HttpHandlerFactorySpy();
		String baseUrl = "http://localhost:8080/gatekeeper/";
		tokenProvider = GatekeeperTokenProviderImp.usingBaseUrlAndHttpHandlerFactory(baseUrl,
				httpHandlerFactory);
	}

	@Test
	public void testHttpHandlerCalledCorrectly() {
		UserInfo userInfo = UserInfo.withLoginIdAndLoginDomain("someLoginId", "someLoginDomain");
		tokenProvider.getAuthTokenForUserInfo(userInfo);
		httpHandler = httpHandlerFactory.getFactored(0);

		assertEquals(httpHandler.outputString,
				"{\"children\":[" + "{\"name\":\"idFromLogin\",\"value\":\"someLoginId\"},"
						+ "{\"name\":\"domainFromLogin\",\"value\":\"someLoginDomain\"}"
						+ "],\"name\":\"userInfo\"}");

		assertEquals(httpHandler.requestProperties.get("Accept"), "application/vnd.uub.record+json");
		assertEquals(httpHandler.requestProperties.get("Content-Type"),
				"application/vnd.uub.record+json");
		assertEquals(httpHandler.requestProperties.size(), 2);
		assertEquals(httpHandler.requestMetod, "POST");
		assertEquals(httpHandler.url, "http://localhost:8080/gatekeeper/rest/authToken");
	}

	@Test
	public void testReturnedAuthToken() {
		UserInfo userInfo = UserInfo.withLoginIdAndLoginDomain("someLoginId", "someLoginDomain");
		AuthToken authToken = tokenProvider.getAuthTokenForUserInfo(userInfo);
		httpHandler = httpHandlerFactory.getFactored(0);

		assertEquals(authToken.token, "someId");
		assertEquals(authToken.validForNoSeconds, 400);
	}

	@Test(expectedExceptions = AuthenticationException.class)
	public void testUnauthorizedToken() {
		httpHandlerFactory.setResponseCode(401);
		tokenProvider.getAuthTokenForUserInfo(userInfo);
	}

	@Test
	public void testRemoveAuthTokenForUser() {
		String idInUserStorage = "someIdInUserStorage";
		String authToken = "someAuthToken";
		tokenProvider.removeAuthTokenForUser(idInUserStorage, authToken);

		httpHandler = httpHandlerFactory.getFactored(0);

		assertEquals(httpHandler.outputString, "someAuthToken");

		assertEquals(httpHandler.requestProperties.size(), 0);
		assertEquals(httpHandler.requestMetod, "DELETE");
		assertEquals(httpHandler.url,
				"http://localhost:8080/gatekeeper/rest/authToken/someIdInUserStorage");
	}

	@Test(expectedExceptions = AuthenticationException.class)
	public void testRemoveAuthTokenForUserNotOk() {
		httpHandlerFactory.setResponseCode(404);

		String idInUserStorage = "someIdInUserStorage";
		String authToken = "someAuthToken";
		tokenProvider.removeAuthTokenForUser(idInUserStorage, authToken);
	}
}
