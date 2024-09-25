package edu.brown.cs.student.main.server.storage;

import edu.brown.cs.student.main.server.data_structures.DatabaseException;
import java.util.ArrayList;
import java.util.HashSet;

public class MockUtilities implements StorageInterface {

  @Override
  public boolean userExists(String uid) {
    return true;
  }

  @Override
  public Long getAnswerCount(String quizLabel, String answer) {
    return 3L;
  }

  @Override
  public String retrieveIQAnswer(String uid, int num) {
    return "IQ answer";
  }

  @Override
  public String[] getAllIQAnswers(String uid) {
    String[] arr = {"answer1", "answer2", "answer3", "answer4"};
    return arr;
  }

  @Override
  public String retrieveDQAnswer(String uid, String date) throws DatabaseException {
    return "DQ answer";
  }

  @Override
  public ArrayList<String> getTodaysDQAnswers() throws DatabaseException {
    ArrayList<String> arr = new ArrayList<>();
    arr.add("todays DQ answer 1");
    arr.add("todays DQ answer 2");
    arr.add("todays DQ answer 3");
    arr.add("todays sDQ answer 4");
    return arr;
  }

  @Override
  public String getTodaysDQ() {
    return "today's daily quiz question";
  }

  @Override
  public ArrayList<String[]> getDQAnswers(String uid) {
    ArrayList<String[]> arr = new ArrayList<>();
    String[] a1 = {"dq answers 1"};
    String[] a2 = {"dq answers 2"};
    String[] a3 = {"dq answers 3"};
    String[] a4 = {"dq answers 4"};
    arr.add(a1);
    arr.add(a2);
    arr.add(a3);
    arr.add(a4);
    return arr;
  }

  @Override
  public boolean recExistsToday(String uid) {
    return true;
  }

  @Override
  public ArrayList<String[]> getRecs(String uid) {
    ArrayList<String[]> arr = new ArrayList<>();
    String[] a1 = {"recs 1"};
    String[] a2 = {"recs 2"};
    String[] a3 = {"recs 3"};
    String[] a4 = {"recs 4"};
    arr.add(a1);
    arr.add(a2);
    arr.add(a3);
    arr.add(a4);
    return arr;
  }

  @Override
  public int getUidIndex(String uid) {
    return 1;
  }

  @Override
  public String getIndexUid(int index) {
    return "one";
  }

  @Override
  public String getRandomUid(String requestUid, HashSet<Integer> used) {
    return "random uid";
  }

  @Override
  public int getTotalUsers() {
    return 10;
  }
}
