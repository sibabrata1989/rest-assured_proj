package test_package;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import library_package.*;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import static org.hamcrest.Matchers.*;

public class UnautorizedAPIsTest implements ConstantVariables {

	Random rndNum = new Random();
	Properties prp;
	String name;
	String payload;
	
	@BeforeClass
	public void commonService() throws IOException
	{
		prp = ReusableMethodsClass.getPropertyFile();
		name = "ModelName_"+rndNum.nextInt(1000);
		
	}
	private void preConditionSet(String host, String moduleName, String payLoadPath) throws IOException
	{
		payload = ReusableMethodsClass.generateStringFromResources(payLoadPath);
		if(payload.contains("name")){
		payload = payload.replace("##ModuleName##", moduleName);
		}
		RestAssured.baseURI= prp.getProperty(host);
	}


	@Test(priority=1)
	public void configNew()
	{
		try{
			preConditionSet("HOST",name,"./Resources/configNew.json");
		
					 given().
							  header("Content-Type","application/json").
							  body(payload).
						 
					  when().    
				              post("/configuration/new").
				              
				      then().
				      		  assertThat().statusCode(200);
					  Thread.sleep(5000);
					  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The new Configuration Failed");
		}
						 
	}
	@Test(priority=2)
	public void postFinetune1()
	{
		try
		{
			preConditionSet("HOST",name,"./Resources/finetune1.json");
		
					  given().
				  	          header("Content-Type","application/json").
				  	          body(payload).
								
					   when().
					          post("/cycle/finetune").
					          
					          then().
				      		  assertThat().statusCode(200).and().contentType(ContentType.JSON);
					  Thread.sleep(3000);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The Post fine tune with empty value is Failed");
		}
		
	}
	
	@Test(priority=3)
	public void postFinetune2()
	{
		try
		{
			preConditionSet("HOST",name,"./Resources/finetune2.json");
		
					  given().
				  	          header("Content-Type","application/json").
				  	          body(payload).
								
					   when().
					          post("/cycle/finetune").
					          
					          then().
				      		  assertThat().statusCode(200).and().contentType(ContentType.JSON);
					  Thread.sleep(3000);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The Post fine tune with values is Failed");
		}
	}
		
	@Test(priority=4)
	public void postConfigurationResume()
	{
		try{
			preConditionSet("HOST",name,"./Resources/configResume.json");
		              given().
				  	          header("Content-Type","application/json").
				  	          body(payload).
								
					   when().
					          post("/configuration/resume").
					          
					          then().
				      		  assertThat().statusCode(200).and().contentType(ContentType.JSON);
		              Thread.sleep(3000);
							  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The Configuration resume is Failed");
		}
	
	
	}
	@Test(priority=5)
	public void getGpusAvailable()
	{
		try
		{
		RestAssured.baseURI= prp.getProperty("HOST");
		
		Response res= given().
							  header("Content-Type","application/json").					
						 
					  when().    
				              get("/configuration/gpus/available").
				              			  
				      then().
				      		  extract().response();
		
							  System.out.println(res);
							  String responseString = res.asString();
							  Assert.assertTrue(responseString.contains("gensynth"), "Failed!: The rsponse host name doesnt match!");
						 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The GPUS available get call is Failed");
		}
	}
	@Test(priority=6)
	public void postFinetune1Negative()
	{
		try
		{
			preConditionSet("HOST",name,"./Resources/finetune1Negative.json");
			
					  given().
				  	          header("Content-Type","application/json").
				  	          body(payload).
								
					   when().
					          post("/cycle/finetune").
					          
					          then().
				      		  assertThat().statusCode(400).and().contentType(ContentType.JSON);
					  Thread.sleep(3000);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The Post fine tune with empty value is Failed");
		}
		
	}
	
