package edu.brown.cs.student.main.server.handlers.edit_database;

import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class AddNewUserHandler implements Route {

  public FirebaseUtilities storageHandler;

  public AddNewUserHandler(FirebaseUtilities storageHandler) {
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
      // String initQuizAns1 = request.queryParams("init-ans-1");
      // String initQuizAns2 = request.queryParams("init-ans-2");
      // String initQuizAns3 = request.queryParams("init-ans-3");

      String initQuizAns1 = request.queryParams("inita1");
      String initQuizAns2 = request.queryParams("inita2");
      String initQuizAns3 = request.queryParams("inita3");
      String initQuizAns4 = request.queryParams("inita4");

      String[] quizAnswers = {
        initQuizAns1, initQuizAns2, initQuizAns3, initQuizAns4
      }; // TODO: change to match # q's

      // check that uid exists, and does not overlap with one that already exists in db
      if (uid == null) {
        responseMap.put("response_type", "failure");
        responseMap.put("error", "uid cannot be null!");
        return Utils.toMoshiJson(responseMap);
      }

      // if the user already exists
      if (this.storageHandler.userExists(uid)) {
        responseMap.put("response_type", "failure");
        responseMap.put("error", "a user with that uid already exists");
        return Utils.toMoshiJson(responseMap);
      }

      // check that all questions have an answer
      if (Arrays.asList(quizAnswers).contains(null)) {
        responseMap.put("response_type", "failure");
        responseMap.put("error", "each initial quiz question must have an answer!");
        return Utils.toMoshiJson(responseMap);
      }

      Map<String, Object> data = new HashMap<>();
      data.put("ans1", initQuizAns1);
      data.put("ans2", initQuizAns2);
      data.put("ans3", initQuizAns3);
      data.put("ans4", initQuizAns4);

      // get the current word count to make a unique word_id by index.
      //   /** this could be helpful for getting size of certain things in database!!  */
      //   int qCount = this.storageHandler.getCollection(uid, "pins").size();
      //   String qNum = "q-" + qCount;

      // use the storage handler to add the document to the database
      this.storageHandler.addUser(uid, quizAnswers);

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
