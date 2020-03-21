package test_package;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import library_package.*;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import static org.hamcrest.Matchers.*;

public class CustomAuthAPIsTest {
    Properties prp;
    String payload;
    String userName = null;
    String teamName = null;
    JsonPath json = null;
    private String token;
    String docID;
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
    public void CheckForCompanyDetailsWithPartialMatch_true() {
        try {
			preConditionSet("HOST", "./Resources/CareerBuilder/searchCompany.json");
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
				preConditionSet("HOST", "./Resources/CareerBuilder/searchCompany.json");
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

    @Test(priority = 4)
	public void CheckForCompanyDetailsWithShowIgnored_False() {

		try {
			preConditionSet("HOST", "./Resources/CareerBuilder/searchCompany.json");
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
			Assert.assertEquals(json.get("data.companies[0].phone_scores[0].contact"),"7034815722");

			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The verification of company failed with show Ignored false");
		}

	}

	@Test(priority = 5)
	public void CheckForCompanyDetailsWithShowIgnored_True() {

		try {
			preConditionSet("HOST", "./Resources/CareerBuilder/searchCompany.json");
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

			Assert.assertEquals(json.get("data.companies[0].phone_scores[0].contact"),"7034815722");

			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The verification of company failed with show Ignored true");
		}

	}

	@Test(priority = 6)
	public void CheckForCompanyDetailsWithPartialAddress() {
		try {
			preConditionSet("HOST", "./Resources/CareerBuilder/searchCompany.json");
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
	public String addCompany() {
		try {
			preConditionSet("HOST", "./Resources/CareerBuilder/addCompany.json");
			payload = payload.replace("WebACE Testing Company", "WebACE Testing Company"+rnd.nextInt(1000));
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
		return json.get("data.document_id");
	}


	@Test(priority = 8)
	public void addDuplicateCompany() {
		try {
			preConditionSet("HOST", "./Resources/CareerBuilder/addCompany.json");

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
			preConditionSet("HOST", "./Resources/CareerBuilder/addCompany.json");
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

	@Test(priority = 10)
	public void updateCompany() {
		docID= addCompany();
		try {
			preConditionSet("HOST", "./Resources/CareerBuilder/addCompany.json");
			payload = payload.replace("116 W Eastman St", "200 W Eastman St");

			Response res = given()
					.header("Content-Type", "application/json")
					.body(payload)

					.when()
					.post("/core/smartrecognition/company/"+docID)

					.then()
					.assertThat().statusCode(200)
					.extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("data"), "");
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The company update failed with duplicate details!");
		}

	}

	@Test(priority = 11)
	public void deleteCompany() {
		docID= addCompany();
		try {
			preConditionSet("HOST", "./Resources/CareerBuilder/addCompany.json");

			Response res = given()
					.header("Content-Type", "application/json")
					.body(payload)

					.when()
					.post("/core/smartrecognition/company/"+docID)

					.then()
					.assertThat().statusCode(200)
					.extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("data"), "");
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The company deletion failed with duplicate details!");
		}

	}

	@Test(priority = 12)
	public void deleteCompany_Negative() {
		docID= null;
		try {
			preConditionSet("HOST", "./Resources/CareerBuilder/addCompany.json");
			payload = payload.replace("116 W Eastman St", "200 W Eastman St");

			Response res = given()
					.header("Content-Type", "application/json")
					.body(payload)

					.when()
					.post("/core/smartrecognition/company/"+docID)

					.then()
					.assertThat().statusCode(405)
					.extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("errors[0].message"), "The requested HTTP method is not supported by this resource.");
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The company deletion negative failed with duplicate details!");
		}

	}

	@Test(priority = 13)
	public void addContactToCompany() {
		docID= addCompany();
		try {
			preConditionSet("HOST", "./Resources/CareerBuilder/addContact.json");
			payload = payload.replace("116 W Eastman St", "200 W Eastman St");

			Response res = given()
					.header("Content-Type", "application/json")
					.body(payload)

					.when()
					.post("/core/smartrecognition/company/"+docID+"/contact/"+"8164440019")

					.then()
					.assertThat().statusCode(200)
					.extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("data"), "");
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The company deletion negative failed with duplicate details!");
		}

	}
	@Test(priority = 14)
	public void addContactToCompany_negative() {
		docID= addCompany();
		try {
			preConditionSet("HOST", "./Resources/CareerBuilder/addContact.json");
			payload = payload.replace("Phone", "Email");

			Response res = given()
					.header("Content-Type", "application/json")
					.body(payload)

					.when()
					.post("/core/smartrecognition/company/"+docID+"/contact/"+"8164440019")

					.then()
					.assertThat().statusCode(500)
					.extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("errors[0].message"), "invalid email 8164440019");
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The add contact negative failed with duplicate details!");
		}

	}

	@Test(priority = 15)
	public void verifyInvalidContactType() {
		docID= addCompany();
		try {
			preConditionSet("HOST", "./Resources/CareerBuilder/addContact.json");
			payload = payload.replace("Phone", "ABC");

			Response res = given()
					.header("Content-Type", "application/json")
					.body(payload)

					.when()
					.post("/core/smartrecognition/company/"+docID+"/contact/"+"8164440019")

					.then()
					.assertThat().statusCode(400)
					.extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("errors[0].type"), "Unknown contact_type");
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The add contact with invalid Type negative failed with duplicate details!");
		}

	}

	@Test(priority = 16)
	public void addContactwithoutDocId_Negative() {
		docID= null;
		try {
			preConditionSet("HOST", "./Resources/CareerBuilder/addContact.json");

			Response res = given()
					.header("Content-Type", "application/json")
					.body(payload)

					.when()
					.post("/core/smartrecognition/company/"+docID+"/contact/"+"8164440019")

					.then()
					.assertThat().statusCode(404)
					.extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("errors[0].message"), "The requested HTTP resource was not found.");
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The add contact without DocID negative failed with duplicate details!");
		}

	}

