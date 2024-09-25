package edu.brown.cs.student.main.server.handlers.data_retrieval;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ReturningUserHandler implements Route {
  public FirebaseUtilities storageHandler;

  /**
   * uses shared state storageHandler
   *
   * @param storageHandler
   */
  public ReturningUserHandler(FirebaseUtilities storageHandler) {
    this.storageHandler = storageHandler;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // collect parameters from the request
      String uid = request.queryParams("uid");
      System.out.println(uid);

      // check that uid exists
      if (uid == null) {
        responseMap.put("response_type", "failure");
        responseMap.put("error", "uid cannot be null!");
        return Utils.toMoshiJson(responseMap);
      }

      // Get Firestore instance
      Firestore db = FirestoreClient.getFirestore();

      // Get document reference
      DocumentSnapshot doc = db.collection("index_map").document("indices").get().get();

      if (doc.exists()) {
        // Get the data from the document
        Map<String, Object> data = doc.getData();
        Object uids = data.get("uid_list");

        // TODO: avoid typecasting
        // Check if uid exists in the indices
        if (((ArrayList<String>) uids).contains(uid)) {
          responseMap.put("response_type", "success");
          responseMap.put("result", "true");
        } else {
          responseMap.put("response_type", "success");
          responseMap.put("result", "false");
        }
      } else {
        responseMap.put("response_type", "failure");
        responseMap.put("result", "error: document does not exist");
      }
    } catch (Exception e) {
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap);
  }
}
