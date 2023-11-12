package com.smartvalue.apigee.resourceManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.smartvalue.apigee.configuration.ApigeeConfig;
import com.smartvalue.apigee.configuration.filteredList.FilteredList;
import com.smartvalue.apigee.configuration.infra.Infra;
import com.smartvalue.apigee.configuration.infra.ManagementServer;
import com.smartvalue.apigee.environmentsMonitor.CondActionPair;
import com.smartvalue.apigee.environmentsMonitor.EnvironmentAction;
import com.smartvalue.apigee.environmentsMonitor.EnvironmentCondition;
import com.smartvalue.apigee.rest.schema.environment.Environment;
import com.smartvalue.apigee.rest.schema.organization.Organization;
import com.smartvalue.apigee.rest.schema.product.ProductsServices;
import com.smartvalue.apigee.rest.schema.proxy.Proxy;
import com.smartvalue.apigee.rest.schema.server.MPServer;
import com.smartvalue.apigee.rest.schema.server.Postgres;
import com.smartvalue.apigee.rest.schema.server.QupidServer;
import com.smartvalue.apigee.rest.schema.server.Router;
import com.smartvalue.apigee.rest.schema.server.Server;
import com.smartvalue.apigee.rest.schema.server.ServerServices;
import com.smartvalue.apigee.rest.schema.sharedFlow.SharedFlow;
import com.smartvalue.apigee.rest.schema.TargetServer;
import com.smartvalue.apigee.rest.schema.virtualHost.VirtualHost;
import com.smartvalue.moj.clients.environments.ClientEnvironmentsFactory;
import com.smartvalue.moj.clients.environments.Environments;
import com.smartvalue.moj.clients.environments.JsonParser;
import com.smartvalue.openapi.SDKGeneratoer;
import com.smartvalue.zip.ZipUtility;



public class Tester {

