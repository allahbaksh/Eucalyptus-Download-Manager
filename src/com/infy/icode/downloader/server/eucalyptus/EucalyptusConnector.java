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
package com.infy.icode.downloader.server.eucalyptus;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.infy.icode.downloader.server.DownloadServiceImpl;
import com.infy.icode.downloader.server.ssh.SSH;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.LaunchConfiguration;
import com.xerox.amazonws.ec2.ReservationDescription;
import com.xerox.amazonws.ec2.ReservationDescription.Instance;

public class EucalyptusConnector {
	public static final String ATTACHED = "attached";
	public static final String AVAILABLE = "available";

	private static final String MOUNT_POINT = "/volume1";
	private static final String EUCALYPTUS_URL = "ecc.eucalyptus.com";

	private Properties props = new Properties();
	private List<String> owners = new ArrayList<String>();
	private String sudo = "";
	private Jec2 cloud = null;
	private String ipAddress;

	public static void main(String[] args) throws Exception {

		String url = "http://download.winscp.net/download/files/2011081607377f4d9c692ef19333291ef20d83b40bf8/winscp434setup.exe";

		String fileName = url.substring(url.lastIndexOf("/") + 1);
		System.out.println(fileName);
		// EucalyptusConnector typicaTutorial = new
		// EucalyptusConnector(args[0]);
		// typicaTutorial.start(args[0]);
	}

	public EucalyptusConnector(String typeOfCloud, Properties inProp)
			throws Exception {
		// props.load(getClass().getClassLoader().getResourceAsStream("euca.properties"));
		props = inProp;

		System.out.println("properties file population : "
				+ props.getProperty("cloud.username"));

		owners.add(props.getProperty("cloud.username").trim());
		sudo = props.getProperty("vmAuth.sudo").trim();
		if ("euca".equals(typeOfCloud)) {
			cloud = new Jec2(props.getProperty("cloud.accessId").trim(), props
					.getProperty("cloud.secretKey").trim(), false, props
					.getProperty("cloud.URL").trim(), 8773);
			cloud.setResourcePrefix("/services/Eucalyptus");
			cloud.setSignatureVersion(1);
		} else {
			cloud = new Jec2(props.getProperty("cloud.accessId").trim(), props
					.getProperty("cloud.secretKey").trim());
		}

	}

	/**
	 * This is main method to be called.
	 * 
	 * @param arg
	 * @param url
	 * @throws Exception
	 */
	public String start(String url) throws Exception {
		// FIXME DELETE THE FILE KNOW_HOST
		// File file = new
		// File("C:\\Documents and Settings\\allahbaksh_asadullah\\.ssh\\")
		Instance instance = createInstance();

		downloadRequest(instance, url);
		String fileName = url.substring(url.lastIndexOf("/") + 1);
		// Download from Warlus to local machine. Save it and provide the link
		// back to the UR

		copyToLocalFolder(fileName);
		// terminateInstance(instance);
		cloud.disassociateAddress(ipAddress);

		// FIXME Deallocate the ip address

		return fileName;

	}

	private String copyToLocalFolder(String fileName) {
		try {
			URL google

			= new URL("http://173.205.188.130:8773/services/Walrus/ab-cred/"
					+ fileName);

			ReadableByteChannel rbc = Channels.newChannel(google.openStream());

			FileOutputStream fos = new FileOutputStream(
					DownloadServiceImpl.PATH + "\\" + fileName);
			fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileName;

	}

	private void downloadRequest(Instance instance, String url)
			throws Exception {
		try {
			String credFileLocation = "";
			SSH.runCommandOnInstance("sudo wget " + credFileLocation,
					ipAddress, props);
			SSH.runCommandOnInstance("sudo tar -xf s3cmd.tar", ipAddress, props);
			SSH.runCommandOnInstance("sudo chmod 777 -R s3cmd-0.9.8.3",
					ipAddress, props);
			SSH.runCommandOnInstance("cd /mnt", ipAddress, props);
			SSH.runCommandOnInstance("sudo wget " + url, ipAddress, props);

			String fileName = url.substring(url.lastIndexOf("/") + 1);
			// Strip down the URL
			String storeCommand = "sudo ~/s3cmd-0.9.8.3/s3cmd -c ~/s3cmd-0.9.8.3/s3cfg.walrus put --acl-public --guess-mime-type "
					+ fileName + " s3://ab-cred/" + fileName;
			SSH.runCommandOnInstance(storeCommand, ipAddress, props);
		} catch (Exception e) {
			// print the error but continue on so the instance gets cleaned up
			e.printStackTrace();
		}
	}

	private Instance createInstance() throws Exception {
		LaunchConfiguration launchConfig = new LaunchConfiguration(props
				.getProperty("launchConfig.imageID").trim());
		launchConfig.setAvailabilityZone(props.getProperty(
				"launchConfig.availabilityZone").trim());
		launchConfig.setKeyName(props.getProperty("launchConfig.keyName")
				.trim());
		launchConfig.setMinCount(Integer.valueOf(props.getProperty(
				"launchConfig.minCount").trim()));
		launchConfig.setMaxCount(Integer.valueOf(props.getProperty(
				"launchConfig.maxCount").trim()));

		ReservationDescription reservationDescription = cloud
				.runInstances(launchConfig);
		Instance instance = reservationDescription.getInstances().get(0);
		String[] instances = new String[] { instance.getInstanceId() };

		// wait for the instance to start running
		do {
			try {
				instance = cloud.describeInstances(instances).get(0)
						.getInstances().get(0);
			} catch (Exception e) {
				System.out.println("Exception");
			}
			System.out.println("Run: Instance ID = " + instance.getInstanceId()
					+ ", State = " + instance.getState());

			Thread.sleep(5000);
		} while (!instance.isRunning());

		ipAddress = cloud.allocateAddress();
		cloud.associateAddress(instance.getInstanceId(), ipAddress);

		System.out.println("Run: Instance ID = " + instance.getInstanceId()
				+ ", Public DNS Name = " + instance.getDnsName()
				+ ", Private DNS Name = " + instance.getPrivateDnsName());

		// takes some time for SSH to start on the VMs so sleep for a bit or we
		// get a connection refused
		System.out.println("SSH: waiting for SSH on VM to start");
		Thread.sleep(30000);

		return instance;
	}

	private void terminateInstance(Instance instance) throws Exception {
		String[] instances = new String[] { instance.getInstanceId() };

		try {
			// don't wait for the instance to terminate as this can take some
			// time and it's not necessary to wait to move forward
			cloud.terminateInstances(instances);
		} catch (NullPointerException e) {
			// the call to terminate the instance goes through but it looks like
			// Eucalyptus returns a null value and typica chokes on it, ignore
			// it
			// see http://code.google.com/p/typica/issues/detail?id=105
		}

		System.out.println("Terminate: Instance ID = "
				+ instance.getInstanceId() + ", State = shutting-down");
	}
}
