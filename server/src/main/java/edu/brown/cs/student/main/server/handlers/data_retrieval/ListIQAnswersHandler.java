package edu.brown.cs.student.main.server.handlers.data_retrieval;

import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler to query firebase and get a list of pins in a given user's database */
public class ListIQAnswersHandler implements Route {
  public FirebaseUtilities storageHandler;

  /**
   * uses shared state storageHandler
   *
   * @param storageHandler
   */
  public ListIQAnswersHandler(FirebaseUtilities storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * takes user's uid as parameter input to api call and accesses the database for that uid creates
   * an Algorithm object and calls getStats: returns the percent frequency of each user's answer in
   * the database
   *
   * @param request The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   * @return serialized json responseMap with pins list and success/failure information
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      String uid = request.queryParams("uid");

      if (uid == null) {
        responseMap.put("response_type", "failure");
        responseMap.put("error", "uid cannot be null!");
        return Utils.toMoshiJson(responseMap);
      }

      // check if user exists
      if (!this.storageHandler.userExists(uid)) {
        responseMap.put("response_type", "failure");
        responseMap.put("error", "uid not found in database");
        return Utils.toMoshiJson(responseMap);
      }

      String[] answers = this.storageHandler.getAllIQAnswers(uid);

      responseMap.put("response_type", "success");
      responseMap.put("answers", answers);
    } catch (Exception e) {
      // error likely occurred in the storage handler
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }
    return Utils.toMoshiJson(responseMap);
  }
}
