package test_package;
import org.json.simple.JSONArray;
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
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.hamcrest.Matchers.*;

public class CustomAuthAPIsTest {
	Properties prp;
	String payload;
	String userName = null;
	String teamName = null;
	JsonPath json = null;
	private String token;
	Random rnd = new Random();
	DbConnect dbc = DbConnect.getInstance();


	private void preConditionSet(String host,String payLoadPath) throws IOException
	{
		        payload = XmlUtils.generateStringFromResources(payLoadPath);
		        prp = XmlUtils.getPropertyFile();
		        RestAssured.baseURI= prp.getProperty(host);
	}

	private void validateUser(ResultSet resultSet, String userName, String status) throws SQLException {
		if(resultSet.next()) {
			String userNameDb = resultSet.getString("login_name");
			String statusDb = resultSet.getString("status");
			Assert.assertTrue(userName.equalsIgnoreCase(userNameDb), "Validating user name created in DB");
			Assert.assertTrue(status.equalsIgnoreCase(statusDb), "Validating status created in DB");
			
		} else {
			Assert.fail("login_name" + userName +  " job_status" + status + " could not be retieved from DB.");
		}
    }


	@BeforeClass
	public void commonService() throws IOException
	{
	       try{		
			preConditionSet("HOST","./Resources/authToken.json");
                        Response res= given()
			.header("Content-Type","application/json")
			.body(payload)
						 
			.when()
			.post("/auth/token")
				              
			.then()
			.assertThat().statusCode(200)
			.extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("auth"), true, "Token not generated! Failed");
			Assert.assertEquals(json.get("user.identifier"), "administrator","Identifier Mismatch!");
			token=json.get("token").toString();// Token generated
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
					
			Response res= given()
			.header("Content-Type","application/json")
			.header("Authorization","Bearer "+token)
						 
			.when()    
			.get("/user/exists?identifier=administrator")
				          
			.then()
			.assertThat().statusCode(200)
                        .extract().response();
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
			payload = XmlUtils.generateStringFromResources("./Resources/userLogin.json");
			payload = payload.replace("##Token##", token);
		
			Response res= given()
			.header("Content-Type","application/json")
			.body(payload)
						 
			.when()    
			.post("/user/login")
				        
			.then()
			.assertThat().statusCode(200)
                        .extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("user.identifier"), "administrator","Identifier Mismatch!");
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
			userName = ("NewUser"+rnd.nextInt(100)).toLowerCase();
			System.out.println("User created is" +userName);
			preConditionSet("HOST","./Resources/addNewUser.json");
			payload = payload.replace("##UserName##", userName);

			given()
			.header("Content-Type","application/json")
			.header("Authorization","Bearer "+token)
			.body(payload)
						 
			.when()   
			.post("/user/new")
				          
