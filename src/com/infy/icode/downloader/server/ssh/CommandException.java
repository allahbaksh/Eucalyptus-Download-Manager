package com.infy.icode.downloader.server.ssh;

import java.util.List;

public class CommandException extends Exception {
	private static final long serialVersionUID = 353831916937701732L;
	private List<CommandOutput> commandOutputs;
	
	public CommandException(Throwable cause, List<CommandOutput> commandOutputs) {
		super(cause);
		this.commandOutputs = commandOutputs;
	}

	public List<CommandOutput> getCommandOutputs() {
		return commandOutputs;
	}
}
