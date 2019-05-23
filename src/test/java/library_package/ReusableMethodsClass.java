package library_package;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import io.restassured.path.json.JsonPath;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.io.FileReader;


public class ReusableMethodsClass {

	//Method to convert Raw response to a XML format
	public static XmlPath Raw_to_Xml(Response res)
	{
		String responseString = res.asString();
		XmlPath xml = new XmlPath(responseString);
		return xml;
	}
	//Method to convert Raw response to a JSON format
	public static JsonPath Raw_to_Json(Response res)
	{
		String responseString = res.asString();
		JsonPath json = new JsonPath(responseString);
		return json;
	}

	//Method used to convert Xml/Json to String
	public static String generateStringFromResources(String path) throws IOException
	{
		return new String (Files.readAllBytes(Paths.get(path)));
	}

	//Properties file configuration
	public static Properties getPropertyFile() throws IOException
	{
		Properties prp = new Properties();
		FileInputStream fs = new FileInputStream("./file.properties");
		prp.load(fs);
		return prp;
	}
	//Pulling Data from JSON datasheet
	public static Map<String,Object> getInputValuesMap(String path,String inputArray)
	{
		Map<String,Object> map = new LinkedHashMap<String,Object>();
		JSONObject jsonObject = null;

		JSONParser parser = new JSONParser();
		try
		{
			//Read all Object from the file...
			Object obj = parser.parse(new FileReader(path));

			//convert Object to JSONObject
			jsonObject = (JSONObject)obj; 

			//Get the Json input array...
			JSONArray filterArray = (JSONArray) jsonObject.get(inputArray);

			//Iterate through each key value pair under the input Array 
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> iterator = filterArray.iterator();
			while(iterator.hasNext())
			{
				JSONObject factObj = (JSONObject)iterator.next();
				map.put((String) factObj.get("Label"), (Object) factObj.get("Value"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
}
