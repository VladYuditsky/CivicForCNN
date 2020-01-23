import java.io.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.apache.commons.lang3.StringUtils;
/*
 * To execute Java, please define "static void main" on a class
 * named Solution.
 *
 * If you need more classes, simply define them inline.
 */

public class Solution {

  // variables for class
  private static final String api_key = "AIzaSyB-2mACwuLJaxqsqbSAgzkhNpMMRCeuu5w";
  private static final String request_url = "https://www.googleapis.com/civicinfo/v2/elections";
  // build full request
  private static final String request = request_url +"?key=" + api_key;
  // cache response
  private static JSONObject response;
  
  
  public static void main(String[] args) {      
    
    // return and format response from civic API
    String rawResponse = sendGET(request);
    // set response globally for use in test cases
    response = convertToJSON(rawResponse);
    System.out.println("electionQuery Response --> " + response);

    // initiate JUnit
    JUnitCore.main("Solution");
  }
  
  
  
  
  
  // parse JSON responses returned as String type
  public static JSONObject convertToJSON(String str) {
   
    try {
      // parse json
      JSONParser parser = new JSONParser();
      JSONObject json = (JSONObject) parser.parse(str);
      return json;      
    } catch(Exception e){
      System.out.println("Cannot parse String: " + str + " to JSONObject"); 
      return null;
    }

  }

    
  // get response code
  public static int getResponseStatus(String url){
    
    try {
      // set url
      URL obj = new URL(url);
      // open connection and set request type to get
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();
      con.setRequestMethod("GET");
      // get the status code
      return con.getResponseCode();
      
    } catch(Exception e){
      System.out.println("Could not send GET request. Please take a look at the request."); 
      return 400;
    }
  }
  
  
  // send GET Requests
  public static String sendGET(String url){
    
    try {
      // set url
      URL obj = new URL(url);
      // open connection and set request type to get
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();
      con.setRequestMethod("GET");
      // get the status code
      int responseCode = con.getResponseCode();
      // if 200 proceed
      if (responseCode == HttpURLConnection.HTTP_OK) {
        // read response
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();
        // print response and return
        //System.out.println(response.toString());
        return response.toString();
        
      } else {
        System.out.println("Server resonded with status: " + responseCode); 
        return "";
      } // if else
       
    } catch(Exception e){
      System.out.println("Could not send GET request. Please take a look at the request."); 
      return null;
    }
    
    
  }
  
  
  
  
  
  
  // ================== TESTS =====================
  
  /*
  * Test response status is 200
  */
  @Test
  public void testResponseStatus() {
  
    Assert.assertEquals(200, getResponseStatus(request));
  }
  
  /*
  * Test response not null
  */
  @Test
  public void testResponseNotNull() {
  
    Assert.assertNotNull(response);
  }
  
  /*
  * Test kind value
  */
  @Test
  public void testResponseValue_Kind() {
  
    String goodValue = "civicinfo#electionsQueryResponse";
    String responseValue = (String) response.get("kind");
    Assert.assertEquals(goodValue, responseValue);
  }
  
  /*
  * Test elections array not null or empty
  */
  @Test
  public void testResponseValue_ElectionsArray() {
  
    JSONArray elections = (JSONArray) response.get("elections");
    Assert.assertNotNull(elections);
    Assert.assertFalse((elections+"").isEmpty());
  }

  /*
  * Test values in elections array
  */
  @Test
  public void testResponseValue_ArrayValues() {
  
    // good values expected
    String ocdDivision = "ocd-division";
    String country ="us";
    String name = "VIP Test Election";
    String electionDay = "2021-06-06";
    String id = "2000";
    
    JSONArray elections = (JSONArray) response.get("elections");
    // format json inside of array to string to be converted back to json
    String jsonStr = elections+"";
    jsonStr = jsonStr.replaceAll("\\[", "");
    jsonStr = jsonStr.replaceAll("\\]", "");
    JSONObject json = convertToJSON(jsonStr);
    System.out.println("Formatted JSON on JSONArray --> " + json);
    
    // Get value in json
    String value_ocdDivision = (String) json.get("ocdDivisionId");
    String value_name = (String) json.get("name");
    String value_electionDay = (String) json.get("electionDay");
    String value_id = (String) json.get("id");

    // assert value contains good values top of test method
    // ocd division
    Assert.assertTrue(value_ocdDivision.startsWith(ocdDivision));
    Assert.assertTrue(value_ocdDivision.endsWith("country:"+country));
    // name
    Assert.assertEquals(name, value_name);
    // election day
    Assert.assertEquals(electionDay, value_electionDay);
    // id
    Assert.assertEquals(id, value_id);
    
  } 
    
}
