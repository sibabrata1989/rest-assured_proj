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
import java.nio.file.Files;
import java.nio.file.Paths;
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


    private void preConditionSet(String host, String payLoadPath) throws IOException {
        payload = ReusableMethodsClass.generateStringFromResources(payLoadPath);
        prp = ReusableMethodsClass.getPropertyFile();
        RestAssured.baseURI = prp.getProperty(host);
    }


    @BeforeClass
    public void commonService() throws IOException {
        try {
            preConditionSet("HOST", "./smart_rec/token_payload.json");
            Response res = given()
                    .header("Content-Type", "application/json")
                    .body(payload)

                    .when()
                    .post("/oauth/token")

                    .then()
                    .assertThat().statusCode(200)
                    .extract().response();
            String responseString = res.asString();
            JsonPath js = new JsonPath(responseString);
            token = js.get("access_token");
            System.out.println("token is :" + token);
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Token generate precondition failed!");
        }
    }

    @Test(priority = 1)
    public void CheckForCompanyDetails() {

        try {

            preConditionSet("HOST", "./Resources/CareerBuilder/searchCompany.json");
            Response res = given()
                    .header("Content-Type", "application/json")
                    /*.header("cid", "C1d1267dd")
                    .header("sig", "ah0VM4eYxdedyWaCvu8fWSBwcnWWFAoMtds2uWXOvlmggu72nze+Ks2tCVBcTA0VrIVw4aOO1rCiIhhKSOvA5Q==")*/
                    .header("Authorization", token)
                    .body(payload)

                    .when()
                    .post("/core/smartrecognition/company/search")

                    .then()
                    .assertThat().statusCode(200)
                    .extract().response();
            String responseString = res.asString();
            JsonPath js = new JsonPath(responseString);

            Assert.assertEquals(json.get("data.companies[0].company_name"), "CVS Health Corporation");
            Assert.assertEquals(json.get("data.companies[0].address"), "11670 Plaza America Drive");
            Assert.assertEquals(json.get("data.companies[0].city"), "Reston");
            Assert.assertEquals(json.get("data.companies[0].admin_area"), "VA");

            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("The verify User Exist failed!");
        }

    }


    @Test(priority = 2)
    public void CheckForCompanyDetailsWithPartialMatch_Invalid() {
        try {
            RestAssured.baseURI = prp.getProperty("HOST");
            payload = ReusableMethodsClass.generateStringFromResources("./Resources/CareerBuilder/searchCompany.json");
            payload = payload.replace("false", "true");
            //adding space to name
			payload = payload.replace("CVS Health Corporation", "CVS     Health Corporation");

            Response res = given()
                    .header("Content-Type", "application/json")
                    .body(payload)

                    .when()
                    .post("/user/login")

                    .then()
                    .assertThat().statusCode(502)
                    .extract().response();
            String responseString = res.asString();
            JsonPath json = new JsonPath(responseString);
            Assert.assertEquals(json.get("errors[0].message"), "SolrSearchRequest returned non-200 response: 406");
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("The company search with space failed!");
        }

    }

    @Test(priority = 3)
		public void CheckForCompanyDetailsWithPartialMatch_Valid() {

			try {

				payload = ReusableMethodsClass.generateStringFromResources("./Resources/CareerBuilder/searchCompany.json");
				payload = payload.replace("false", "true");
				Response res = given()
						.header("Content-Type", "application/json")
						.header("Authorization", token)
						.body(payload)

						.when()
						.post("/core/smartrecognition/company/search")

						.then()
						.assertThat().statusCode(200)
						.extract().response();
				String responseString = res.asString();
				JsonPath js = new JsonPath(responseString);
                Assert.assertEquals(json.get("data.companies[0].company_name"), "CVS Health Corporation");
                Assert.assertEquals(json.get("data.companies[0].address"), "11670 Plaza America Drive");
                Assert.assertEquals(json.get("data.companies[0].city"), "Reston");
                Assert.assertEquals(json.get("data.companies[0].admin_area"), "VA");

				Thread.sleep(3000);
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail("The verification of company failed");
			}


    }
    //todo : verification method pending
    @Test(priority = 4)
	public void CheckForCompanyDetailsWithShowIgnored_False() {

		try {

			payload = ReusableMethodsClass.generateStringFromResources("./Resources/CareerBuilder/searchCompany.json");
			payload = payload.replace("SHOW_IGNORED", "false");
			Response res = given()
					.header("Content-Type", "application/json")
					.header("Authorization", token)
					.body(payload)

					.when()
					.post("/core/smartrecognition/company/search")

					.then()
					.assertThat().statusCode(200)
					.extract().response();
			String responseString = res.asString();
			JsonPath js = new JsonPath(responseString);

			//verification method pending

			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The verification of company failed with show Ignored false");
		}

	}
    //todo : verification method pending
	@Test(priority = 5)
	public void CheckForCompanyDetailsWithShowIgnored_True() {

		try {

			payload = ReusableMethodsClass.generateStringFromResources("./Resources/CareerBuilder/searchCompany.json");
			payload = payload.replace("SHOW_IGNORED", "true");
			Response res = given()
					.header("Content-Type", "application/json")
					.header("Authorization", token)
					.body(payload)

					.when()
					.post("/core/smartrecognition/company/search")

					.then()
					.assertThat().statusCode(200)
					.extract().response();
			String responseString = res.asString();
			JsonPath js = new JsonPath(responseString);

			//verification method pending

			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The verification of company failed with show Ignored true");
		}

	}

	@Test(priority = 6)
	public void CheckForCompanyDetailsWithPartialAddress() {
		try {
			RestAssured.baseURI = prp.getProperty("HOST");
			payload = ReusableMethodsClass.generateStringFromResources("./Resources/CareerBuilder/searchCompany.json");
			payload = payload.replace("false", "true");
			payload = payload.replace("SHOW_IGNORED", "false");
			payload = payload.replace("CVS Health Corporation", "CVS");

			Response res = given()
					.header("Content-Type", "application/json")
					.body(payload)

					.when()
					.post("/core/smartrecognition/company/search")

					.then()
					.assertThat().statusCode(400)
					.extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("errors[0].message"), "Requested Address :11670 Plaza America Drive");
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The company search with partial address failed!");
		}

	}

	@Test(priority = 7)
	public void addCompany() {
		try {
			RestAssured.baseURI = prp.getProperty("HOST");
			payload = ReusableMethodsClass.generateStringFromResources("./Resources/CareerBuilder/addCompany.json");

			Response res = given()
					.header("Content-Type", "application/json")
					.body(payload)

					.when()
					.post("/core/smartrecognition/company")

					.then()
					.assertThat().statusCode(200)
					.extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("data.document_id"), "**********************************");
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The company addition failed!");
		}

	}


	@Test(priority = 8)
	public void addDuplicateCompany() {
		try {
			RestAssured.baseURI = prp.getProperty("HOST");
			payload = ReusableMethodsClass.generateStringFromResources("./Resources/CareerBuilder/addCompany.json");

			Response res = given()
					.header("Content-Type", "application/json")
					.body(payload)

					.when()
					.post("/core/smartrecognition/company")

					.then()
					.assertThat().statusCode(409)
					.extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("errors[0].type"), "Document already exists.");
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The company addition failed with duplicate details!");
		}

	}

	@Test(priority = 9)
	public void addCompanywithOnlyName() {
		try {
			RestAssured.baseURI = prp.getProperty("HOST");
			payload = ReusableMethodsClass.generateStringFromResources("./Resources/CareerBuilder/addCompany.json");
			payload = payload.replace("WebACE Testing Company", "WebACE Testing Company"+rnd.nextInt(1000));
			payload = payload.replace("116 W Eastman St", "");
			payload = payload.replace("Arlington Heights", "");
			payload = payload.replace("TX", "");

			Response res = given()
					.header("Content-Type", "application/json")
					.body(payload)

					.when()
					.post("/core/smartrecognition/company")

					.then()
					.assertThat().statusCode(200)
					.extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("data.document_id"), "**********************************");
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The company addition failed with duplicate details!");
		}

	}


}

