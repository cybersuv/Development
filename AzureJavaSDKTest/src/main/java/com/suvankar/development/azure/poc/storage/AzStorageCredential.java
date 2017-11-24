package com.suvankar.development.azure.poc.storage;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Logger;

@Component
public class AzStorageCredential {
	static Logger logger = (Logger) LoggerFactory.getLogger("defaultLogger");
	private static final String PROP_AZ_STORAGE_ACCOUNT_NAME="azure.storage.account.name";
	private static final String PROP_AZ_STORAGE_ACCOUNT_KEY="azure.storage.access.key";
	public final String HEADER_ACCOUNT_NAME="Az-Account";
	public final String HEADER_STORAGE_KEY="Az-Key";
	public final String HEADER_STORAGE_SIGNATURE="Az-Signature";
	public final String HEADER_STORAGE_SHARE="Az-Share";
	private String azStorageAccountName;
	private String azStorageKey;
	private String azSignature;
	private String azShare;
	
	public AzStorageCredential(){
		setAzStorageAccountName(System.getProperty(PROP_AZ_STORAGE_ACCOUNT_NAME));
		setAzStorageKey(System.getProperty(PROP_AZ_STORAGE_ACCOUNT_KEY));
		logger.trace("Storage credentials are set as :\nAccount Name : " + getAzStorageAccountName() + "\nAccount Key : " + getAzStorageKey());
	}

	public String getAzStorageAccountName() {
		return azStorageAccountName;
	}

	public void setAzStorageAccountName(String azStorageAccountName) {
		this.azStorageAccountName = azStorageAccountName;
	}

	public String getAzStorageKey() {
		return azStorageKey;
	}

	public void setAzStorageKey(String azStorageKey) {
		this.azStorageKey = azStorageKey;
	}

	public String getAzSignature() {
		return azSignature;
	}

	public void setAzSignature(String azSignature) {
		this.azSignature = azSignature;
	}

	public String getAzShare() {
		return azShare;
	}

	public void setAzShare(String azShare) {
		this.azShare = azShare;
	}
	
	public void printCredential(){
		logger.trace("=== Printing set Azure Credential Attributes ===\n### START ###\nAccount Name : "+
					this.azStorageAccountName+"\nAccount Key : "+this.azStorageKey+
					"\nSAS String :"+this.azSignature+"\nShare Name : "+this.azShare+"\n### END ###");
	}
	
	
}
