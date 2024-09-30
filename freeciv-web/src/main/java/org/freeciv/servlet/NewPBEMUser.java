/*******************************************************************************
 * Freeciv-web - the web version of Freeciv. http://www.FreecivX.net/
 * Copyright (C) 2009-2017 The Freeciv-web project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.freeciv.servlet;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.sql.*;
import javax.sql.*;

import org.apache.commons.codec.digest.DigestUtils;
import javax.naming.*;

import org.freeciv.services.Validation;
import org.freeciv.util.Constants;


/**
 * Creates a new play by email user account.
 *
 * URL: /create_pbem_user
 */
public class NewPBEMUser extends HttpServlet {

	private static final int ACTIVATED = 1;
	
	private static final long serialVersionUID = 1L;

	private final Validation validation = new Validation();


	public void init(ServletConfig config) throws ServletException {
		super.init(config);

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String username = java.net.URLDecoder.decode(request.getParameter("username"), StandardCharsets.UTF_8);
		String password = java.net.URLDecoder.decode(request.getParameter("password"), StandardCharsets.UTF_8);
		String email = java.net.URLDecoder.decode(request.getParameter("email").replace("+", "%2B"), StandardCharsets.UTF_8);


		String ipAddress = request.getHeader("X-Real-IP");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}

		if (password == null || password.length() <= 2) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Invalid password. Please try again with another password.");
			return;
		}
		if (!validation.isValidUsername(username)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Invalid username. Please try again with another username.");
			return;
		}
		if (email == null || email.length() <= 4) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Invalid e-mail address. Please try again with another username.");
			return;
		}

		Connection conn = null;
		try {
			Thread.sleep(300);

			Context env = (Context) (new InitialContext().lookup(Constants.JNDI_CONNECTION));
			DataSource ds = (DataSource) env.lookup(Constants.JNDI_DDBBCON_MYSQL);
			conn = ds.getConnection();

			String query = "INSERT INTO auth (username, email, secure_hashed_password, activated, ip) "
							+ "VALUES (?, ?, ?, ?, ?)";
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, username.toLowerCase());
			preparedStatement.setString(2, email);
			preparedStatement.setString(3, DigestUtils.sha256Hex(password));
			preparedStatement.setInt(4, ACTIVATED);
			preparedStatement.setString(5, ipAddress);
			preparedStatement.executeUpdate();


			String userIdQuery =
					"SELECT id "
							+ "FROM auth "
							+ "WHERE LOWER(username) = LOWER(?) "
							+ "	AND activated = '1' LIMIT 1";
			PreparedStatement ps1 = conn.prepareStatement(userIdQuery);
			ps1.setString(1, username);
			ResultSet rs1 = ps1.executeQuery();
			if (!rs1.next()) {
				response.getOutputStream().print("Failed");
			} else {
				response.getOutputStream().print("OK," + rs1.getString(1));
			}

		} catch (Exception err) {
			response.setHeader("result", "error");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unable to create user.");
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "This endpoint only supports the POST method.");

	}

}
