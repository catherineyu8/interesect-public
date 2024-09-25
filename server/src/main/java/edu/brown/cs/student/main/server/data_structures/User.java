package edu.brown.cs.student.main.server.data_structures;

import java.util.ArrayList;
import java.util.HashMap;

/** User object doesn't need to be sent as Json! Questions: - */
public class User {

  public String uid;
  public ArrayList<String>
      dailyQuizAnswers; // use strings so that we don't have a set number of answers
  public HashMap<String, String> initialQuizAnswers; // hashmap for now
  public ArrayList<String> previousRecs; // may change from string!
}
