package com.smartvalue.apigee.rest.schema.keyValueMap;

import java.io.IOException;
import java.util.ArrayList;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.smartvalue.apigee.resourceManager.ManagementServer;

public class KvmServices extends com.smartvalue.apigee.rest.schema.Service {
	
	

	public KvmServices(ManagementServer ms, String m_orgName, String m_envName) {
		super(ms, m_orgName, m_envName);
		// TODO Auto-generated constructor stub
	}

	public ArrayList <String>  getAllKvms() throws UnirestException, IOException
	{
		String apiPath = "/v1/o/"+orgName+"/e/"+envName+"/keyvaluemaps" ; 
		ArrayList<String> result = this.getMs().executeGetMgmntAPI(apiPath, ArrayList.class) ; 
		return result;
		
	}
	
	public KeyValueMap  getKvmDetails( String m_kvmName ) throws UnirestException, IOException
	{
		String apiPath = "/v1/o/"+orgName+"/e/"+envName+"/keyvaluemaps/"+  m_kvmName ; 
		KeyValueMap result = this.getMs().executeGetMgmntAPI(apiPath, KeyValueMap.class) ; 
		return result;
		
	}

}
