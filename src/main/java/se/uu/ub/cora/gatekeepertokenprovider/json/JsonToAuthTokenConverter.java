/*
 * Copyright 2017, 2024, 2025 Uppsala University Library
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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import se.uu.ub.cora.gatekeepertokenprovider.AuthToken;
import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public final class JsonToAuthTokenConverter {
	private static final String VALUE = "value";
	private static final String NAME = "name";
	private static final String CHILDREN = "children";
	private String jsonAuthToken;
	private Map<String, String> childValues = new HashMap<>();
	private Set<String> permissionUnits = new LinkedHashSet<>();

	public static JsonToAuthTokenConverter forJson(String jsonAuthToken) {
		return new JsonToAuthTokenConverter(jsonAuthToken);
	}

	private JsonToAuthTokenConverter(String jsonAuthToken) {
		this.jsonAuthToken = jsonAuthToken;
	}

	public AuthToken parseAuthTokenFromJson() {
		JsonParser jsonParser = new OrgJsonParser();
		JsonObject jsonAuthTokenObject = (JsonObject) jsonParser.parseString(jsonAuthToken);
		extractChildValues(jsonAuthTokenObject);
		return createAuthTokenFromChildValuesAndPermissionUnits();
	}

	private void extractChildValues(JsonObject jsonUser) {
		JsonArray children = jsonUser.getValueAsJsonArray(CHILDREN);
		for (JsonValue child : children) {
			parseChild((JsonObject) child);
		}
	}

	private void parseChild(JsonObject child) {
		String name = child.getValueAsJsonString(NAME).getStringValue();
		if ("permissionUnit".equals(name)) {
			extractPermissionUnits(child);
		} else {
			extractAtomicValue(child, name);
		}
	}

	private void extractPermissionUnits(JsonObject childObject) {
		JsonArray permissionUnitChildren = childObject.getValueAsJsonArray(CHILDREN);
		permissionUnitChildren.forEach(child -> possiblyParsePermissionUnitId((JsonObject) child));
	}

	private void possiblyParsePermissionUnitId(JsonObject permissionUnitChild) {
		String permissionUnitName = permissionUnitChild.getValueAsJsonString(NAME).getStringValue();
		if ("linkedRecordId".equals(permissionUnitName)) {
			String permissionUnitValue = permissionUnitChild.getValueAsJsonString(VALUE)
					.getStringValue();
			permissionUnits.add(permissionUnitValue);
		}
	}

	private void extractAtomicValue(JsonObject childObject, String name) {
		childValues.put(name, childObject.getValueAsJsonString(VALUE).getStringValue());
	}

	private AuthToken createAuthTokenFromChildValuesAndPermissionUnits() {
		return new AuthToken(childValues.get("token"), childValues.get("tokenId"),
				Long.parseLong(childValues.get("validUntil")),
				Long.parseLong(childValues.get("renewUntil")), childValues.get("idInUserStorage"),
				childValues.get("loginId"), Optional.ofNullable(childValues.get("firstName")),
				Optional.ofNullable(childValues.get("lastName")), permissionUnits);
	}
}
