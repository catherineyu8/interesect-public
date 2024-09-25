package edu.brown.cs.student.main.server.handlers.edit_database;

import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class AddRecHandler implements Route {

  public FirebaseUtilities storageHandler;

  public AddRecHandler(FirebaseUtilities storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Invoked when a request is made on this route's corresponding path e.g. '/hello'
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

      // TODO: use algorithm to get a new rec
      String rec = "you should try listening to ricky montgomery.";

      // get the current word count to make a unique word_id by index.
      //   /** this could be helpful for getting size of certain things in database!!  */
      //   int qCount = this.storageHandler.getCollection(uid, "pins").size();
      //   String qNum = "q-" + qCount;

      // use the storage handler to add the document to the database
      this.storageHandler.addRecString(uid, rec, "placeholder"); // TODO: make recs the right type

      responseMap.put("response_type", "success");
      responseMap.put("rec", rec);
    } catch (Exception e) {
      // error likely occurred in the storage handler
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap);
  }
}
