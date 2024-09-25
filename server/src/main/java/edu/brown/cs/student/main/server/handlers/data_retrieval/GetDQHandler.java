package edu.brown.cs.student.main.server.handlers.data_retrieval;

import edu.brown.cs.student.main.server.api.DataSource;
import edu.brown.cs.student.main.server.api.DogDataSource;
import edu.brown.cs.student.main.server.api.MealDataSource;
import edu.brown.cs.student.main.server.api.MovieDataSource;
import edu.brown.cs.student.main.server.api.MovieGenreDataSource;
import edu.brown.cs.student.main.server.data_structures.DatabaseException;
import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler to query firebase and get a list of pins in a given user's database */
public class GetDQHandler implements Route {
  public FirebaseUtilities storageHandler;
  public String[] apis;

  /**
   * uses shared state storageHandler
   *
   * @param storageHandler
   */
  public GetDQHandler(FirebaseUtilities storageHandler) {
    this.storageHandler = storageHandler;
    this.apis = new String[] {"dog", "meal", "movie", "movie-genre"};
  }

  /**
   * takes user's uid as parameter input to api call and accesses the database for that uid returns
   * the user
   *
   * @param request The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   * @return serialized json responseMap with pins list and success/failure information
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();

    // collect parameters from the request
    // String api = request.queryParams("api");

    // if (api == null) {
    //   responseMap.put("response_type", "failure");
    //   responseMap.put("error", "api cannot be null!");
    //   return Utils.toMoshiJson(responseMap);
    // }

    ArrayList<String> answers = new ArrayList<>();
    String question = "";
    // check if daily quiz answers already exist:
    try {
      answers = this.storageHandler.getTodaysDQAnswers();
      question = this.storageHandler.getTodaysDQ();
      responseMap.put("response_type", "success");

      responseMap.put("q", question); // TODO: get the question from the database
      responseMap.put("ansChoices", answers);

    } catch (DatabaseException e) { // no answers exist yet, generate and store them
      // generate a random api from the list of apis we have
      int max = 3;
      int min = 0;
      int range = max - min + 1;
      int i = (int) (Math.random() * range) + min;
      String api = this.apis[i];

      answers = this.generateAnswersFromApi(api);
      question = "What's your favorite " + api + "?";
      this.storageHandler.storeDailyQuizAnswers(answers, question);

      responseMap.put("response_type", "success");
      responseMap.put("q", question);
      responseMap.put("ansChoices", answers);
    }

    return Utils.toMoshiJson(responseMap);
  }

  /**
   * generate daily quiz answers using apis
   *
   * @param api
   * @return
   */
  private ArrayList<String> generateAnswersFromApi(String api) {
    ArrayList<String> answers = new ArrayList<>();
    try {
      if (api.equals("meal")) {
        DataSource source = new MealDataSource();
        answers = source.getAnswers(4);
      } else if (api.equals("dog")) {
        DataSource source = new DogDataSource();
        answers = source.getAnswers(4);
      } else if (api.equals("movie")) {
        DataSource source = new MovieDataSource("movie");
        answers = source.getAnswers(4);
      }
      // else if (api.equals("tv")) {
      //   DataSource source = new MediaDataSource("tv");
      //   answers = source.getAnswers(4);
      // }
      else if (api.equals("movie-genre")) {
        DataSource source = new MovieGenreDataSource();
        answers = source.getAnswers(4);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
    return answers;
  }
}
