package edu.brown.cs.student.main.server.api;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import okio.Buffer;

/** MealDataSource This class is what communicates with the Meal DB to */
public class MealDataSource implements DataSource {

  /**
   * constructor - fills in the stateCodes HashMap We store this as a hashmap so that we don't have
   * to query the census for it since we will need it every time we use this class
   */
  public MealDataSource() {}

  /**
   * getData method that goes through the following steps to get broadband information related to
   * inputted StateAndCounty 1. Use our internal hashmap to search for state and get its code 2. Use
   * that state code to query the census and retrieve the county code for user's inputted county 3.
   * Use the state and county codes retrieved to get the broadband data on specified county
   *
   * @param sc --StateAndCounty object that contains the State and County to search for
   * @return
   * @throws DatasourceException
   * @throws IllegalArgumentException if either the state or county entered had a typo or does not
   *     exist in census data
   */
  public ArrayList<String> getAnswers(int numAnswers)
      throws DatasourceException, IllegalArgumentException {

    ArrayList<String> meals = new ArrayList<>();

    while (meals.size() < numAnswers) {
      try {
        // build request to get broadband data from census api for given state and county
        URL requestURL = new URL("https", "www.themealdb.com", "/api/json/v1/1/random.php");
        HttpURLConnection clientConnection = connect(requestURL);
        Moshi moshi = new Moshi.Builder().build();

        Type mealResponseType = MealResponse.class;
        JsonAdapter<MealResponse> adapter = moshi.adapter(mealResponseType);
        MealResponse body =
            adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
        clientConnection.disconnect();

        // validity checks for response
        if (body == null || body.getMeals().size() < 1) {
          throw new DatasourceException("Malformed response from API");
        }

        // check if answers already contains this meal before adding!
        String m = body.getMeals().get(0).get("strMeal");
        if (meals.contains(m)) {
          continue;
        } else {
          meals.add(m);
        }
      } catch (IOException e) {
        throw new DatasourceException(e.getMessage(), e);
      }
    }
    return meals;
  }

  /**
   * Private helper method; throws IOException so different callers can handle differently if
   * needed.
   */
  private static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if (!(urlConnection instanceof HttpURLConnection))
      throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
    if (clientConnection.getResponseCode() != 200)
      throw new DatasourceException(
          "unexpected: API connection not success status " + clientConnection.getResponseMessage());
    return clientConnection;
  }

  public static class MealResponse {
    private List<Map<String, String>> meals;

    public List<Map<String, String>> getMeals() {
      return this.meals;
    }

    // public void setMeals(List<Map<String, String>> meals) {
    //   this.meals = meals;
    // }
  }
}
