/*
 * Copyright 2016, 2024 Uppsala University Library
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
	public void testJsonToUserInfoConverterWithOutNames() {
		String jsonAuthToken = "{\"children\":[" + "{\"name\":\"token\",\"value\":\"someToken\"},"
				+ "{\"name\":\"tokenId\",\"value\":\"someTokenId\"},"
				+ "{\"name\":\"validUntil\",\"value\":\"100\"},"
				+ "{\"name\":\"renewUntil\",\"value\":\"200\"},"
				+ "{\"name\":\"idInUserStorage\",\"value\":\"someIdFromStorage\"},"
				+ "{\"name\":\"loginId\",\"value\":\"someLoginId\"}" + "],\"name\":\"authToken\"}";
		JsonToAuthTokenConverter converter = JsonToAuthTokenConverter.forJson(jsonAuthToken);

		AuthToken authToken = converter.parseAuthTokenFromJson();

		assertEquals(authToken.token(), "someToken");
		assertEquals(authToken.validUntil(), 100L);
		assertEquals(authToken.renewUntil(), 200L);
		assertEquals(authToken.idInUserStorage(), "someIdFromStorage");
		assertEquals(authToken.loginId(), "someLoginId");
	}

	@Test
	public void testJsonToUserInfoConverterWithNames() {
		String jsonAuthToken = "{\"children\":[" + "{\"name\":\"token\",\"value\":\"someToken\"},"
				+ "{\"name\":\"tokenId\",\"value\":\"someTokenId\"},"
				+ "{\"name\":\"validUntil\",\"value\":\"100\"},"
				+ "{\"name\":\"renewUntil\",\"value\":\"200\"},"
				+ "{\"name\":\"idInUserStorage\",\"value\":\"someIdFromStorage\"},"
				+ "{\"name\":\"loginId\",\"value\":\"someLoginId\"},"
				+ "{\"name\":\"firstName\",\"value\":\"someFirstName\"},"
				+ "{\"name\":\"lastName\",\"value\":\"someLastName\"}"
				+ "],\"name\":\"authToken\"}";
		JsonToAuthTokenConverter converter = JsonToAuthTokenConverter.forJson(jsonAuthToken);

		AuthToken authToken = converter.parseAuthTokenFromJson();

		assertEquals(authToken.token(), "someToken");
		assertEquals(authToken.validUntil(), 100L);
		assertEquals(authToken.renewUntil(), 200L);
		assertEquals(authToken.idInUserStorage(), "someIdFromStorage");
		assertEquals(authToken.loginId(), "someLoginId");
		assertEquals(authToken.firstName().get(), "someFirstName");
		assertEquals(authToken.lastName().get(), "someLastName");
	}

}
