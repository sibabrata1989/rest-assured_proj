package test_package;
import static org.hamcrest.Matchers.equalTo;
import org.testng.annotations.BeforeClass;
import library_package.*;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.util.Properties;

public class UnautorizedAPIsTest implements ConstantVariables {

	Properties prp;
	
	@BeforeClass
	public void commonService() throws IOException
	{
		prp = ReusableMethodsClass.getPropertyFile();
	}
	
	@Test
	public void getGpusAvailable()
	{
		
		RestAssured.baseURI= prp.getProperty("HOST");
		
		Response res= given().
							  header("Content-Type","application/json").
							
						 
					  when().    
				              get("/configuration/gpus/available").
				              
				  
				      then().extract().response();
	
							 JsonPath js= ReusableMethodsClass.Raw_to_Json(res);
						 
	}
	
	@Test
	public void getInvocationCancel()
	{
		
		RestAssured.baseURI= prp.getProperty("HOST");
		
		Response res= given().
							  header("Content-Type","application/json").
							  body("{\"invocationId\":10}").
						 
					  when().    
				              post("/invocation/cancel").
				              
				  
				      then().extract().response();
	
							 JsonPath js= ReusableMethodsClass.Raw_to_Json(res);
						 
	}
	@Test
	public void postFinetune()
	{
		
		RestAssured.baseURI=prp.getProperty("HOST");
		
		Response res= given().
				  	          header("Content-Type","application/json").
				  	          body(payload.finetunePostPayload("name")).
								
					   when().
					          post("/cycle/finetune").
					          
					   then().extract().response();
	
							  JsonPath js= ReusableMethodsClass.Raw_to_Json(res);
							  System.out.println("Below is the response validation");
							  //System.out.println(js.get("text"));
							  //System.out.println(js.get("id"));
							 // id=js.get("id").toString();
	
	
	}
	
	@Test
	public void postFinetune2()
	{
		
		RestAssured.baseURI=prp.getProperty("HOST");
		
		Response res= given().
				  	          header("Content-Type","application/json").
				  	          body(payload.finetunePostBodyPayload2()).
								
					   when().
					          post("/cycle/finetune").
					          
					   then().extract().response();
	
							  JsonPath js= ReusableMethodsClass.Raw_to_Json(res);
							  System.out.println("Below is the response validation");
							  //System.out.println(js.get("text"));
							  //System.out.println(js.get("id"));
							 // id=js.get("id").toString();
	
	
	}
	@Test
	public void postConfigurationResume()
	{
		
		RestAssured.baseURI=prp.getProperty("HOST");
		
		Response res= given().
				  	          header("Content-Type","application/json").
				  	          body(payload.configResumePostPayload("name")).
								
					   when().
					          post("/configuration/resume").
					          
					   then().extract().response();
	
							  JsonPath js= ReusableMethodsClass.Raw_to_Json(res);
							  System.out.println("Below is the response validation");
							  //System.out.println(js.get("text"));
							  //System.out.println(js.get("id"));
							 // id=js.get("id").toString();
	
	
	}
}
