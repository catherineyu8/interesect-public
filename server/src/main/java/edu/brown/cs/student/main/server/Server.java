package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.handlers.data_retrieval.GetDQHandler;
import edu.brown.cs.student.main.server.handlers.data_retrieval.ListIQAnswersHandler;
import edu.brown.cs.student.main.server.handlers.data_retrieval.ListRecsHandler;
import edu.brown.cs.student.main.server.handlers.data_retrieval.ListStatsHandler;
import edu.brown.cs.student.main.server.handlers.data_retrieval.ReturningUserHandler;
import edu.brown.cs.student.main.server.handlers.edit_database.AddDQAnswerHandler;
import edu.brown.cs.student.main.server.handlers.edit_database.AddNewUserHandler;
import edu.brown.cs.student.main.server.handlers.edit_database.AddRecHandler;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.io.IOException;
import spark.Filter;
import spark.Spark;

/** Top Level class for our project, utilizes spark to create and maintain our server. */
public class Server {

  /**
   * Sets up server using Spark Instantiates FirebaseUtilities to use for database handlers and
   * Datasources to use for geojson handlers Sets up endpoints for respective handlers
   */
  public static void setUpServer() {
    int port = 3232;
    Spark.port(port);

    after(
        (Filter)
            (request, response) -> {
              response.header("Access-Control-Allow-Origin", "*");
              response.header("Access-Control-Allow-Methods", "*");
            });

    // StorageInterface firebaseUtils;
    try {

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
      Spark.get("list-iq-answers", new ListIQAnswersHandler(fb));

      // used for conditional rendering
      Spark.get("returning-user", new ReturningUserHandler(fb));

      Spark.notFound(
          (request, response) -> {
            response.status(404); // Not Found
            System.out.println("ERROR");
            return "404 Not Found - The requested endpoint does not exist.";
          });
      Spark.init();
      Spark.awaitInitialization();

      System.out.println("Server started at http://localhost:" + port);
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println(
          "Error: Could not initialize Firebase. Likely due to firebase_config.json not being found. Exiting.");
      System.exit(1);
    }
  }

  /**
   * Runs Server.
   *
   * @param args none
   */
  public static void main(String[] args) {
    setUpServer();
  }
}
