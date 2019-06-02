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
		payload = payload.replace("##ModuleName##", moduleName);
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
	@Test(priority=9)
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
}
