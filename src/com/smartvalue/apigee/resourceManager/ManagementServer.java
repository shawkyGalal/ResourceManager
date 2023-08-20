package com.smartvalue.apigee.resourceManager;

import java.io.IOException;
import java.lang.reflect.Type;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.security.crypto.codec.Base64;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.smartvalue.apigee.configuration.infra.Infra;
import com.smartvalue.apigee.resourceManager.helpers.Helper;
import com.smartvalue.apigee.rest.schema.ApigeeAccessToken;
import com.smartvalue.apigee.rest.schema.organization.Organization;
import com.smartvalue.apigee.rest.schema.product.ProductsServices;

import com.smartvalue.apigee.rest.schema.server.Server  ;
import com.smartvalue.apigee.rest.schema.server.ServerServices;


public class ManagementServer extends Server{
	
	MyServerProfile serverProfile ;  

	public ManagementServer(Infra m_infra  ) throws UnirestException 
	{
		MyServerProfile m_serverProfile = mapConfigFileToServerProfile(m_infra  ) ;
		serverProfile = m_serverProfile; 
		if (serverProfile.getAuthType().equalsIgnoreCase("OAuth") ) 
		{
			ApigeeAccessToken at = this.getAccess_token() ;
			this.serverProfile.setBearerToken(at.getAccess_token()) ;
			this.serverProfile.setRefreshToken(at.getRefresh_token()) ;
			
		}
	}
	
	private MyServerProfile mapConfigFileToServerProfile( Infra m_infra) {
		MyServerProfile result = new MyServerProfile() ;
		result.setAuthType(m_infra.getAuthType());
		result.setCredential_user(m_infra.getSysadminCred().getUsername());
		result.setCredential_pwd(m_infra.getSysadminCred().getPassword());
		result.setClientId(m_infra.getSysadminCred().getClientId());
		result.setClientSecret(m_infra.getSysadminCred().getClientSecret());
		
		result.setTokenUrl(m_infra.getTokenUrl());
		result.setHostUrl(m_infra.getMgmServerUrl());
		result.setOauthHostURL(m_infra.getOauthMgmServerUrl());
		
		result.setConnectionTimeout(m_infra.getConnectionTimeout());
		result.setSocketTimeout(m_infra.getSocketTimeout());
		
		return result;
	}

	
	public ManagementServer(MyServerProfile m_serverProfile ) 
	{	
		serverProfile = m_serverProfile; 
	}
   
	public HashMap <String , Organization>  getOrgs() throws UnirestException, IOException {
		
		String apiPath = "/v1/o/" ; 
		String[] orgNames  = this.executeGetMgmntAPI(apiPath , String[].class) ; 
		HashMap <String , Organization> result = new HashMap <String , Organization> () ; 
		for (String orgname : orgNames )
		{
			result.put(orgname  , new Organization(this , orgname)) ; 
		}
		return result ; 
	}

	private String  getAuthorizationHeader()
	{
		String authorization = null ; 
		if (this.serverProfile.getAuthType().equalsIgnoreCase("Basic"))
		{
			authorization = "Basic "+ new String(Base64.encode((this.serverProfile.getCredential_user() + ":" + this.serverProfile.getCredential_pwd()).getBytes()), Charset.forName("UTF-8")) ; 
		}
		else if (this.serverProfile.getAuthType().equalsIgnoreCase("OAuth"))
		{
			String accessToken  = this.serverProfile.getBearerToken() ;
			 authorization = "Bearer "+ accessToken ; 
		}
		return authorization ; 
	}
	
	private String getHostUrl()
	{
		String hostUrl ; 
		if (this.serverProfile.getAuthType().equalsIgnoreCase("Basic"))
		{hostUrl = this.serverProfile.getHostUrl() ;}
		else {hostUrl = this.serverProfile.getOauthHostURL() ; }
		return hostUrl ; 
	}
	public HttpResponse<String> getPostHttpResponse(String m_apiPath , String m_body , String m_contentType ) throws UnirestException, IOException  {
		Unirest.setTimeouts(this.serverProfile.getConnectionTimeout(), this.serverProfile.getSocketTimeout());
		String hostUrl = getHostUrl () ; 
		String authorization = getAuthorizationHeader() ; 
		HttpResponse<String> response = Unirest.post(hostUrl + m_apiPath)
				.header("Authorization", authorization )
				.header("Content-Type", m_contentType )
				.body(m_body).asString();
		return response ;  
	}
	public HttpResponse<String> getGetHttpResponse(String m_apiPath ) throws UnirestException, IOException  {
		Unirest.setTimeouts(this.serverProfile.getConnectionTimeout(), this.serverProfile.getSocketTimeout());
		String hostUrl = getHostUrl() ;
		String authorization = getAuthorizationHeader() ; 
		HttpResponse<String> response  = Unirest.get(hostUrl + m_apiPath).header("Authorization", authorization ).asString();
		return response ;
		
	}
	