	@Test(priority = 17)
	public void addContactwithoutContactId_Negative() {
		docID= null;
		try {
			preConditionSet("HOST", "./Resources/CareerBuilder/addContact.json");

			Response res = given()
					.header("Content-Type", "application/json")
					.body(payload)

					.when()
					.post("/core/smartrecognition/company/"+docID+"/contact/")

					.then()
					.assertThat().statusCode(404)
					.extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("errors[0].message"), "The requested HTTP resource was not found.");
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The add contact without contact ID negative failed with duplicate details!");
		}

	}

	@Test(priority = 18)
	public void ignoreContact() {
		docID= null;
		try {
			preConditionSet("HOST", "./Resources/CareerBuilder/addContact.json");

			Response res = given()
					.header("Content-Type", "application/json")
					.body(payload)

					.when()
					.post("/core/smartrecognition/company/"+docID+"/contact/"+"8164440019/ignore")

					.then()
					.assertThat().statusCode(200)
					.extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("data"), "");
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The ignore contact failed!");
		}

	}

	@Test(priority = 19)
	public void ignoreContact_Invalid() {
		docID= null;
		try {
			preConditionSet("HOST", "./Resources/CareerBuilder/addContact.json");

			Response res = given()
					.header("Content-Type", "application/json")
					.body(payload)

					.when()
					.post("/core/smartrecognition/company/"+docID+"/contact/"+"8164440019/ignore")

					.then()
					.assertThat().statusCode(400)
					.extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("errors[0].message"), "The requested HTTP resource was not found.");
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The ignore contact failed!");
		}

	}

	@Test(priority = 20)
	public void addAttempt_true() {
		docID= addCompany();
		try {
			preConditionSet("HOST", "./Resources/CareerBuilder/addAttempt.json");
			payload = payload.replace("ORDER_ID", "9561769");
			payload = payload.replace("ATTEMPT_DATE", "2020-03-02T12:06:48Z");
			Response res = given()
					.header("Content-Type", "application/json")
					.body(payload)

					.when()
					.post("/core/smartrecognition/company/"+docID+"/contact/"+"8164440019/attempt")

					.then()
					.assertThat().statusCode(200)
					.extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("data"), "");
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The add attempt success!");
		}

	}

	@Test(priority = 20)
	public void addAttempt_false() {
		docID= addCompany();
		try {
			preConditionSet("HOST", "./Resources/CareerBuilder/addAttempt.json");
			payload = payload.replace("true", "false");
			Response res = given()
					.header("Content-Type", "application/json")
					.body(payload)

					.when()
					.post("/core/smartrecognition/company/"+docID+"/contact/"+"8164440019/attempt")

					.then()
					.assertThat().statusCode(200)
					.extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("data"), "");
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The add attempt success!");
		}

	}

	@Test(priority = 21)
	public void addAttemptwithoutOrderID() {
		docID= addCompany();
		try {
			preConditionSet("HOST", "./Resources/CareerBuilder/addAttempt.json");
			payload = payload.replace("ORDER_ID", "");
			Response res = given()
					.header("Content-Type", "application/json")
					.body(payload)

					.when()
					.post("/core/smartrecognition/company/"+docID+"/contact/"+"8164440019/attempt")

					.then()
					.assertThat().statusCode(400)
					.extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("errors[0].message"), "order_id is required");
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The addAttemptwithoutOrderID failed!");
		}

	}

	@Test(priority = 22)
	public void addAttemptwithoutAttemptDate() {
		docID= addCompany();
		try {
			preConditionSet("HOST", "./Resources/CareerBuilder/addAttempt.json");
			payload = payload.replace("ATTEMPT_DATE", "");
			Response res = given()
					.header("Content-Type", "application/json")
					.body(payload)

					.when()
					.post("/core/smartrecognition/company/"+docID+"/contact/"+"8164440019/attempt")

					.then()
					.assertThat().statusCode(400)
					.extract().response();
			String responseString = res.asString();
			JsonPath json = new JsonPath(responseString);
			Assert.assertEquals(json.get("errors[0].message"), "attemptDate cannot be null");
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("The addAttemptwithoutAttemptDate failed!");
		}

	}
}

