package edu.brown.cs.student.main.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.testng.AssertJUnit.assertNotNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.data_retrieval.GetDQHandler;
import edu.brown.cs.student.main.server.handlers.data_retrieval.ListRecsHandler;
import edu.brown.cs.student.main.server.handlers.data_retrieval.ListStatsHandler;
import edu.brown.cs.student.main.server.handlers.data_retrieval.ReturningUserHandler;
import edu.brown.cs.student.main.server.handlers.edit_database.AddDQAnswerHandler;
import edu.brown.cs.student.main.server.handlers.edit_database.AddNewUserHandler;
import edu.brown.cs.student.main.server.handlers.edit_database.AddRecHandler;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class UnitTesting {
  private final Type listListString =
      Types.newParameterizedType(List.class, List.class, String.class);
  private JsonAdapter<List<List<String>>> adapter;

  @BeforeEach
  public void setup() throws IOException {
    // In fact, restart the entire Spark server for every test!

    FirebaseUtilities fb = new FirebaseUtilities();

    // used for login
    Spark.get("add-new-user", new AddNewUserHandler(fb));

    // used for daily quiz
    Spark.get("add-DQ-answer", new AddDQAnswerHandler(fb));
    Spark.get("get-DQ", new GetDQHandler(fb));

    // used for recs
    Spark.get("add-rec", new AddRecHandler(fb));
    Spark.get("list-recs", new ListRecsHandler(fb));

    // used for stats
    // Spark.get("add-stat", new AddStatHandler(fb));
    Spark.get("list-stats", new ListStatsHandler(fb));

    // used for conditional rendering
    Spark.get("returning-user", new ReturningUserHandler(fb));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(listListString);
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints after each test
    Spark.stop();
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  /**
   * Helper to start a connection to a specific API endpoint/params
   *
   * @param apiCall the call string, including endpoint (NOTE: this would be better if it had more
   *     structure!)
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  // testing successfully adding a new user
  @Test
  public void testingAddNewUser() throws IOException {
    HttpURLConnection connection =
        tryRequest("add-new-user?uid=test-uid&inita1=1&inita2=2&inita3=3&inita4=4");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("success", responseMap.get("response_type"));
    connection.disconnect();
  }

  // testing adding a new user without answers
  @Test
  public void testingAddNewUserNoAnswers() throws IOException {
    HttpURLConnection connection = tryRequest("add-new-user?uid=test-uid");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("failure", responseMap.get("response_type"));
    assertEquals("each initial quiz question must have an answer!", responseMap.get("error"));
    connection.disconnect();
  }

  // testing adding a new user with not enough answers
  @Test
  public void testingAddNewUserNotEnoughAnswers() throws IOException {
    HttpURLConnection connection =
        tryRequest("add-new-user?uid=test-uid&inita1=1&inita2=2&inita3=3");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("failure", responseMap.get("response_type"));
    assertEquals("each initial quiz question must have an answer!", responseMap.get("error"));
    connection.disconnect();
  }

  // testing adding a new user with no uid
  @Test
  public void testingAddNewUserNoUID() throws IOException {
    HttpURLConnection connection = tryRequest("add-new-user?inita1=1&inita2=2&inita3=3&inita4=4");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("failure", responseMap.get("response_type"));
    assertEquals("uid cannot be null!", responseMap.get("error"));
    connection.disconnect();
  }

  // testing adding a new user for user that already exists
  @Test
  public void testingAddNewUserAlrExists() throws IOException {
    HttpURLConnection connection =
        tryRequest("add-new-user?uid=catherine&inita1=1&inita2=2&inita3=3&inita4=4");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("failure", responseMap.get("response_type"));
    assertEquals("a user with that uid already exists", responseMap.get("error"));
    connection.disconnect();
  }

  // testing add DQ answer successfully
  @Test
  public void addDQanswer() throws IOException {
    HttpURLConnection connection = tryRequest("add-DQ-answer?uid=test-uid&answer=testing");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("success", responseMap.get("response_type"));
    connection.disconnect();
  }

  // testing add dq answer without uid
  @Test
  public void addDQanswerNoUID() throws IOException {
    HttpURLConnection connection = tryRequest("add-DQ-answer?answer=testing");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("failure", responseMap.get("response_type"));
    assertEquals("uid cannot be null!", responseMap.get("error"));
    connection.disconnect();
  }

  // testing add dq answer without answer
  @Test
  public void addDQanswerNoAnswer() throws IOException {
    HttpURLConnection connection = tryRequest("add-DQ-answer?uid=test-uid");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("failure", responseMap.get("response_type"));
    assertEquals("daily quiz answer cannot be null!", responseMap.get("error"));
    connection.disconnect();
  }

  // testing get dq
  @Test
  public void getDQ() throws IOException {
    HttpURLConnection connection = tryRequest("get-DQ");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("success", responseMap.get("response_type"));
    connection.disconnect();
  }

  // testing add rec
  @Test
  public void addRec() throws IOException {
    HttpURLConnection connection = tryRequest("add-rec?uid=test-uid");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("success", responseMap.get("response_type"));
    connection.disconnect();
  }

  // testing add rec without uid
  @Test
  public void addRecNoUID() throws IOException {
    HttpURLConnection connection = tryRequest("add-rec?uid=test-uid");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("failure", responseMap.get("response_type"));
    assertEquals("uid cannot be null!", responseMap.get("error"));
    connection.disconnect();
  }

  // testing for non existent user
  @Test
  public void addRecNoUser() throws IOException {
    HttpURLConnection connection = tryRequest("add-rec?uid=charlotte");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("failure", responseMap.get("response_type"));
    assertEquals("uid not found in database", responseMap.get("error"));
    connection.disconnect();
  }

  // testing list recs
  @Test
  public void listRecs() throws IOException {
    HttpURLConnection connection = tryRequest("list-recs?uid=test-uid");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("success", responseMap.get("response_type"));
    connection.disconnect();
  }

  // testing list recs without uid
  @Test
  public void listRecsNoUID() throws IOException {
    HttpURLConnection connection = tryRequest("list-recs");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("failure", responseMap.get("response_type"));
    assertEquals("uid cannot be null!", responseMap.get("error"));
    connection.disconnect();
  }

  // testing list recs for user not in database
  @Test
  public void listRecsNoUser() throws IOException {
    HttpURLConnection connection = tryRequest("list-recs?uid=charlotte");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("failure", responseMap.get("response_type"));
    assertEquals(
        "Cannot invoke \\\"String.equals(Object)\\\" because \\\"uidInAnswer\\\" is null",
        responseMap.get("error"));
    connection.disconnect();
  }

  // testing list stats
  @Test
  public void listStats() throws IOException {
    HttpURLConnection connection = tryRequest("list-stats?uid=test-uid");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("success", responseMap.get("response_type"));
    connection.disconnect();
  }

  // testing list stats without uid
  @Test
  public void listStatsNoUID() throws IOException {
    HttpURLConnection connection = tryRequest("list-stats");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("failure", responseMap.get("response_type"));
    assertEquals("uid cannot be null!", responseMap.get("error"));
    connection.disconnect();
  }

  // testing list stats without user not in database
  @Test
  public void listStatsNoUser() throws IOException {
    HttpURLConnection connection = tryRequest("list-stats");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("failure", responseMap.get("response_type"));
    assertEquals("uid not found in database", responseMap.get("error"));
    connection.disconnect();
  }

  // testing returning user for true case
  @Test
  public void returningUserTrue() throws IOException {
    HttpURLConnection connection = tryRequest("returning-user?uid=catherine");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("success", responseMap.get("response_type"));
    assertEquals("true", responseMap.get("result"));
    connection.disconnect();
  }

  // testing returning user for false case
  @Test
  public void returningUserFalse() throws IOException {
    HttpURLConnection connection = tryRequest("returning-user?uid=molly");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("success", responseMap.get("response_type"));
    assertEquals("false", responseMap.get("result"));
    connection.disconnect();
  }

  // testing list stats without uid
  @Test
  public void returningUserNoUID() throws IOException {
    HttpURLConnection connection = tryRequest("list-stats");
    assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode(), "should be 200");

    String responseJson = new Buffer().readFrom(connection.getInputStream()).readUtf8();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    Map<String, String> responseMap = jsonAdapter.fromJson(responseJson);
    assertNotNull(responseMap);
    assertEquals("failure", responseMap.get("response_type"));
    assertEquals("uid cannot be null!", responseMap.get("error"));
    connection.disconnect();
  }
}
