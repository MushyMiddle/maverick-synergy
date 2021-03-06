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

package com.sshtools.common.command;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.sshtools.common.logger.Log;
import com.sshtools.common.util.IOStreamConnector;

/**
 * A further extension of the {@link ExecutableCommand} that provides the
 * ability to execute a native process. Caution should be taken with this
 * current implementation as no processing of EOL is provided and results may
 * vary on different platforms. It is recommended that only automated clients
 * use this feature due to the many differences in user terminals which may
 * cause problems with command output not performing as expected.
 *
 * 
 */
public class NativeExecutableCommand extends ExecutableCommand {

	
	Process process;
	String[] commandLine;
	String[] env;
	ProcessThread thread;

	int exitValue = STILL_ACTIVE;

	public NativeExecutableCommand() {
	}

	public void onStart() {
		thread.start();
	}

	public int getExitCode() {
		return exitValue;
	}

	public boolean createProcess(String[] commandLine, Map<String, String> environment) {

		if(commandLine.length==0) {
			return false;
		}
		
		if(Log.isDebugEnabled())
			Log.debug("Creating native process: " + commandLine[0]);

		this.commandLine = commandLine;

		// Now process the environment
		Map.Entry<String, String> entry;
		Vector<String> tmp = new Vector<String>();
		if (environment != null) {
			for (Iterator<Map.Entry<String, String>> it = environment.entrySet().iterator(); it.hasNext();) {
				entry = it.next();
				tmp.add(entry.getKey().toString() + "=" + entry.getValue().toString());
			}
		}

		env = new String[tmp.size()];
		tmp.copyInto(env);

		try {
			process = Runtime.getRuntime().exec(commandLine, env);
			thread = new ProcessThread();
			return true;
		} catch (IOException ex) {
			return false;
		}
	}

	public void processStdinData(byte[] data) throws IOException {
		try {
			if (process != null) {
				process.getOutputStream().write(data);
				process.getOutputStream().flush();
			}
		} catch (IOException ex) {
			if(Log.isDebugEnabled())
				Log.debug("Failed to write data into native process", ex);
		}
	}

	public void kill() {
		process.destroy();
	}

	class ProcessThread extends Thread {
		IOStreamConnector stdout;
		IOStreamConnector stderr;

		public void run() {
			try {

				stdout = new IOStreamConnector(process.getInputStream(), getOutputStream());
				stderr = new IOStreamConnector(process.getErrorStream(), getStderrOutputStream());

				exitValue = process.waitFor();
			} catch (Throwable ex) {
				if(Log.isDebugEnabled())
					Log.debug("Native process transfer thread failed", ex);
			}
		}
	}

}
