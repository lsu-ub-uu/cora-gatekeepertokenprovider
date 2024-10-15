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

import se.uu.ub.cora.gatekeepertokenprovider.UserInfo;
import se.uu.ub.cora.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class UserInfoToJsonConverter {

	private static final String VALUE = "value";
	private static final String NAME = "name";
	private static final String CHILDREN = "children";
	private UserInfo userInfo;
	private OrgJsonBuilderFactoryAdapter orgJsonBuilderFactoryAdapter = new OrgJsonBuilderFactoryAdapter();

	public UserInfoToJsonConverter(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public String convertUserInfoToJson() {
		JsonObjectBuilder userBuilder = createObjectBuilderWithName("userInfo");
		addData(userBuilder);
		return userBuilder.toJsonFormattedString();
	}

	private void addData(JsonObjectBuilder userBuilder) {
		JsonArrayBuilder userChildren = returnAndAddChildrenToBuilder(userBuilder);

		if (idInUserStorageExists()) {
			addIdInUserStorageToJson(userChildren);
		} else {
			addLoginIdToJson(userChildren);
			addDomainFromLoginToJson(userChildren);
		}
	}

	private void addIdInUserStorageToJson(JsonArrayBuilder userChildren) {
		JsonObjectBuilder id = createObjectBuilderWithName("idInUserStorage");
		id.addKeyString(VALUE, userInfo.userId);
		userChildren.addJsonObjectBuilder(id);
	}

	private boolean idInUserStorageExists() {
		return userInfo.userId != null && !"".equals(userInfo.userId);
	}

	private JsonObjectBuilder createObjectBuilderWithName(String name) {
		JsonObjectBuilder roleBuilder = orgJsonBuilderFactoryAdapter.createObjectBuilder();
		roleBuilder.addKeyString(NAME, name);
		return roleBuilder;
	}

	private void addLoginIdToJson(JsonArrayBuilder userChildren) {
		JsonObjectBuilder id = createObjectBuilderWithName("loginId");
		id.addKeyString(VALUE, userInfo.loginId);
		userChildren.addJsonObjectBuilder(id);
	}

	private void addDomainFromLoginToJson(JsonArrayBuilder userChildren) {
		JsonObjectBuilder domainFromLogin = createObjectBuilderWithName("domainFromLogin");
		domainFromLogin.addKeyString(VALUE, String.valueOf(userInfo.domainFromLogin));
		userChildren.addJsonObjectBuilder(domainFromLogin);
	}

	private JsonArrayBuilder returnAndAddChildrenToBuilder(JsonObjectBuilder userBuilder) {
		JsonArrayBuilder userChildren = orgJsonBuilderFactoryAdapter.createArrayBuilder();
		userBuilder.addKeyJsonArrayBuilder(CHILDREN, userChildren);
		return userChildren;
	}
}
