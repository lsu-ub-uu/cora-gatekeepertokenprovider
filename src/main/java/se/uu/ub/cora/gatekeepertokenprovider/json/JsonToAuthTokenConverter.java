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

package se.uu.ub.cora.gatekeepertokenprovider.json;

import java.util.HashMap;
import java.util.Map;

import se.uu.ub.cora.gatekeepertokenprovider.AuthToken;
import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public final class JsonToAuthTokenConverter {

	private static final String CHILDREN = "children";

	private String jsonAuthToken;
	private Map<String, String> childValues;

	private JsonToAuthTokenConverter(String jsonAuthToken) {
		this.jsonAuthToken = jsonAuthToken;
	}

	public AuthToken parseAuthTokenFromJson() {
		JsonParser jsonParser = new OrgJsonParser();
		JsonObject jsonUser = (JsonObject) jsonParser.parseString(jsonAuthToken);
		childValues = extractChildValuesToArray(jsonUser);
		return createAuthTokenFromChildValues(childValues);
	}

	public static JsonToAuthTokenConverter forJson(String jsonAuthToken) {
		return new JsonToAuthTokenConverter(jsonAuthToken);
	}

	private Map<String, String> extractChildValuesToArray(JsonObject jsonUser) {
		JsonArray children = jsonUser.getValueAsJsonArray(CHILDREN);
		childValues = new HashMap<>();
		for (JsonValue child : children) {
			JsonObject childObject = (JsonObject) child;
			childValues.put(childObject.getValueAsJsonString("name").getStringValue(),
					childObject.getValueAsJsonString("value").getStringValue());
		}
		return childValues;
	}

	private AuthToken createAuthTokenFromChildValues(Map<String, String> childValues) {
		AuthToken authToken = AuthToken.withIdAndValidForNoSecondsAndIdInUserStorageAndIdFromLogin(
				childValues.get("id"), Integer.parseInt(childValues.get("validForNoSeconds")),
				childValues.get("idInUserStorage"), childValues.get("idFromLogin"));
		authToken.firstName = childValues.get("firstName");
		authToken.lastName = childValues.get("lastName");
		return authToken;
	}

}