	@Test(priority=7)
	public void postFinetune2Negative()
	{
		try
		{
			preConditionSet("HOST",name,"./Resources/finetune2Negative.json");
				
					  given().
				  	          header("Content-Type","application/json").
				  	          body(payload).
								
					   when().
					          post("/cycle/finetune").
					          
					          then().
				      		  assertThat().statusCode(400).and().contentType(ContentType.JSON);
					  Thread.sleep(3000);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The Post fine tune with values is Failed");
		}
	}
	@Test(priority=8)
	public void configTemplate()
	{
		try{
			preConditionSet("HOST",name,"./Resources/configTemplate.json");
		
					 given().
							  header("Content-Type","application/json").
							  body(payload).
						 
					  when().    
				              post("/config-templates/save").
				              
				      then().
				      		  assertThat().statusCode(200);
					  Thread.sleep(3000);
					  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The new template creation Failed");
		}
						 
	}
	@Test(priority=9)
	public void configDuplicateTemplate()
	{
		try{
			preConditionSet("HOST",name,"./Resources/configTemplate.json");
		
					 given().
							  header("Content-Type","application/json").
							  body(payload).
						 
					  when().    
				              post("/config-templates/save").
				              
				      then().
				      		  assertThat().statusCode(400);
					  Thread.sleep(3000);
					  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The new template creation Failed");
		}
				
						 
	}
	@Test(priority=10)
	public void archieveTemplate()
	{
		try{
			preConditionSet("HOST",name,"./Resources/archiveTemplate.json");
		
					 given().
							  header("Content-Type","application/json").
							  body(payload).
						 
					  when().    
				              post("/config-templates/archive").
				              
				      then().
				      		  assertThat().statusCode(200);
					  Thread.sleep(3000);
					  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The template deletion Failed");
		}
				
						 
	}
	@Test(priority=11)
	public void validateDSIPositiv()
	{
		try{
			preConditionSet("HOST",name,"./Resources/validateDSI_Positive.json");
		
					 given().
							  header("Content-Type","application/json").
							  body(payload).
						 
					  when().    
				              post("/configuration/validate-dsi").
				              
				      then().
				      		  assertThat().statusCode(200).and().
				      		  body("message", equalTo("Your Custom Dataset Interface is valid.")).and().
				      		  body("success", equalTo(true));
				      		  Thread.sleep(3000);
					  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The DSI Validation Positive- Failed");
		}
				
						 
	}
	@Test(priority=12)
	public void validateDSINegative()
	{
		try{
			preConditionSet("HOST",name,"./Resources/validateDSI_Negative.json");
		
					 given().
							  header("Content-Type","application/json").
							  body(payload).
						 
					  when().    
				              post("/configuration/validate-dsi").
				              
				      then().
				      		  assertThat().statusCode(400).and().
				      		  body("message", contains("An exception occurred in the data pipeline")).and().
				      		  body("success", equalTo(false));
				      		  Thread.sleep(3000);
					  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The DSI Validation Negative- Failed");
		}
				
						 
	}
	@Test(priority=13)
	public void validateCycleEdge()
	{
		try{
			preConditionSet("HOST",name,"./Resources/cycleEdge.json");
		
					 given().
							  header("Content-Type","application/json").
							  body(payload).
						 
					  when().    
				              post("/cycle/edge").
				              
				      then().
				      		  assertThat().statusCode(200).and();
				      		  Thread.sleep(3000);
					  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("Validate Cycle Edge is Failed!");
		}
				
						 
	}
	@Test(priority=14)
	public void loginWithoutAuth()
	{
		try{
			preConditionSet("HOST","","./Resources/login.json");
		
					 given().
							  header("Content-Type","application/json").
							  body(payload).
						 
					  when().    
				              post("/auth/token").
				              
				      then().
				      		  assertThat().statusCode(200).and();
				      		  Thread.sleep(3000);
					  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("Login Failed!");
		}
				
						 
	}
	@Test(priority=15)
	public void getInvocation()
	{
		try{
			preConditionSet("HOST","","");
		
					 given().
							  header("Content-Type","application/json").
						 
					  when().    
				              get("/configuration?invocationId=1").
				              
				      then().
				      		  assertThat().statusCode(200).and();
				      		  Thread.sleep(3000);
					  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("Get Invocation ID call failed!");
		}
				
						 
	}
	@Test(priority=16)
	public void getWorkersAvailable()
	{
		try{
			preConditionSet("HOST","","");
		
					 given().
							  header("Content-Type","application/json").
						 
					  when().    
				              get("/configuration/workers/available").
				              
				      then().
				      		  assertThat().statusCode(200).and();
				      		  Thread.sleep(3000);
					  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("Get avialble worker call failed");
		}
				
						 
	}
	@Test(priority=17)
	public void verifyConfigTemplateExists()
	{
		try{
			preConditionSet("HOST","","");
		
					 given().
							  header("Content-Type","application/json").
						 
					  when().    
				              get("/config-templates/exists?name=Belgian%20Traffic%20Signs%20Example&section=1").
				              
				      then().
				      		  assertThat().statusCode(200).and();
				      		  Thread.sleep(3000);
					  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("Verify Configuration template get call Failed!");
		}
				
						 
	}
	@Test(priority=18)
	public void verifyConfigTemplatesSection5()
	{
		try{
			preConditionSet("HOST","","");
		
					 given().
							  header("Content-Type","application/json").
							  body(payload).
						 
					  when().    
				              get("/config-templates?section=5").
				              
				      then().
				      		  assertThat().statusCode(200).and().
				      		  body("section", equalTo(5));
				      		  Thread.sleep(3000);
					  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("Verify Config template section 5 get call Failed!");
		}
				
						 
	}
	@Test(priority=19)
	public void verifyConfigTemplatesSection4()
	{
		try{
			preConditionSet("HOST","","");
		
					 given().
							  header("Content-Type","application/json").
							  body(payload).
						 
					  when().    
				              get("/config-templates?section=4").
				              
				      then().
				      		  assertThat().statusCode(200).and().
				      		  body("section", equalTo(4));
				      		  Thread.sleep(3000);
					  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("Verify Config template section 4 get call Failed!");
		}
				
						 
	}
	@Test(priority=20)
	public void verifyConfigTemplatesSection2()
	{
		try{
			preConditionSet("HOST","","");
		
					 given().
							  header("Content-Type","application/json").
							  body(payload).
						 
					  when().    
				              get("/config-templates?section=2").
				              
				      then().
				      		  assertThat().statusCode(200).and();
				      		  Thread.sleep(3000);
					  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("Verify Config template section 4 get call Failed!");
		}
				
						 
	}
	@Test(priority=21)
	public void verifyInvocationCancel()
	{
		try{
			preConditionSet("HOST","","cancelInvocation.json");
		
					 given().
							  header("Content-Type","application/json").
							  body("").
						 
					  when().    
				              post("/invocation/cancel").
				              
				      then().
				      		  assertThat().statusCode(200).and();
				      		  Thread.sleep(3000);
					  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("Verify Invocation Cancel call Failed!");
		}
				
						 
	}
}
