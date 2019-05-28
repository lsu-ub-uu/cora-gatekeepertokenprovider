/*
 * Copyright 2017 Uppsala University Library
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

import se.uu.ub.cora.gatekeepertokenprovider.authentication.AuthenticationException;
import se.uu.ub.cora.gatekeepertokenprovider.json.JsonToAuthTokenConverter;
import se.uu.ub.cora.gatekeepertokenprovider.json.UserInfoToJsonConverter;
import se.uu.ub.cora.httphandler.HttpHandler;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;

public final class GatekeeperTokenProviderImp implements GatekeeperTokenProvider {
	private Logger log = LoggerProvider.getLoggerForClass(GatekeeperTokenProviderImp.class);
	private static final int STATUS_OK = 200;
	private static final String APPLICATION_UUB_RECORD_JSON = "application/vnd.uub.record+json";
	private static final String ACCEPT = "Accept";
	private String gatekeeperUrl;
	private HttpHandlerFactory httpHandlerFactory;

	private GatekeeperTokenProviderImp(String gatekeeperUrl,
			HttpHandlerFactory httpHandlerFactory) {
		this.gatekeeperUrl = gatekeeperUrl;
		this.httpHandlerFactory = httpHandlerFactory;
		log.logInfoUsingMessage("Using " + gatekeeperUrl + " as gatekeeperUrl.");
	}

	public static GatekeeperTokenProviderImp usingBaseUrlAndHttpHandlerFactory(String gatekeeperUrl,
			HttpHandlerFactory httpHandlerFactory) {
		return new GatekeeperTokenProviderImp(gatekeeperUrl, httpHandlerFactory);
	}

	@Override
	public AuthToken getAuthTokenForUserInfo(UserInfo userInfo) {
		String url = gatekeeperUrl + "rest/authToken";

		UserInfoToJsonConverter userInfoToJsonConverter = new UserInfoToJsonConverter(userInfo);
		String json = userInfoToJsonConverter.convertUserInfoToJson();

		HttpHandler httpHandler = httpHandlerFactory.factor(url);
		httpHandler.setRequestMethod("POST");
		httpHandler.setRequestProperty(ACCEPT, APPLICATION_UUB_RECORD_JSON);
		httpHandler.setRequestProperty("Content-Type", APPLICATION_UUB_RECORD_JSON);
		httpHandler.setOutput(json);

		if (httpHandler.getResponseCode() != STATUS_OK) {
			throw new AuthenticationException("authToken gives no authorization:"
					+ httpHandler.getResponseCode() + " url:  " + url + " json: " + json);
		}
		JsonToAuthTokenConverter jsonToAuthTokenConverter = JsonToAuthTokenConverter
				.forJson(httpHandler.getResponseText());
		return jsonToAuthTokenConverter.parseAuthTokenFromJson();
	}

	@Override
	public void removeAuthTokenForUser(String idInUserStorage, String authToken) {
		String url = gatekeeperUrl + "rest/authToken/" + idInUserStorage;

		HttpHandler httpHandler = httpHandlerFactory.factor(url);
		httpHandler.setRequestMethod("DELETE");
		httpHandler.setOutput(authToken);

		if (httpHandler.getResponseCode() != STATUS_OK) {
			throw new AuthenticationException("AuthToken could not be removed");
		}
	}

	public String getGatekeeperUrl() {
		// needed for test
		return gatekeeperUrl;
	}

	public HttpHandlerFactory getHttpHandlerFactory() {
		// needed for test
		return httpHandlerFactory;
	}
}
