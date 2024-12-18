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
		HttpHandler httpHandler = httpHandlerFactory.factor(url);
		httpHandler.setRequestMethod("POST");
		httpHandler.setRequestProperty("Content-Type", "application/vnd.uub.userInfo+json");
		httpHandler.setRequestProperty(ACCEPT, "application/vnd.uub.authToken+json");
		httpHandler.setOutput(convertUserInfoToJson(userInfo));

		ifStatusNokThrowAuthenticationException(httpHandler.getResponseCode(),
				"AuthToken cannot be created");
		return convertJsonToAuthToken(httpHandler.getResponseText());
	}

	private String convertUserInfoToJson(UserInfo userInfo) {
		UserInfoToJsonConverter userInfoToJsonConverter = new UserInfoToJsonConverter(userInfo);
		return userInfoToJsonConverter.convertUserInfoToJson();
	}

	private void ifStatusNokThrowAuthenticationException(int responseCode, String errorMessage) {
		if (responseCode != STATUS_OK) {
			throw new AuthenticationException(errorMessage);
		}
	}

	private AuthToken convertJsonToAuthToken(String responseText) {
		JsonToAuthTokenConverter jsonToAuthTokenConverter = JsonToAuthTokenConverter
				.forJson(responseText);
		return jsonToAuthTokenConverter.parseAuthTokenFromJson();
	}

	@Override
	public AuthToken renewAuthToken(String tokenId, String token) {
		String url = gatekeeperUrl + "rest/authToken/" + tokenId;
		HttpHandler httpHandler = httpHandlerFactory.factor(url);
		httpHandler.setRequestMethod("POST");
		httpHandler.setRequestProperty("Content-Type", "text/plain");
		httpHandler.setRequestProperty(ACCEPT, "application/vnd.uub.authToken+json");
		httpHandler.setOutput(token);

		ifStatusNokThrowAuthenticationException(httpHandler.getResponseCode(),
				"AuthToken could not be renewed");
		return convertJsonToAuthToken(httpHandler.getResponseText());

	}

	@Override
	public void removeAuthToken(String tokenId, String authToken) {
		String url = gatekeeperUrl + "rest/authToken/" + tokenId;
		HttpHandler httpHandler = httpHandlerFactory.factor(url);
		httpHandler.setRequestMethod("DELETE");
		httpHandler.setRequestProperty("Content-Type", "text/plain");
		httpHandler.setOutput(authToken);

		ifStatusNokThrowAuthenticationException(httpHandler.getResponseCode(),
				"AuthToken could not be removed");
	}

	public String onlyForTestGetGatekeeperUrl() {
		return gatekeeperUrl;
	}

	public HttpHandlerFactory onlyForTersGetHttpHandlerFactory() {
		return httpHandlerFactory;
	}

}
