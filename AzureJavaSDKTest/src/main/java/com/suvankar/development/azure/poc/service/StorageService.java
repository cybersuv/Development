package com.suvankar.development.azure.poc.service;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.CopyStatus;
import com.microsoft.azure.storage.file.CloudFile;
import com.microsoft.azure.storage.file.CloudFileClient;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;
import com.microsoft.azure.storage.file.FileRequestOptions;
import com.suvankar.development.azure.poc.storage.AzStorageCredential;

import ch.qos.logback.classic.Logger;

@Service
public class StorageService {

	@Autowired
	AzStorageCredential azCredential;
	
	static Logger logger = (Logger) LoggerFactory.getLogger("defaultLogger");
	
	private String getConnectionString(){
		// https://suvstorage1711.file.core.windows.net/?sv=2017-04-17&ss=f&srt=sco&sp=rwdlc&se=2020-11-24T01:38:07Z&st=2017-11-22T17:38:07Z&spr=https,http&sig=BeJLm7%2Fb7hjzvRgMyY7DXIm7ca8cNF3zAWfhaL6K9a8%3D
		
		
		//comp=list&restype=container
		
		String storageConnectionString =
		    "DefaultEndpointsProtocol=https;" +
		    "AccountName="+azCredential.getAzStorageAccountName()+";" +
		    "AccountKey="+azCredential.getAzStorageKey()+";"+
		    "EndpointSuffix=core.windows.net";
		///?sv=2017-04-17&ss=f&srt=sco&sp=rwdlc&se=2020-11-24T01:38:07Z&st=2017-11-22T17:38:07Z&spr=https,http&sig=BeJLm7%2Fb7hjzvRgMyY7DXIm7ca8cNF3zAWfhaL6K9a8%3D&comp=list
		return storageConnectionString;
	}
	
	private String getFileNameFromLocation(String fileLocation){
		return fileLocation.split("/")[fileLocation.split("/").length-1];
	}
	
	public static String getStackTrace(Exception e)
	{
	    StringWriter sWriter = new StringWriter();
	    PrintWriter pWriter = new PrintWriter(sWriter);
	    e.printStackTrace(pWriter);
	    return sWriter.toString();
	}
	
	public boolean storeFile(MultipartFile multipartFile,String fileUploadPath) throws Exception, IOException{
		File localFile = new File(fileUploadPath+multipartFile.getOriginalFilename());
		multipartFile.transferTo(localFile);
		logger.trace("Saved local copy of \""+ multipartFile.getOriginalFilename() + "\"");
		logger.trace("Initiating Azure storage upload..");
		if(storeFileToAzureBlob(fileUploadPath+multipartFile.getOriginalFilename())){
			logger.trace("Cloud upload is successful. Deleting local copy..");
			localFile.delete();
			logger.trace("Deleted!!");
			return true;
		}else{
			logger.trace("Cloud upload failed. Keeping the local file.");
			return false;
		}
	}
	
	private boolean storeFileToAzure(String fileLocation) throws Exception{
		try {
			FileRequestOptions fro = new FileRequestOptions();
			fro.setRequireEncryption(Boolean.FALSE);
			logger.trace("Using the CloudStorageAccount object to connect to the storage account");
		    CloudStorageAccount storageAccount = CloudStorageAccount.parse(getConnectionString());
		    logger.trace("Connected!");
		    logger.trace("Creating the Azure Files client..");
		    CloudFileClient fileClient = storageAccount.createCloudFileClient();
		    fileClient.setDefaultRequestOptions(fro);
		    logger.trace("Getting a reference to the file share \""+ azCredential.getAzShare() +"\"....");
		    CloudFileShare share = fileClient.getShareReference(azCredential.getAzShare());
		    logger.trace("Getting a reference to the root directory for the share...");
		    CloudFileDirectory rootDir = share.getRootDirectoryReference();
		    logger.trace("Getting reference to target directory inbound/input...");
		    CloudFileDirectory targetDir = rootDir.getDirectoryReference("inbound").getDirectoryReference("input");
		    logger.trace("Creating a file reference in the target directory for file :"+getFileNameFromLocation(fileLocation).toLowerCase()+" ...");
		    //StorageCredentials cred= StorageCredentials.tryParseCredentials(getConnectionString());
		    CloudFile cloudFile = targetDir.getFileReference(getFileNameFromLocation(fileLocation).toLowerCase());
		    logger.trace("Uploading file..");
	        cloudFile.uploadFromFile(fileLocation);
	        logger.trace("Success!!");
	        logger.trace("Returning TRUE..");
	        return true;
		} catch (Exception ex) {
			logger.error("Exception occured in file upload \n" + getStackTrace(ex));
			logger.trace("Returning FALSE..");
			throw ex;
			//return false;
		}
	}
	
	private boolean storeFileToAzureBlob(String fileLocation) throws Exception{
		try {
			logger.trace("Using the CloudStorageAccount object to connect to the storage account");
		    CloudStorageAccount storageAccount = CloudStorageAccount.parse(getConnectionString());
		    logger.trace("Connected!");
		    logger.trace("Creating the Azure Blob client..");
		    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
		    logger.trace("Getting a reference to the Blob container \""+ azCredential.getAzShare() +"\"....");
		    CloudBlobContainer container = blobClient.getContainerReference(azCredential.getAzShare());
		    logger.trace("Getting a reference to the root directory for the share...");
		    logger.trace("Getting reference to target directory inbound/input...");
		    CloudBlockBlob blob = container.getBlockBlobReference("inbound/input/" + getFileNameFromLocation(fileLocation));
		    logger.trace("Creating a file reference in the target directory for file :"+getFileNameFromLocation(fileLocation).toLowerCase()+" ...");
		    blob.uploadFromFile(fileLocation);
		    /*CloudBlockBlob namedBlob = container.getBlockBlobReference("inbound/input/" + getFileNameFromLocation(fileLocation));
		    namedBlob.startCopy(blob);
		    waitForCopyToComplete(namedBlob);
		    blob.deleteIfExists();*/
	        logger.trace("Success!!");
	        logger.trace("Returning TRUE..");
	        return true;
		} catch (Exception ex) {
			logger.error("Exception occured in file upload \n" + getStackTrace(ex));
			logger.trace("Returning FALSE..");
			throw ex;
			//return false;
		}
	}
	
	private static void waitForCopyToComplete(CloudBlob blob) throws InterruptedException, StorageException {
        CopyStatus copyStatus = CopyStatus.PENDING;
        while (copyStatus == CopyStatus.PENDING) {
            Thread.sleep(1000);
            copyStatus = blob.getCopyState().getStatus();
        }
}
}
