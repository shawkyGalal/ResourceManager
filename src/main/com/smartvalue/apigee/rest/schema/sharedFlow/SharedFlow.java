package com.smartvalue.apigee.rest.schema.sharedFlow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.smartvalue.apigee.rest.schema.proxyDeployment.ProxyDeployment;
import com.smartvalue.apigee.rest.schema.proxyDeployment.auto.Environment;
import com.smartvalue.apigee.rest.schema.proxyDeployment.auto.Revision;

public class SharedFlow extends com.smartvalue.apigee.rest.schema.sharedFlow.auto.SharedFlow {

	public HashMap<String , Exception>  exportAllDeployedRevisions(String folderDest ) throws NumberFormatException, UnirestException, IOException
	{
		HashMap<String , Exception> failedResult = new HashMap<String , Exception>();  
		
		for ( String  DeployedEnvName :  this.getDeployedRevisions().keySet()) 
		{
			ArrayList<String > envRevisions = this.getDeployedRevisions().get(DeployedEnvName) ;
			for (String revisionString : envRevisions  )
			{
				int revision = Integer.parseInt(revisionString);
				try {
					String path = folderDest+"\\" + DeployedEnvName + "\\"+ this.getName()+"\\" + revision+"\\" ; 
					Path pathObj = Paths.get(path);
			        Files.createDirectories(pathObj);
					export(revision , path) ;
					System.out.println("SharedFlow " + this.getName() + " Revision " +  revision + " Deplyed to Env" + DeployedEnvName +" Exported Successfully");
				}
				catch (Exception e) {
					failedResult.put(revisionString, e); 
				}
			}
				
		}
		
		return failedResult ; 
	}
	
	/**
	 * 
	 * @return a hashmap containing information about the deployments of a proxy 
	 * [
	 * "Env01" : [2 , 5]   // Proxy revisions 2, 5 are deployed to Env01
	 * "Env02" : [1 , 4]   // Proxy revisions 1, 4 are deployed to Env02
	 * ]
	 * @throws UnirestException
	 * @throws IOException
	 */
	public HashMap<String , ArrayList<String>> getDeployedRevisions() throws UnirestException, IOException
	{
		HashMap<String , ArrayList<String>> result = new  HashMap<String , ArrayList<String>>() ; 
		ProxyDeployment proxyDeployment = this.getDeployments();
		for ( Environment env : proxyDeployment.getEnvironment() ) 
		{
			String envName= env.getName(); 
			ArrayList<String> deployedRevision = new ArrayList<String>();  
			for (Revision revision  : env.getRevision())
			{
				deployedRevision.add(revision.getName()); 
			}
			result.put(envName, deployedRevision); 
		}
		
		return result ; 
		
	}
	
	@SuppressWarnings("unchecked")
	public ProxyDeployment  getDeployments() throws UnirestException, IOException
	{
		ProxyDeployment result = null; 
		String apiPath = getResourcePath()+"/deployments" ; 
		result = this.getManagmentServer().executeGetMgmntAPI(apiPath , ProxyDeployment.class ) ;
		return result ; 
	}	


	public String getResourcePath() {
		// TODO Auto-generated method stub
		return "/v1/organizations/"+this.getOrgName()+"/sharedflows/"+this.getName();
	}
}
