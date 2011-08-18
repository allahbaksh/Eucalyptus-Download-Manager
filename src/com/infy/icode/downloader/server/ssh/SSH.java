/*
 * Copyright (c) 2010, Cybera and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Cybera or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * This software is provided by the copyright holders and contributors "as
 * is" and any express or implied warranties, including, but not limited to,
 * the implied warranties of merchantability and fitness for a particular
 * purpose are disclaimed.  In no event shall the copyright owner or
 * contributors be liable for any direct, indirect, incidental, special,
 * exemplary, or consequential damages (including, but not limited to,
 * procurement of substitute goods or services; loss of use, data, or
 * profits; or business interruption) however caused and on any theory of
 * liability, whether in contract, strict liability, or tort (including
 * negligence or otherwise) arising in any way out of the use of this
 * software, even if advised of the possibility of such damage.
 */
package com.infy.icode.downloader.server.ssh;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.FileKeyProvider;
import net.schmizz.sshj.userauth.keyprovider.PKCS8KeyFile;

public class SSH {
	/**
	 * Main method just to test SSH commands on already running VMs.
	 */
	public static void main(String[] args) throws Exception {
		ArrayList<String> commandStrs = new ArrayList<String>();
		commandStrs.add("ping -c 1 google.com");
		commandStrs
				.add("wget http://typica.googlecode.com/files/typica-1.7.2.zip");
		String dnsName = "173.205.188.134";
		String username = "ubuntu";
		String privateKeyLocation = "d:\\eucalyptus.pem";

		SSH.runCommandsOnInstance(commandStrs, dnsName, username,
				privateKeyLocation);
	}

	public static List<CommandOutput> runCommandsOnInstance(
			List<String> commandStrs, String dnsName, String username,
			String privateKeyLocation) throws Exception {
		System.out.println("SSH: connecting to " + dnsName + " as " + username
				+ " with " + privateKeyLocation);

		ArrayList<CommandOutput> commandOutputs = new ArrayList<CommandOutput>();

		final SSHClient ssh = new SSHClient();
		ssh.addHostKeyVerifier(new PromiscuousVerifier()); // !!! only use the
															// PromiscuousVerifier
															// if the VMs you
															// are connecting to
															// are known and
															// trusted !!!
		ssh.connect(dnsName);

		try {
			File keyFile = new File(privateKeyLocation);
			FileKeyProvider keyProvider = new PKCS8KeyFile();
			keyProvider.init(keyFile);
			ssh.authPublickey(username, keyProvider);

			for (String commandStr : commandStrs) {
				Session session = null;

				try {
					session = ssh.startSession();

					final Command command = session.exec(commandStr);
					CommandOutput commandOutput = new CommandOutput(commandStr,
							command);

					System.out.println("Command: "
							+ commandOutput.getCommandStr());
					System.out.println("Command: stdout = "
							+ commandOutput.getStandardOutput());
					System.out.println("Command: stderr = "
							+ commandOutput.getStandardError());
					System.out.println("Command: exit = "
							+ commandOutput.getExitStatus());

					commandOutputs.add(commandOutput);
				} catch (Exception e) {
					throw new CommandException(e, commandOutputs);
				} finally {
					if (session != null) {
						session.close();
					}
				}
			}
		} finally {
			ssh.disconnect();
		}

		return commandOutputs;
	}

	public static CommandOutput runCommandOnInstance(String commandStr,
			String dnsName, String username, String privateKeyLocation)
			throws Exception {
		ArrayList<String> commandList = new ArrayList<String>();
		commandList.add(commandStr);

		List<CommandOutput> commandOutputs = runCommandsOnInstance(commandList,
				dnsName, username, privateKeyLocation);
		return commandOutputs.get(0);
	}

	public static List<CommandOutput> runCommandsOnInstance(
			List<String> commandStrs, String ipAddress, Properties props)
			throws Exception {
		return runCommandsOnInstance(commandStrs, ipAddress,
				props.getProperty("vmAuth.username").trim(),
				props.getProperty("vmAuth.privateKeyLocation").trim());
	}

	public static CommandOutput runCommandOnInstance(String commandStr,
			String ipAddress, Properties props) throws Exception {
		ArrayList<String> commandList = new ArrayList<String>();
		commandList.add(commandStr);

		List<CommandOutput> commandOutputs = runCommandsOnInstance(commandList,
				ipAddress, props);
		return commandOutputs.get(0);
	}
}
