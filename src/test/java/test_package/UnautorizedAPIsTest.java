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
	

	@Test(priority=1)
	public void configNew()
	{
		try{
			 payload = ReusableMethodsClass.generateStringFromResources("./Resources/configNew.json");
			 payload = payload.replace("##ModuleName##", ""+name+"");
			 RestAssured.baseURI= prp.getProperty("HOST");
		
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
		payload = ReusableMethodsClass.generateStringFromResources("./Resources/finetune1.json");
		payload = payload.replace("##ModuleName##", ""+name+"");
		RestAssured.baseURI=prp.getProperty("HOST");
		
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
		payload = ReusableMethodsClass.generateStringFromResources("./Resources/finetune2.json");
		payload = payload.replace("##ModuleName##", ""+name+"");
		RestAssured.baseURI=prp.getProperty("HOST");
		
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

		payload = ReusableMethodsClass.generateStringFromResources("./Resources/configResume.json");
		
		RestAssured.baseURI=prp.getProperty("HOST");
		
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
		payload = ReusableMethodsClass.generateStringFromResources("./Resources/finetune1.json");
		payload = payload.replace("##ModuleName##", ""+name+"");
		payload = payload.replaceAll("/d", "-1");
		RestAssured.baseURI=prp.getProperty("HOST");
		
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
		payload = ReusableMethodsClass.generateStringFromResources("./Resources/finetune2.json");
		payload = payload.replace("##ModuleName##", ""+name+"");
		payload = payload.replaceAll("/d", "-100");
		RestAssured.baseURI=prp.getProperty("HOST");
		
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
}
