/**
 * (c) 2002-2019 JADAPTIVE Limited. All Rights Reserved.
 *
 * This file is part of the Maverick Synergy Java SSH API.
 *
 * Maverick Synergy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Maverick Synergy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Maverick Synergy.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.sshtools.client;

/**
 *  Represents a keyboard-interactive prompt.
 */
public class KeyboardInteractivePrompt {
	private String prompt;
	private String response;
	private boolean echo;

	/**
	 * Creates a new KBIPrompt object.
	 *
	 * @param prompt
	 * @param echo
	 */
	public KeyboardInteractivePrompt(String prompt, boolean echo) {
		this.prompt = prompt;
		this.echo = echo;
	}

	/**
	 * Get the prompt message to display to the user
	 *
	 * @return String
	 */
	public String getPrompt() {
		return prompt;
	}

	/**
	 * <tt>true</tt> if the user response should be echo'd to the display,
	 * otherwise <tt>false</tt>.
	 *
	 * @return boolean
	 */
	public boolean echo() {
		return echo;
	}

	/**
	 * Set the user's response for this prompt.
	 *
	 * @param response
	 */
	public void setResponse(String response) {
		this.response = response;
	}

	/**
	 * Get the user's response for this prompt.
	 *
	 * @return String
	 */
	public String getResponse() {
		return response;
	}
}
