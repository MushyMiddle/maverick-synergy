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
package com.sshtools.vsession.commands.ssh;

import java.util.Arrays;

public class SshClientArguments {

	private int port = 22;
	private String destination;
	private String identityFile;
	private String loginName;
	private String command;
	private String[] ciphers;
	private String[] hmacs;
	private String securityLevel;
	private String[] configOptions;
	private boolean compression;
	

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getIdentityFile() {
		return identityFile;
	}

	public void setIdentityFile(String identityFile) {
		this.identityFile = identityFile;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String[] getCiphers() {
		return ciphers;
	}

	public void setCiphers(String[] ciphers) {
		this.ciphers = ciphers;
	}

	public String[] getHmacs() {
		return hmacs;
	}

	public void setHmacs(String[] hmacs) {
		this.hmacs = hmacs;
	}

	public String getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(String securityLevel) {
		this.securityLevel = securityLevel;
	}

	public String[] getConfigOptions() {
		return configOptions;
	}

	public void setConfigOptions(String[] configOptions) {
		this.configOptions = configOptions;
	}

	public boolean isCompression() {
		return compression;
	}

	public void setCompression(boolean compression) {
		this.compression = compression;
	}

	@Override
	public String toString() {
		return "SshClientArguments [port=" + port + ", destination=" + destination + ", identityFile=" + identityFile
				+ ", loginName=" + loginName + ", command=" + command + ", ciphers=" + Arrays.toString(ciphers)
				+ ", hmacs=" + Arrays.toString(hmacs) + ", securityLevel=" + securityLevel + ", configOptions="
				+ Arrays.toString(configOptions) + ", compression=" + compression + "]";
	}

}