	public static void main (String[] args) throws Exception
	{
		
		String specsUrl = "https://api.moj.gov.local/v1/najiz-services/portal/openapi.json" ;   // "https://raw.githubusercontent.com/openapitools/openapi-generator/master/modules/openapi-generator/src/test/resources/3_0/petstore.json" ;
		String lang = "php"; 
		String outputDirectory = "C:\\Users\\Shawky Foda\\Downloads\\MOJ_SDK_"+lang ;
		TestSDKGenerator(specsUrl , lang , outputDirectory); 
		File outFile = new File(outputDirectory) ; 
		List<File> fileList = new ArrayList<File>() ; 
		fileList.add(outFile); 
		ZipUtility.zip(fileList, outputDirectory+".zip");
		
		Environments clientEnvs = ClientEnvironmentsFactory.create("moj-enviropnments.json") ; 
		com.smartvalue.moj.clients.environments.Environment e  =clientEnvs.getEnvByName("testing") ;
		String authURL = e.getUrlBuilder()
				.withForthAuth(false)
				.withResponseType("code")
				.withScope("openid")
				.buildAuthorizationURL();
		
		
		//e.executeRequest("/test01", null, "GET", "") ; 
		JsonParser apigeeConfigParser = new JsonParser( ) ;
		ApigeeConfig ac = apigeeConfigParser.getObject("config.json" , ApigeeConfig.class) ; 
		//ApigeeConfig ac  = ApigeeConfigFactory.create("config.json" , ApigeeConfig.class) ; 

		Infra infra = ac.getInfra("MasterWorks" , "MOJ" , "Prod") ;
		String orgName = "stg" ; 
		String envName = "iam-protected" ; 
		String proxyName = "oidc-core" ;
		String region = "dc-1" ; 
		
		 //Infra infra = ac.getInfra("SmartValue" , "Demo" , "Prod") ; 
		 //String orgName =  "smart-value"  ; // "stg" ; 
		 //String envName = "prod"  ; // "iam-protected"
		 //String proxyName = "DZIT" ;
		 //String region = "dc-1" ; 
		
		ManagementServer ms = infra.getManagementServer(region) ; 
		Organization org = (Organization) ms.getOrgs().get(orgName) ;  
		Environment env = (Environment) org.getEnvs().get(envName);
		//Environment env02 = (Environment) org.getEnvs().get("cert-protected");
		env.startMonitoring(2);
		//env02.startMonitoring(2);
		
		
		
		ProductsServices   productServices = ms.getProductServices(orgName) ; 
		ArrayList<Object>  productsWithoutProxies  =productServices.getProductsWithoutProxies() ;  
		System.out.println(productsWithoutProxies); 
		
		Proxy proxy = org.getProxy(proxyName); 
		HashMap<String, ArrayList<String>> xx= proxy.getRevisionsUsesTargetServer("Yesser_Server" , true) ; 
		System.out.println(xx);
		
		HashMap<String, Proxy> allProxies = org.getAllProxies();
		Renderer.hashMaptoHtmlTable(allProxies); 
		
		HashMap<String, Object> proxies = org.getAllProxiesUsesTargetServer("Yesser_Server" , true);
		System.out.println(proxies);
		String[] policesNames = {"FC-ELK-Logger" ,  "ELK-Logger" ,  "FC-Elk-Logger" } ; 
		ms.getProxyServices(orgName).getProxiesWithoutPolices(policesNames, true) ; 
		HashMap<String , TargetServer> allTargetServers = env.getTargetServers();  
		System.out.println(allTargetServers);
		
		ArrayList<String> proxiesNotDeployed = org.getUndeployedProxies() ; 
		System.out.println(proxiesNotDeployed.toString());
 		
		
		ms.getFreeMps("dc-2") ; 
		List<MPServer> envMpServers = env.getMessageProcesors(region) ;
		Renderer.arrayListToHtmlTable(envMpServers) ; 

		MPServer mp = ((MPServer)envMpServers.get(0)) ;
		String[] commands = {"pwd" , "ls -ltr"} ; 
		String privateKeyPath = "C:\\Users\\sfoda\\.ssh\\id_rsa"; //"G:\\My Drive\\SmartValue\\.ssh\\ShawkyFoda\\id_rsa";
		mp.executeShell(privateKeyPath , commands) ; 
		HashMap<String , ArrayList<String>> aa = mp.getAssociatedEnvs(region) ; 
		
		MPServer mp1 = ((MPServer)envMpServers.get(1)) ;
		HashMap<String , ArrayList<String>> bb = mp1.getAssociatedEnvs(region) ; 

		
		
		boolean healthy = mp.healthCheck() ;
		ArrayList<String> result = mp.removeFromEnvironmnt(env ) ; 
		Renderer.arrayListToHtmlTable(result) ;
		
		result = mp.addToEnvironmnt( env ) ; 
		Renderer.arrayListToHtmlTable(result) ;
		
		result = env.removeMessageProcessor(mp) ; 
		result = env.addMessageProcessor(mp) ;
		
		org.getAllApps() ; 
		ServerServices ss = ms.getServerServices() ;
		ss.getServers("gateway", region) ; 
		List<Server>  gatewayServers = ss.getServers("gateway" , region ) ;
		List<Server>  analyticsServers = ss.getServers("analytics" , region ) ;
		List<Server>  centralServers = ss.getServers("central" , region ) ;
		
		FilteredList<MPServer> regionAllMpServers = ss.getMPServers(region); 
		FilteredList<Router> routerServers =  ss.getRouterServers(region);
		FilteredList<Postgres> potgresServers = ss.getPostgresServers(region); 
		FilteredList<QupidServer> qupidServers =  ss.getQupidServers(region) ;
		FilteredList<ManagementServer> mgmServer = ss.getManagementServers(region)  ; 
		List<MPServer> dcMPs= env.getMessageProcesors(region);
		List<MPServer> drMPs= env.getMessageProcesors("dc-2");
		
		ss.getAllEnvsMessageProcessors(orgName); 
		
		ss.getOnlyUpMpServers(region); 
		
		System.out.println(gatewayServers);
	
		String[] allVirtuslHosts = env.getAllVirtualHosts() ; 
			
		System.out.println(allVirtuslHosts.toString());
		
		VirtualHost vh = env.getVirtualHostByName(allVirtuslHosts[0]) ;
		HttpResponse<String> resultxx = vh.executeGetRequest("/test01" , null,  null) ; 
		System.out.println(resultxx.toString());
		
		String[] allShardFlows = org.getAllShardFlow() ;
		SharedFlow shardFlow = org.getShardFlow(allShardFlows[1]) ; 
		System.out.println(shardFlow.toString());
		
		//-- Testing Environment Monitoring Framework -- 
		ArrayList<CondActionPair> condActionPairs = new ArrayList<>() ;
		EnvironmentCondition ec = new EnvironmentCondition(env) 
			{ 	@Override
				public boolean evaluate() throws Exception  {
					boolean result  = true ; 
					ManagementServer ms = this.getEnv().getMs(); 
					ArrayList<String> regions = ms.getRegions(); 
					for (String region : regions)
					{	List<MPServer> mpservers = this.getEnv().getMessageProcesors(region);
						for (MPServer mpserver : mpservers )
						{
							result = result  && mpserver.healthCheck(); 
							if (! result) 
							{
								break ; 
							}
						}
					}
					return result ; 
				}
			};
		
		EnvironmentAction ea = new EnvironmentAction(env) 
			{	@Override
				public void run() throws UnirestException, IOException {
					// TODO Auto-generated method stub
					// Add one of the free mp's to this env. 
					Environment env = this.getEnv() ;
					ArrayList<MPServer> freeMps = env.getMs().getFreeMps(region) ;
					env.addMessageProcessor(freeMps.get(0)) ; 
				}
			} ; 
		 
		CondActionPair cp = new CondActionPair() ; 
		cp.setAction(ea); cp.setCondition(ec); 
		condActionPairs.add(cp);
		env.monitor(condActionPairs);
		
		
		
	
	} 

	public static void TestSDKGenerator(String specsUrl , String lang , String outputDirectory ) {
		SDKGeneratoer sdkg = new SDKGeneratoer() ;
		sdkg.setLang(lang);
				
		// "https://raw.githubusercontent.com/openapitools/openapi-generator/master/modules/openapi-generator/src/test/resources/3_0/petstore.json" ;  
		sdkg.setOutputDir(outputDirectory ) ; 
		sdkg.setPackageName("org.moj.najiz.sdk");
		sdkg.setValidateSpecs(false); 
		// "https://api.moj.gov.local/realestateidentityid/openapi.json" ; //
		  
		sdkg.generateSDK(specsUrl);
	}
	
		
}