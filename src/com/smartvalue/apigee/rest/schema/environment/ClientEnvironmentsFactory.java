package com.smartvalue.apigee.rest.schema.environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import com.smartvalue.moj.clients.environments.Environments;
import com.smartvalue.moj.clients.environments.JsonParser;

public class ClientEnvironmentsFactory {

	public static Environments create(InputStream inputStream) throws FileNotFoundException, IOException
	{
		Type envsType = (Type) Environments.class ; 
		JsonParser envParser = new JsonParser( envsType ) ;
		Environments clientEnvs = (Environments) envParser.getObject(inputStream) ;
		return clientEnvs;
	}
	
	public static Environments create(File file) throws FileNotFoundException, IOException
	{
		Type envsType = (Type) Environments.class ; 
		JsonParser envParser = new JsonParser( envsType ) ;
		Environments clientEnvs = (Environments) envParser.getObject(file) ;
		return clientEnvs;
	}
	
	public static Environments create(String filePath) throws FileNotFoundException, IOException
	{
		Type envsType = (Type) Environments.class ; 
		JsonParser envParser = new JsonParser( envsType ) ;
		Environments clientEnvs = (Environments) envParser.getObject(filePath) ;
		return clientEnvs;
	} 
	
	
}
