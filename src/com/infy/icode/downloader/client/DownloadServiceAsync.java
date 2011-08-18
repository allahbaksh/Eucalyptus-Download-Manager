package com.infy.icode.downloader.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface DownloadServiceAsync {
	void download(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;
}
