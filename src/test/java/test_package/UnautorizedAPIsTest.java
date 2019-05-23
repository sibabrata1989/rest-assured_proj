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
			 RestAssured.baseURI= prp.getProperty("HOST");
		
					 given().
							  header("Content-Type","application/json").
							  body(payload.configNew(name)).
						 
					  when().    
				              post("/configuration/new").
				              
				      then().
				      		  assertThat().statusCode(200);
					  Thread.sleep(5000);
					  
		}
		catch(Exception e)
		{
			Assert.fail("The new Configuration Failed");
		}
						 
	}
	@Test(priority=2)
	public void postFinetune1()
	{
		try
		{
		
		RestAssured.baseURI=prp.getProperty("HOST");
		
					  given().
				  	          header("Content-Type","application/json").
				  	          body(payload.finetunePostBodyPayload1(name)).
								
					   when().
					          post("/cycle/finetune").
					          
					          then().
				      		  assertThat().statusCode(200).and().contentType(ContentType.JSON);
					  Thread.sleep(3000);
		}
		catch(Exception e)
		{
			Assert.fail("The Post fine tune with empty value is Failed");
		}
		
	}
	
	@Test(priority=3)
	public void postFinetune2()
	{
		try
		{

		RestAssured.baseURI=prp.getProperty("HOST");
		
					  given().
				  	          header("Content-Type","application/json").
				  	          body(payload.finetunePostPayload2(name)).
								
					   when().
					          post("/cycle/finetune").
					          
					          then().
				      		  assertThat().statusCode(200).and().contentType(ContentType.JSON);
					  Thread.sleep(3000);
		}
		catch(Exception e)
		{
			Assert.fail("The Post fine tune with values is Failed");
		}
	}
		
	@Test(priority=4)
	public void postConfigurationResume()
	{
		try{


		RestAssured.baseURI=prp.getProperty("HOST");
		
		              given().
				  	          header("Content-Type","application/json").
				  	          body(payload.configResumePostPayload(name)).
								
					   when().
					          post("/configuration/resume").
					          
					          then().
				      		  assertThat().statusCode(200).and().contentType(ContentType.JSON);
		              Thread.sleep(3000);
							  
		}
		catch(Exception e)
		{
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
	
							  String responseString = res.asString();
							  Assert.assertTrue(responseString.contains("gensynth"), "Failed!: The rsponse host name doesnt match!");
						 
		}
		catch(Exception e)
		{
			Assert.fail("The GPUS available get call is Failed");
		}
	}
}
