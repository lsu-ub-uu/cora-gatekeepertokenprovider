/*
 * Copyright 2016 Uppsala University Library
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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.gatekeepertokenprovider.AuthToken;

public class JsonToAuthTokenConverterTest {
	@Test
	public void testJsonToUserInfoConverter() {
		String jsonAuthToken = "{\"children\":[" + "{\"name\":\"token\",\"value\":\"someToken\"},"
				+ "{\"name\":\"validForNoSeconds\",\"value\":\"400\"},"
				+ "{\"name\":\"idInUserStorage\",\"value\":\"someIdFromStorage\"},"
				+ "{\"name\":\"loginId\",\"value\":\"someLoginId\"}" + "],\"name\":\"authToken\"}";
		JsonToAuthTokenConverter converter = JsonToAuthTokenConverter.forJson(jsonAuthToken);

		AuthToken authToken = converter.parseAuthTokenFromJson();

		assertEquals(authToken.token, "someToken");
		assertEquals(authToken.validForNoSeconds, 400);
		assertEquals(authToken.idInUserStorage, "someIdFromStorage");
		assertEquals(authToken.loginId, "someLoginId");
	}

}