	public HttpResponse<String> getDeleteHttpResponse(String m_apiPath ) throws UnirestException, IOException  {
		Unirest.setTimeouts(this.serverProfile.getConnectionTimeout(), this.serverProfile.getSocketTimeout());
		String hostUrl = getHostUrl() ;
		String authorization = getAuthorizationHeader() ; 
		HttpResponse<String> response  = Unirest.delete(hostUrl + m_apiPath).header("Authorization", authorization ).asString();
		return response ;
	}
	
	public HttpResponse<String> getPutHttpResponse(String m_apiPath ) throws UnirestException, IOException  {
		Unirest.setTimeouts(this.serverProfile.getConnectionTimeout(), this.serverProfile.getSocketTimeout());
		String hostUrl = getHostUrl() ;
		String authorization = getAuthorizationHeader() ; 
		HttpResponse<String> response  = Unirest.put(hostUrl + m_apiPath).header("Authorization", authorization ).asString();
		return response ;
	}
	
private <T> T GsonClassMapper(HttpResponse<String> response ,  Class<T> classOfT  ) throws UnirestException 
{
	T result = null ; 
	if (Helper.isConsideredSuccess(response.getStatus()) )   
	{
		Gson gson = new Gson();
		result = gson.fromJson(response.getBody(),  classOfT);
	} 
	else {
		throw new UnirestException ( response.getBody()) ; 
	}
	return result ; // Primitives.wrap(classOfT).cast(result);
}
	
	public <T> T executeGetMgmntAPI(String m_apiPath ,  Class<T> classOfT  ) throws UnirestException, IOException
	{
		T result = null ; 
		HttpResponse<String> response =  this.getGetHttpResponse(m_apiPath) ;
		result = GsonClassMapper(response , classOfT  ) ; 
		return result ; // Primitives.wrap(classOfT).cast(result);
	}
	
	public <T> T executePostMgmntAPI(String m_apiPath ,  Class<T> classOfT , String m_body , String m_contentType ) throws UnirestException, IOException
	{
		T result = null ; 
		HttpResponse<String> response =  this.getPostHttpResponse(m_apiPath , m_body , m_contentType) ;
		result = GsonClassMapper(response , classOfT  ) ; 
		return result ; // Primitives.wrap(classOfT).cast(result);
	} 
	
	public <T> T executeMgmntAPI(String m_apiPath, Type listType) throws UnirestException, IOException 
	{
		T result = null ; 
		HttpResponse<String> response = this.getGetHttpResponse(m_apiPath) ;
		if (Helper.isConsideredSuccess(response.getStatus()) )   
		{
			
			Gson gson = new Gson();
			result = gson.fromJson(response.getBody(),  listType);
		} 
		else {
			throw new UnirestException ( response.getBody()) ; 
		}
		
		return result; //Primitives.wrap(listType).cast(result);
	}
	

	
	
	/*		
	public <T> T executeMgmntAPIUsingJaxJson(String m_apiPath , Class<T> classOfT ,  String m_verb ) throws UnirestException, IOException
	{
		T result = null ; 
		HttpResponse<String> response = this.getApiHttpResponse(m_apiPath, m_verb) ; 
		
		if (Helper.isConsideredSuccess(response.getStatus()) )  
		{
			 result = JavaxJson.fromJson(response.getBody(), classOfT);
		} 
		else {
			throw new UnirestException ( response.getBody() + "\n Response Status Code = " + response.getStatus() ) ; 
		}
		return result ; // Primitives.wrap(classOfT).cast(result);
		
	}
	*/
	
	
	
	public ApigeeAccessToken getAccess_token() throws UnirestException
	{
		Unirest.setTimeouts(0, 0);
		HttpResponse<String> response = Unirest.post(this.serverProfile.getTokenUrl())
		  .header("Content-Type", "application/x-www-form-urlencoded")
		  .header("grant_type", "client_credentials")
		  .header("Authorization", "Basic "+ new String(Base64.encode((this.serverProfile.getClientId() + ":" + this.serverProfile.getClientSecret()).getBytes()), Charset.forName("UTF-8")))
		  .field("grant_type", "client_credentials")
		  .asString();
		ApigeeAccessToken token = null ; 
		if (Helper.isConsideredSuccess(response.getStatus()) )   
		{
			token = new ApigeeAccessToken();
			Gson gson = new Gson();
			token = gson.fromJson(response.getBody(), ApigeeAccessToken.class);
		} 
		else {
			throw new UnirestException ( response.getBody()) ; 
		}
		return token ; 
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String>  getTargetServersList01(String m_org , String m_env ) throws UnirestException, IOException
	{
		ArrayList<String> targetServersNames = null; 
		String apiPath = "/v1/o/"+m_org+"/e/"+m_env+"/targetservers?expand=true" ; 
		targetServersNames = this.executeGetMgmntAPI(apiPath , ArrayList.class ) ; 
		return targetServersNames ; 
	}
	
	
	
	
	
	public ProductsServices getProductServices()
	{
		ProductsServices srv = new ProductsServices() ; 
		srv.setMs(this);
		return srv;
		
	}

	public ServerServices getServerServices()
	{
		ServerServices srv = new ServerServices() ; 
		srv.setMs(this);
		return srv;
		
	}
}
