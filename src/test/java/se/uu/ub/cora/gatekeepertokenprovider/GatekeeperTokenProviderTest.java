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

import se.uu.ub.cora.gatekeepertokenprovider.http.HttpHandlerFactorySpy;
import se.uu.ub.cora.gatekeepertokenprovider.http.HttpHandlerSpy;

public class GatekeeperTokenProviderTest {
	private HttpHandlerSpy httpHandler;
	private HttpHandlerFactorySpy httpHandlerFactory;
	private GatekeeperTokenProvider tokenProvider;

	@BeforeMethod
	public void setUp() {
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

		assertEquals(httpHandler.requestMetod, "POST");
		assertEquals(httpHandler.url, "http://localhost:8080/gatekeeper/rest/authToken");
	}

}
