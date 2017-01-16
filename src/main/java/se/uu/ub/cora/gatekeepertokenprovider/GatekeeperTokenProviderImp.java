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

import se.uu.ub.cora.gatekeepertokenprovider.json.JsonToAuthTokenConverter;
import se.uu.ub.cora.gatekeepertokenprovider.json.UserInfoToJsonConverter;
import se.uu.ub.cora.httphandler.HttpHandler;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
import se.uu.ub.cora.spider.authentication.AuthenticationException;

public final class GatekeeperTokenProviderImp implements GatekeeperTokenProvider {
	private static final int STATUS_OK = 200;
	private static final String APPLICATION_UUB_RECORD_JSON = "application/uub+record+json";
	private static final String ACCEPT = "Accept";
	private String baseUrl;
	private HttpHandlerFactory httpHandlerFactory;

	private GatekeeperTokenProviderImp(String baseUrl, HttpHandlerFactory httpHandlerFactory) {
		this.baseUrl = baseUrl;
		this.httpHandlerFactory = httpHandlerFactory;
	}

	public static GatekeeperTokenProviderImp usingBaseUrlAndHttpHandlerFactory(String baseUrl,
			HttpHandlerFactory httpHandlerFactory) {
		return new GatekeeperTokenProviderImp(baseUrl, httpHandlerFactory);
	}

	@Override
	public AuthToken getAuthTokenForUserInfo(UserInfo userInfo) {
		String url = baseUrl + "rest/authToken";

		UserInfoToJsonConverter userInfoToJsonConverter = new UserInfoToJsonConverter(userInfo);
		String json = userInfoToJsonConverter.convertUserInfoToJson();

		HttpHandler httpHandler = httpHandlerFactory.factor(url);
		httpHandler.setRequestMethod("POST");
		httpHandler.setRequestProperty(ACCEPT, APPLICATION_UUB_RECORD_JSON);
		httpHandler.setRequestProperty("Content-Type", APPLICATION_UUB_RECORD_JSON);
		httpHandler.setOutput(json);

		if (httpHandler.getResponseCode() != STATUS_OK) {
			throw new AuthenticationException("authToken gives no authorization");
		}
		JsonToAuthTokenConverter jsonToAuthTokenConverter = JsonToAuthTokenConverter
				.forJson(httpHandler.getResponseText());
		return jsonToAuthTokenConverter.parseAuthTokenFromJson();
	}

}
