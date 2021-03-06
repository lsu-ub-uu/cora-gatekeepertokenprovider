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

public final class AuthToken {

	public final String token;
	public final int validForNoSeconds;
	public final String idInUserStorage;
	public final String idFromLogin;
	public String firstName;
	public String lastName;

	private AuthToken(String id, int validForNoSeconds, String idInUserStorage, String idFromLogin) {
		this.token = id;
		this.validForNoSeconds = validForNoSeconds;
		this.idInUserStorage = idInUserStorage;
		this.idFromLogin = idFromLogin;
	}

	public static AuthToken withIdAndValidForNoSecondsAndIdInUserStorageAndIdFromLogin(String id,
			int validForNoSeconds, String idInUserStorage, String idFromLogin) {
		return new AuthToken(id, validForNoSeconds, idInUserStorage, idFromLogin);
	}

}
