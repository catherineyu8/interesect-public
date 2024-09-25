package edu.brown.cs.student.main.server.handlers.data_retrieval;

import edu.brown.cs.student.main.server.Algorithm;
import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler to query firebase and get a list of pins in a given user's database */
public class ListRecsHandler implements Route {
  public FirebaseUtilities storageHandler;

  /**
   * uses shared state storageHandler
   *
   * @param storageHandler
   */
  public ListRecsHandler(FirebaseUtilities storageHandler) {
    this.storageHandler = storageHandler;
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
    try {
      String uid = request.queryParams("uid");

      if (uid == null) {
        responseMap.put("response_type", "failure");
        responseMap.put("error", "uid cannot be null!");
        return Utils.toMoshiJson(responseMap);
      }

      if (!this.storageHandler.recExistsToday(uid)) {
        System.out.println("date doesn't exist");
        this.createAndStoreNewRec(uid);
      }
      ArrayList<String[]> recs = this.storageHandler.getRecs(uid);
      // String[][] recs = {{"rec1", "date1"}, {"rec1", "date2"}}; // TRIVIAL FOR NOW

      responseMap.put("response_type", "success");
      responseMap.put("recs", recs);
    } catch (Exception e) {
      // error likely occurred in the storage handler
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }
    return Utils.toMoshiJson(responseMap);
  }

  private void createAndStoreNewRec(String uid) {
    Algorithm a = new Algorithm(this.storageHandler);
    String[] rec = a.runAlgorithm(uid);
    this.storageHandler.addRecString(uid, rec[0], rec[1]);
  }
}
