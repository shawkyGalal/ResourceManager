package com.smartvalue.apigee.rest.schema.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.smartvalue.apigee.configuration.infra.ManagementServer;
import com.smartvalue.apigee.rest.schema.Service;
import com.smartvalue.apigee.rest.schema.product.Product;
import com.smartvalue.apigee.rest.schema.proxy.Proxy;
import com.smartvalue.apigee.rest.schema.proxy.transformers.TransformResult;

public class ApplicationServices extends Service {

	public ApplicationServices(ManagementServer ms, String m_orgName) {
		super(ms, m_orgName);
		
	}


	@Override
	public ArrayList<HttpResponse<String>> deleteAll() throws UnirestException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResourcePath() {
		
		return "/v1/organizations/"+orgName+"/"+getApigeeObjectType()+"/";
	}
	
	
	public Application getAppByName(String appName) throws Exception
	{
		return this.getResource(appName, Application.class) ; 
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String>  getAllAppsNamesList() throws Exception
	{
		ArrayList<String> appssList = this.getAllResources(ArrayList.class); 
		return appssList ;  
	}

	public ArrayList<Application> getAllAppsList() throws Exception
	{
		
		return this.getAllResourcesList(Application.class) ; 
		
	}


	@Override
	public String getApigeeObjectType() {
		return "apps";
	}
	

}
