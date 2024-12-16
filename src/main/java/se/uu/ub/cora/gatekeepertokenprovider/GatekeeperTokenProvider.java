/*
 * Copyright 2017, 2024 Uppsala University Library
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

public interface GatekeeperTokenProvider {
	/**
	 * Generates and retrieves an authentication token for the given user information.
	 *
	 * @param userInfo
	 *            the {@link UserInfo} object containing details about the user
	 * @return an {@link AuthToken} associated with the provided user information
	 */
	AuthToken getAuthTokenForUserInfo(UserInfo userInfo);

	/**
	 * Removes an existing authentication token from Gatekeeper system.
	 *
	 * @param tokenId
	 *            the unique identifier of the token to be removed
	 * @param authToken
	 *            the authentication token to be removed
	 * @throws AuthenticationException
	 *             if the token cannot be removed due to authorization issues
	 */
	void removeAuthToken(String tokenId, String authToken);
}
