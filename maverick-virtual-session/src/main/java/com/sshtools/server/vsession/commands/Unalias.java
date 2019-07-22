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
 * along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.sshtools.server.vsession.commands;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.sshtools.server.vsession.ShellCommand;
import com.sshtools.server.vsession.ShellCommandWithOptions;
import com.sshtools.server.vsession.VirtualConsole;

/**
 * Usage: unalias [-a] name [name ...]
 * @author lee
 *
 */
public class Unalias extends ShellCommandWithOptions {
	

	public Unalias() {
		super("unalias", ShellCommand.SUBSYSTEM_SHELL, "Usage: unalias [-a] name [name ...]", 
				"Unset an alias that has previously been set.",
				new Option("a", false, "Print current values"));
		setBuiltIn(true);
	}

	public void run(CommandLine cli, VirtualConsole console) throws IOException {
		String username = console.getConnection().getUsername();


		Map<String, String> aliaslist;
		if (!Alias.userlist.containsKey(username)) {
			Alias.userlist.put(username, new HashMap<String, String>());
		}
		aliaslist = Alias.userlist.get(username);
		
		String[] args = cli.getArgs();
		if (!cli.hasOption('a') && args.length > 1) {
			boolean skip = true;
			for(String arg : args) {
				if(skip) {
					skip = false;
					continue;
				}
				if(aliaslist.containsKey(arg)) {
					aliaslist.remove(arg);
				} else {
					console.println("unalias: " + arg + ": not found");
				}
			}
			
		} else {
			if (Alias.userlist.containsKey(username)) {
				Alias.userlist.remove(username);
			} 
		} 
	}
}