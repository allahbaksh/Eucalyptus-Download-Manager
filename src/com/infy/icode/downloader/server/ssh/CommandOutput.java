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

import java.io.IOException;

import net.schmizz.sshj.connection.channel.direct.Session.Command;

public class CommandOutput {
	private String commandStr;
	private String standardOutput;
	private String standardError;
	private Integer exitStatus;
	
	public CommandOutput(String commandStr, Command command) throws IOException {
		this.commandStr = commandStr;
		this.standardOutput = command.getOutputAsString();
		this.standardError = command.getErrorAsString();
		this.exitStatus = command.getExitStatus();
	}
	
	public String getCommandStr() {
		return commandStr;
	}
	public String getStandardOutput() {
		return standardOutput;
	}
	public String getStandardError() {
		return standardError;
	}
	public Integer getExitStatus() {
		return exitStatus;
	}
	
	
}
