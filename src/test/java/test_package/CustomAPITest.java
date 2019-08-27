package test_package;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import library_package.*;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import static org.hamcrest.Matchers.*;

public class CustomAPITest implements ConstantVariables {

	Properties prp;
	String payload;
	private String token;
	
	private void preConditionSet(String host,String payLoadPath) throws IOException
	{
		payload = ReusableMethodsClass.generateStringFromResources(payLoadPath);
		prp = ReusableMethodsClass.getPropertyFile();
		RestAssured.baseURI= prp.getProperty(host);
	}
	
	@BeforeClass
	public void commonService() throws IOException
	{
		prp = ReusableMethodsClass.getPropertyFile();
		try{		
			preConditionSet("HOST","./Resources/CustomAPIPayload/authToken.json");
		Response res= given().
							  header("Content-Type","application/json").
							  body(payload).
						 
					  when().    
				              post("/auth/token").
				              
				      then().extract().response();
				          	
					  JsonPath js= ReusableMethodsClass.Raw_to_Json(res);
					  Assert.assertEquals(js.get("auth"), true, "Token not generated! Failed");
					  Assert.assertEquals(js.get("identifier"), "administrator", "Identifier Mismatch!");
					  token=js.get("token").toString();// Token generated
					  Thread.sleep(3000);
					  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("Token generate precondition failed!");
		}
		
	}
	
	@Test(priority=1)
	public void verifyUserExist()
	{
		try{
			RestAssured.baseURI= prp.getProperty("HOST");
			payload = ReusableMethodsClass.generateStringFromResources("./Resources/CustomAPIPayload/userExist.json");
			payload = payload.replace("##Token##", token);
		
			Response res= given().
							  header("Content-Type","application/json").
							  body(payload).
						 
					  when().    
				              get("/user/exists?identifier=administrator").
				              
				      then().
				      		  assertThat().statusCode(200).
		      		  
				      extract().response();

					  System.out.println(res);
					  String responseString = res.asString();
					  Assert.assertTrue(responseString.contains("true"), "Failed!: The response of user exist doesn't return true!");
					  Thread.sleep(3000);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The verify User Exist failed!");
		}
						 
	}
	@Test(priority=2)
	public void userLogin()
	{
		try{
			RestAssured.baseURI= prp.getProperty("HOST");
			payload = ReusableMethodsClass.generateStringFromResources("./Resources/CustomAPIPayload/userExist.json");
			payload = payload.replace("##Token##", token);
		
			Response res= given().
							  header("Content-Type","application/json").
							  body(payload).
						 
					  when().    
				              post("/user/login").
				              
				      then().
				      		  assertThat().statusCode(200).
		      		  
				      extract().response();

					  System.out.println(res);
					  String responseString = res.asString();
					  Assert.assertTrue(responseString.contains("true"), "Failed!: The response of user Login doesn't return true!");
					  Thread.sleep(3000);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The verify User Login failed!");
		}
						 
	}
	
	@Test(priority=3)
	public void addNewUser()
	{
		try{
			preConditionSet("HOST","./Resources/CustomAPIPayload/addNewUser.json");
		
					  given().
							  header("Content-Type","application/json").
							  body(payload).
						 
					  when().    
				              post("/user/new").
				              
				      then().
				      		  assertThat().statusCode(200);
					  Thread.sleep(3000);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The verify add new User failed!");
		}
						 
	}
	@Test(priority=4)
	public void deleteNewUser()
	{
		try{
			preConditionSet("HOST","./Resources/CustomAPIPayload/deleteNewUser.json");
		
					  given().
							  header("Content-Type","application/json").
							  body(payload).
						 
					  when().    
				              post("user/delete").
				              
				      then().
				      		  assertThat().statusCode(200);
					  Thread.sleep(3000);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("Verify delete User failed!");
		}
						 
	}
	
	
}
