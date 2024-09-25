package edu.brown.cs.student.main.server.storage;

import edu.brown.cs.student.main.server.data_structures.DatabaseException;
import java.util.ArrayList;
import java.util.HashSet;

public interface StorageInterface {

  /**
   * check if a user exists in our database by checking if a document with their uid containing iq
   * collection containing q0 exists - just checking their uid document wasn't working for some
   * weird reason (probably bc user documents only contain collections, not fields?) - this should
   * be fine though bc of the way we add users
   *
   * @param uid
   * @return
   */
  public boolean userExists(String uid);

  /**
   * Return the count of the provided answer number
   *
   * @param quizLabel
   * @param answer
   * @return
   */
  public Long getAnswerCount(String quizLabel, String answer);

  /**
   * Retrieves the initial quiz answer for the provided user with the given question num
   *
   * @param uid
   * @param num
   * @return
   */
  public String retrieveIQAnswer(String uid, int num);

  /** get all of a user's IQ answers at once */
  public String[] getAllIQAnswers(String uid);

  /**
   * try to get a daily quiz answer for the inputted user/date catch all the exceptions separately
   * so database exception goes through!
   *
   * @throws DatabaseException
   */
  public String retrieveDQAnswer(String uid, String date) throws DatabaseException;

  /**
   * Check if today has DQ answers already, if so return them If not, returns a Database Exception
   *
   * @throws DatabaseException
   */
  public ArrayList<String> getTodaysDQAnswers() throws DatabaseException;

  /**
   * check database for today's daily quiz question and return if found - will never be called when
   * it doesn't exist anyway
   *
   * @return
   * @throws DatabaseException
   */
  public String getTodaysDQ();

  /** Get all of a user's daily quiz answers as string[]? */
  public ArrayList<String[]> getDQAnswers(String uid);

  /**
   * For a given user, check if they already have a recommendation in the database for today
   *
   * @param uid
   * @return
   */
  public boolean recExistsToday(String uid);

  /**
   * method to retrieve list of recommendations from database (format may have to change based on
   * what frontend needs)
   */
  public ArrayList<String[]> getRecs(String uid);

  /**
   * get the uid index for a string uid
   *
   * @param uid
   * @return
   */
  public int getUidIndex(String uid);

  /**
   * get uid for provided index
   *
   * @param index
   * @return
   */
  public String getIndexUid(int index);

  /**
   * get the user corresponding to a given index
   *
   * @param requestUid
   * @param used
   * @return
   */
  public String getRandomUid(String requestUid, HashSet<Integer> used);

  /**
   * method to get total users in database
   *
   * @return
   */
  public int getTotalUsers();

  /////////// FUNCTIONS WRITTEN BY TIM//////////////

  /**
   * Using a user's uid and the collection we want to access, return collection of data
   *
   * @param uid
   * @param collection_id
   * @return List that represents a collection of data stored in user's firebase
   * @throws InterruptedException
   * @throws ExecutionException
   * @throws IllegalArgumentException
   */
}
