package com.smartvalue.apigee.configuration;

import java.util.ArrayList;
import java.util.HashMap;

public class Partner {
	private ArrayList<Customer> Customers ;
	//private HashMap<String , HashMap<String , Infra>> CustomersMap  = new HashMap<String , HashMap<String, Infra>> () ;
	private String Name ;  

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public ArrayList<Customer> getCustomers() {
		return Customers;
	}

	public void setCustomers(ArrayList<Customer> customers) {
		Customers = customers;
		
	}

	public HashMap<String, HashMap<String, Infra>> getCustomersMap() {
		HashMap<String, HashMap<String, Infra>> result = new HashMap<String, HashMap<String, Infra>>() ; 
		for (Customer cust : this.getCustomers())
		{
			
			HashMap<String, Infra> infraMap = new HashMap<String, Infra>() ; 
			for ( Infra infra : cust.getInfras())
			{
				infraMap.put(infra.getName() , infra) ; 
				
			}
			result.put(cust.getName() , infraMap ) ; 
			
		}
		
		return result;
	}

	

	
}