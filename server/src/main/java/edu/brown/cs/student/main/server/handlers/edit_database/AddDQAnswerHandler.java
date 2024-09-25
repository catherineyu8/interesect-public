package edu.brown.cs.student.main.server.handlers.edit_database;

import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class AddDQAnswerHandler implements Route {

  public FirebaseUtilities storageHandler;

  public AddDQAnswerHandler(FirebaseUtilities storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Query Params: String uid, String answer
   *
   * @param request The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   * @return The content to be set in the response
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // collect parameters from the request
      String uid = request.queryParams("uid");
      String answer = request.queryParams("answer");

      // check that uid exists, and does not overlap with one that already exists in db
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

      // check that answer exists
      if (answer == null) {
        responseMap.put("response_type", "failure");
        responseMap.put("error", "daily quiz answer cannot be null!");
        return Utils.toMoshiJson(responseMap);
      }
      // TODO: should we check if uid already exists? or just replace it if it does?

      Map<String, Object> data = new HashMap<>();
      data.put("answer", answer);

      // use the storage handler to add the document to the database
      this.storageHandler.addDailyQuiz(uid, answer);

      responseMap.put("response_type", "success");
      responseMap.put("user_info", data);

    } catch (Exception e) {
      // error likely occurred in the storage handler
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap);
  }
}
