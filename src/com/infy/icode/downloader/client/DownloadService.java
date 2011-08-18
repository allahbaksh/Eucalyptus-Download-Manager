package com.infy.icode.downloader.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("download")
public interface DownloadService extends RemoteService {
	String download(String name) throws IllegalArgumentException;
}
