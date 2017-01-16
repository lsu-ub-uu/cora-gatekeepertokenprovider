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

import se.uu.ub.cora.gatekeepertokenprovider.UserInfo;

public class UserInfoToJsonConverterTest {

	@Test
	public void testAuthTokenToJsonConverter() {
		UserInfo userInfo = UserInfo.withLoginIdAndLoginDomain("someLoginId", "someDomain");
		UserInfoToJsonConverter converter = new UserInfoToJsonConverter(userInfo);
		String json = converter.convertUserInfoToJson();
		String expected = "{\"children\":["
				+ "{\"name\":\"idFromLogin\",\"value\":\"someLoginId\"},"
				+ "{\"name\":\"domainFromLogin\",\"value\":\"someDomain\"}"
				+ "],\"name\":\"userInfo\"}";
		assertEquals(json, expected);
	}

	@Test
	public void testAuthTokenToJsonConverterEmptyIdInStorage() {
		UserInfo userInfo = UserInfo.withLoginIdAndLoginDomain("someLoginId", "someDomain");
		userInfo.idInUserStorage = "";
		UserInfoToJsonConverter converter = new UserInfoToJsonConverter(userInfo);
		String json = converter.convertUserInfoToJson();
		String expected = "{\"children\":["
				+ "{\"name\":\"idFromLogin\",\"value\":\"someLoginId\"},"
				+ "{\"name\":\"domainFromLogin\",\"value\":\"someDomain\"}"
				+ "],\"name\":\"userInfo\"}";
		assertEquals(json, expected);
	}

	@Test
	public void testAuthTokenToJsonConverterWithIdInUserStorage() {
		UserInfo userInfo = UserInfo.withIdInUserStorage("someId");
		UserInfoToJsonConverter converter = new UserInfoToJsonConverter(userInfo);
		String json = converter.convertUserInfoToJson();
		String expected = "{\"children\":[" + "{\"name\":\"idInUserStorage\",\"value\":\"someId\"}"
				+ "],\"name\":\"userInfo\"}";
		assertEquals(json, expected);
	}
}
