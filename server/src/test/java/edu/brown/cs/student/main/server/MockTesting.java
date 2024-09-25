package edu.brown.cs.student.main.server;

import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.server.data_structures.DatabaseException;
import edu.brown.cs.student.main.server.storage.MockUtilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class MockTesting {
  MockUtilities m = new MockUtilities();

  // testing the mocked version of userExists
  @Test
  public void testUserExists() {
    assertEquals(true, m.userExists("uid"));
  }

  // testing the mocked version of getAnswerCoutn
  @Test
  public void testGetAnswerCount() {
    assertEquals(3L, m.getAnswerCount("label", "answer"));
  }

  // testing the mocked version of retrieveIQAnswer
  @Test
  public void testRetrieveIQAnswer() {
    assertEquals("IQ answer", m.retrieveIQAnswer("uid", 1));
  }

  // testing the mocked version of getallIQAnswers
  @Test
  public void testGetAllIQAnswer() {
    String[] arr = {"answer1", "answer2", "answer3", "answer4"};
    assertTrue(Arrays.equals(arr, m.getAllIQAnswers("uid")));
  }

  // testing the mocked version of retrieveDQAnswer
  @Test
  public void testRetrieveDQAnswer() throws DatabaseException {
    assertEquals("DQ answer", m.retrieveDQAnswer("uid", "date"));
  }

  // testing the mocked version of getTodaysDQAnswer
  @Test
  public void testGetTodaysDQAnswers() throws DatabaseException {
    ArrayList<String> arr = new ArrayList<>();
    arr.add("todays DQ answer 1");
    arr.add("todays DQ answer 2");
    arr.add("todays DQ answer 3");
    arr.add("todays sDQ answer 4");
    assertEquals(arr, m.getTodaysDQAnswers());
  }

  // testing the mocked version of getTodaysDQ
  @Test
  public void testGetTodaysDQ() {
    assertEquals("today's daily quiz question", m.getTodaysDQ());
  }

  // testing the mocked version of getDQAnswers
  @Test
  public void testGetDQAnswers() {
    ArrayList<String[]> arr = new ArrayList<>();
    String[] a1 = {"dq answers 1"};
    String[] a2 = {"dq answers 2"};
    String[] a3 = {"dq answers 3"};
    String[] a4 = {"dq answers 4"};
    arr.add(a1);
    arr.add(a2);
    arr.add(a3);
    arr.add(a4);
    ArrayList<String[]> answers = m.getDQAnswers("uid");
    assertTrue(Arrays.equals(arr.get(0), answers.get(0)));
    assertTrue(Arrays.equals(arr.get(1), answers.get(1)));
    assertTrue(Arrays.equals(arr.get(2), answers.get(2)));
    assertTrue(Arrays.equals(arr.get(3), answers.get(3)));
  }

  // testing the mocked version of recExistsToday
  @Test
  public void testRecExistsToday() {
    assertEquals(true, m.recExistsToday("uid"));
  }

  // testing the mocked version of getRecs
  @Test
  public void testGetRecs() {
    ArrayList<String[]> arr = new ArrayList<>();
    String[] a1 = {"recs 1"};
    String[] a2 = {"recs 2"};
    String[] a3 = {"recs 3"};
    String[] a4 = {"recs 4"};
    arr.add(a1);
    arr.add(a2);
    arr.add(a3);
    arr.add(a4);
    // assertEquals(arr, m.getRecs("uid"));
    ArrayList<String[]> recs = m.getRecs("uid");
    System.out.println("arr: " + arr.toString());
    ;
    System.out.println("recs: " + recs.toString());
    assertTrue(Arrays.equals(arr.get(0), recs.get(0)));
    assertTrue(Arrays.equals(arr.get(1), recs.get(1)));
    assertTrue(Arrays.equals(arr.get(2), recs.get(2)));
    assertTrue(Arrays.equals(arr.get(3), recs.get(3)));
  }

  // testing the mocked version of getUidIndex
  @Test
  public void testGetUIDIndex() {
    assertEquals(1, m.getUidIndex("uid"));
  }

  // testing the mocked version of getIndexUid
  @Test
  public void testGetIndexUID() {
    assertEquals("one", m.getIndexUid(1));
  }

  // testing the mocked version of getRandomUid
  @Test
  public void testGetRandomUid() {
    HashSet<Integer> h = new HashSet<>();
    assertEquals("random uid", m.getRandomUid("request", h));
  }

  // testing the mocked version of getTotalUsers
  @Test
  public void testGetTotalUsers() {
    assertEquals(10, m.getTotalUsers());
  }
}