			.then()
			.assertThat().statusCode(200);
			Thread.sleep(10000);
			ResultSet resultSet = dbc.getDbData("Select login_name,status from users where login_name='" + userName+ "' and status= 't';");
			validateUser(resultSet,userName,"t");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The verify add new User failed!");
		}
						 
	}
	@Test(priority=4 , enabled=false)
	public void deleteNewUser()
	{
		try{
			preConditionSet("HOST","./Resources/deleteNewUser.json");
			payload = payload.replace("##UserName##", userName);

			given()
			.header("Content-Type","application/json")
			.header("Authorization","Bearer "+token)
			.body(payload)
						 
			.when()    
			.post("user/delete")
			           
			.then()
			.assertThat().statusCode(200);
			Thread.sleep(3000);
			ResultSet resultSet = dbc.getDbData("Select login_name,status from users where login_name='" + userName+ "' and status= 'f';");
			validateUser(resultSet,userName,"f");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("Verify delete User failed!");
		}
						 
	}
	@Test(priority=6 , enabled=false)
	public void updateUserRole()
	{
		try{
			preConditionSet("HOST","./Resources/updateUserRole.json");
			payload = payload.replace("##UserName##", userName);
			payload = payload.replace("##Role##", "Admin");

			Response res = given().
					header("Content-Type","application/json").
					header("Authorization","Bearer "+token).
					body(payload).

					when().
					post("/user/modify-user-role").

					then().
					assertThat().statusCode(200).
					log().all().
					extract().response();

			System.out.println(res);
//			String responseString = res.asString();
//			JsonPath json = new JsonPath(responseString);
//			Assert.assertEquals(json.get("role"), "Admin", "The role update failed!");
//			Thread.sleep(3000);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("Update user failed due to exception!");
		}
	}

	@Test(priority=7)
	public void verifyChangePassword()
	{
		try{
			preConditionSet("HOST","./Resources/changePassword.json");
			payload = payload.replace("##UserName##", userName);
			payload = payload.replace("##password##", "987654");

			Response res = given().
					header("Content-Type","application/json").
					header("Authorization","Bearer "+token).
					body(payload).

					when().
					post("/user/change-password").

					then().
					assertThat().statusCode(200).
					extract().response();
//
//			System.out.println(res);
//			String responseString = res.asString();
//			JsonPath json = new JsonPath(responseString);
//			Assert.assertEquals(json.get("newPassword"), "98765", "The change passwordfailed!");
//			Thread.sleep(3000);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("Change Password failed due to exception!");
		}

	}

	@Test(priority=8)
	public void verifyAllUsers()
	{
		
		try{

			Response res = given().
					header("Content-Type","application/json").
					header("Authorization","Bearer "+token).

					when().
					get("/users").

					then().
					assertThat().statusCode(200).
					extract().response();

			System.out.println(res);
			String responseString = res.asString();
			json = new JsonPath(responseString);
			Assert.assertEquals(json.get("identifier[0]"),"administrator", "Failed!: The Admin user doesn't exist!");
			Assert.assertEquals(json.get("identifier[1]"),userName, "Failed!: The added user doesn't exist!");
			Assert.assertEquals(json.get("role[0]"),"admin", "Failed!: The Admin role doesn't exist!");
			Assert.assertEquals(json.get("role[1]"),"scientist", "Failed!: The added user role doesn't exist!");
			Thread.sleep(3000);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("Verify All user failed due to exception!");
		}
		
	}
	//add users method.
	public void addUsers(int count)
	{
		for(int i=1;i<=count;i++)
		{
			userName = ("NewUser_"+i);
			preConditionSet("HOST","./Resources/addNewUser.json");
			payload = payload.replace("##UserName##", userName);
	
			given()
			.header("Content-Type","application/json")
			.header("Authorization","Bearer "+token)
			.body(payload)
						 
			.when()   
			.post("/user/new")
				          
			.then()
			.assertThat().statusCode(200);
			System.out.println("User "+count+"created is" +userName);
			Thread.sleep(5000);
		}
	}
	
	public void deleteTeam(String teamName, String payload)
	{
			preConditionSet("HOST",payload);
			
			given()
			.header("Content-Type","application/json")
			.header("Authorization","Bearer "+token)
			.body(payload)
						 
			.when()   
			.post("/team/delete")
				          
			.then()
			.assertThat().statusCode(200);
			System.out.println("Team deleted is" +teamName);
			Thread.sleep(10000);
		
	}
	
	@Test(priority=9)
	public void addNewTeamWithSingleUser()
	{
		//precondition..
		addUsers(3);
		
		try{
			preConditionSet("HOST","./Resources/newTeam_SingleUser.json");
			
			Response res = given()
			.header("Content-Type","application/json")
			.header("Authorization","Bearer "+token)
			.body(payload)
						 
			.when()   
			.post("/team/new")
				          
			.then()
			.assertThat().statusCode(200).body("name[0]", equalTo("Team_1"))
			
			.extract().response();
			String responseString = res.asString();
			Assert.assertTrue(responseString.contains("user1"),"The user1 in Team_1 is not found in the response!");
			Assert.assertTrue(responseString.contains("administrator"),"The administrator in Team_1 is not found in the response!");
			Thread.sleep(5000);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The verify Team Addition with single user Failed!");
		}
		finally
		{
			deleteTeam("Team_1", "./Resources/deleteteam_SingleUser.json");
		}
						 
	}
	
	@Test(priority=10)
	public void addNewTeamWithMultipleUsers()
	{
		
		try{
			preConditionSet("HOST","./Resources/newTeam_MultipleUsers.json");
			
			Response res = given()
			.header("Content-Type","application/json")
			.header("Authorization","Bearer "+token)
			.body(payload)
						 
			.when()   
			.post("/team/new")
				          
			.then()
			.assertThat().statusCode(200).body("name[0]", equalTo("Team_2"))
			
			.extract().response();
			String responseString = res.asString();
			Assert.assertTrue(responseString.contains("user1"),"The user1 in Team_2 is not found in the response!");
			Assert.assertTrue(responseString.contains("user2"),"The user2 in Team_2 is not found in the response!");
			Assert.assertTrue(responseString.contains("administrator"),"The administrator in Team_2 is not found in the response!");
			Thread.sleep(5000);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The verify team addition with multiple users Failed!");
		}
		finally
		{
			deleteTeam("Team_2", "./Resources/deleteteam_MultipleUsers.json");
		}
						 
	}
	
	@Test(priority=11)
	public void addNewTeamWithThreeUsersAndUpdateOwner()
	{
		
		try{
			preConditionSet("HOST","./Resources/newTeam_ThreeUsers.json");
			
			Response res1 = given()
			.header("Content-Type","application/json")
			.header("Authorization","Bearer "+token)
			.body(payload)
						 
			.when()   
			.post("/team/new")
				          
			.then()
			.assertThat().statusCode(200).body("name[0]", equalTo("Team_3"))
			
			.extract().response();
			String responseString = res1.asString();
			Assert.assertTrue(responseString.contains("user1"),"The user1 in Team_3 is not found in the response!");
			Assert.assertTrue(responseString.contains("user2"),"The user2 in Team_3 is not found in the response!");
			Assert.assertTrue(responseString.contains("user3"),"The user3 in Team_3 is not found in the response!");
			Assert.assertTrue(responseString.contains("administrator"),"The administrator in Team_3 is not found in the response!");
			Thread.sleep(5000);
			
			//update the role
			preConditionSet("HOST","./Resources/updateTeam3UserRoleAsOwner.json");
			Response res2 = given()
					.header("Content-Type","application/json")
					.header("Authorization","Bearer "+token)
					.body(payload)
								 
					.when()   
					.post("/team/edit")
						          
					.then()
					.assertThat().statusCode(200).body("members[0].user2.teamOwner", equalTo(true))
					
					.extract().response();
					Thread.sleep(5000);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The verify team addition with Three users and update owner Failed!");
		}
		finally
		{
			deleteTeam("Team_3", "./Resources/deleteTeam_ThreeUsers.json");
		}
						 
	}
	@Test(priority=10)
	public void verifyErrorMsgWithTeamAddition()
	{
		
		try{
			preConditionSet("HOST","./Resources/addNewTeam_WithoutAdministrator.json");
			
			Response res = given()
			.header("Content-Type","application/json")
			.header("Authorization","Bearer "+token)
			.body(payload)
						 
			.when()   
			.post("/team/new")
				          
			.then()
			.assertThat().statusCode(400)
			
			.extract().response();
			String responseString = res.asString();
			json = new JsonPath(responseString);
			Assert.assertEquals(json.get("message"),"You must be an owner of the team that you're creating","Failed:User is allowed to create a Team and allocated as Owner without Login!");
			Thread.sleep(5000);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.fail("The verify error message for team addition is failed!");
		}
						 
	}

}

