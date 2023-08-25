package com.smartvalue.apigee.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;


import com.google.gson.Gson;
import com.smartvalue.apigee.configuration.infra.Infra;


public class ApigeeConfig {
	
	private ArrayList<Partner> Partners ;
	
	private HashMap<String , HashMap<String , HashMap<String , Infra> > > partnersMap = new HashMap<> () ; 

	public ApigeeConfig (String  m_apigeeConfigFilePath) throws FileNotFoundException , IOException
	{
		File configFile = new File(m_apigeeConfigFilePath) ; //("E:\\MasterWorks\\Eclipse-WS\\ResourceManager\\config.json") ;
		FileInputStream inputStream = new FileInputStream(configFile);
		String fileContent = readFromInputStream(inputStream) ; 
		Gson gson = new Gson();
		ApigeeConfig result = null; 
		result = gson.fromJson(fileContent, (Type) ApigeeConfig.class);
	
		this.setPartners(result.getPartners()) ; 
 
		for ( Partner partner : this.Partners  )
		{
			this.partnersMap.put (partner.getName()  , partner.getCustomersMap()) ; 
		}
		
	}
	
	private ArrayList<Partner> getPartners() {
		return Partners;
	}
	public void setPartners(ArrayList<Partner> partners) {
		this.Partners = partners;
	}

		
	private HashMap<String, HashMap<String, Infra>> getPartner(String m_partnerName) throws Exception 
	{
		HashMap<String, HashMap<String, Infra>> partner =  this.getPartnersMap().get(m_partnerName);
		if (partner == null)
		{
			throw new Exception ("Partner " + m_partnerName + " Not Found") ; 
		}
		return partner ; 
	}
	
	private HashMap<String, Infra> getCustomer(String m_partnerName , String m_customerName) throws Exception {
		HashMap<String, HashMap<String, Infra>> Partner =  this.getPartner( m_partnerName);
		HashMap<String, Infra> customer =  Partner.get(m_customerName);
		if (customer == null)
		{
			throw new Exception ("Customer " + m_customerName + " Not Found For Partner " + m_partnerName ) ;
		}
		return customer ; 
	}

	public Infra getInfra( String m_partnerName , String m_customerName , String m_infraName) throws Exception {
		HashMap<String, Infra> customer =  this.getCustomer( m_partnerName ,  m_customerName);
		Infra infra = customer.get(m_infraName) ; 
		if (infra == null )
		{
			throw new Exception ("Infra " + m_infraName  + " Not Found For Customer " + m_customerName + " and Partner " + m_partnerName ) ;

		}
		return infra ; 
	}

	private static String readFromInputStream(InputStream inputStream)
			  throws IOException {
			    StringBuilder resultStringBuilder = new StringBuilder();
			    try (BufferedReader br
			      = new BufferedReader(new InputStreamReader(inputStream))) {
			        String line;
			        while ((line = br.readLine()) != null) {
			            resultStringBuilder.append(line).append("\n");
			        }
			    }
			  return resultStringBuilder.toString();
			}

	public HashMap<String , HashMap<String , HashMap<String , Infra> > > getPartnersMap() {
		return partnersMap;
	}
	

}
