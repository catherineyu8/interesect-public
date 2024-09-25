package edu.brown.cs.student.main.server.api;

import io.github.cdimascio.dotenv.Dotenv;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import okio.Buffer;

/**
 * CensusDataSource This class is what communicates with the Census API (contains ALL functionality
 * used to communicate with ACS in this project) Stores state codes in internal hashmap as instance
 * variable "Caches" county codes in case the same state is queried on the next query Retrieves
 * broadband information per user requests
 */
public class MovieGenreDataSource implements DataSource {

  /**
   * constructor - fills in the stateCodes HashMap We store this as a hashmap so that we don't have
   * to query the census for it since we will need it every time we use this class
   */
  public MovieGenreDataSource() {}

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

    Dotenv dotenv = Dotenv.load();

    String MOVIE_API_KEY = dotenv.get("MOVIE_API_KEY");

    ArrayList<String> genres = new ArrayList<>();

    while (genres.size() < numAnswers) {
      try {
        URL requestURL =
            new URL(
                "https",
                "api.themoviedb.org",
                "/3/genre/movie/list?language=en" + "&api_key=" + MOVIE_API_KEY);
        HttpURLConnection clientConnection = connect(requestURL);
        Moshi moshi = new Moshi.Builder().build();

        Type responseType = GenreResponse.class;
        JsonAdapter<GenreResponse> adapter = moshi.adapter(responseType);
        GenreResponse body =
            adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
        clientConnection.disconnect();

        // validity checks for response
        if (body == null || body.genres.size() < 1) {
          throw new DatasourceException("Malformed response from API");
        }

        int mMax = body.genres.size() - 1;
        int mMin = 0;
        int mRange = mMax - mMin + 1;
        int mIndex = (int) (Math.random() * mRange) + mMin;

        String g = body.genres.get(mIndex).name;
        if (genres.contains(g)) {
          continue;
        } else {
          genres.add(g);
        }
      } catch (IOException e) {
        throw new DatasourceException(e.getMessage(), e);
      }
    }
    return genres;
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

  public static class GenreResponse {
    List<Genre> genres;
  }

  public static class Genre {
    String name;
  }

  //   public static class DogAttribute {
  //     String name;
  //     String description;
  //   }
}
