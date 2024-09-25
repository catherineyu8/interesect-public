package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.server.data_structures.DatabaseException;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;

public class Algorithm {

  private FirebaseUtilities fb; // pass in through handler class
  static final double MATCH_THRESHOLD = 0.1;

  public Algorithm(FirebaseUtilities fb) {
    this.fb = fb;
  }

  /**
   * Steps: 1. Choose random integer between 0 and the number of users in the uid map - find the
   * user that number corresponds to and add that number to a hashset 2. Check that user's percent
   * match with the inputted user - go through each of their initial quiz answers (TODO: decide?
   * maybe just daily quiz) - count matches (TODO: (review this): if users match ALL INITIAL
   * ANSWERS, immediately stop!) - go through one of their daily quiz answers and check if the
   * other's exists and if their answers are the same and count matches & total common answers - get
   * percentage match by looking at total (matches/total answers) 3. If the users' match percentage
   * is not within a certain threshold, repeat steps 1 & 2 (checking used user's hashset to make
   * sure we're choosing a new random user) until we do 4. Create recommendation - for each initial
   * quiz answer for the given user (for whom rec is for), check the recommended user. - (TODO: for
   * now) recommend the first answer that doesn't match
   */

  /**
   * computes a recommendation for the given user
   *
   * @param uidIn
   * @return
   */
  public String[] runAlgorithm(String uidIn) {

    HashSet<Integer> usersTried = new HashSet<>(); // make hashset to keep track of used users

    String uidTest = this.fb.getRandomUid(uidIn, usersTried);
    double matchScore = this.calculateMatchScore(uidIn, uidTest);
    int numUsers = this.fb.getTotalUsers();

    while (matchScore < MATCH_THRESHOLD) {
      usersTried.add(this.fb.getUidIndex(uidTest));

      // check if we've checked all users and not found anything
      if (usersTried.size() >= numUsers) {
        return new String[] {
          "Sorry, no recommendation could be calculated today", "rec user not found"
        };
      }

      uidTest = this.fb.getRandomUid(uidIn, usersTried);
      matchScore = this.calculateMatchScore(uidIn, uidTest);
    }

    return this.getRecommendation(uidIn, uidTest);
  }

  /**
   * Finds the first non-matching initial quiz answer and recommends that TODO: determine if this is
   * actually what we want to be doing
   *
   * @param uidIn
   * @param uidRec
   * @return
   */
  public String[] getRecommendation(String uidIn, String uidRec) {
    double numInitQuizQuestions = 4; // number of initial quiz questions
    String rec = "";

    // compute num matches in init quiz answers
    for (int i = 0; i < numInitQuizQuestions; i++) {
      String uidInAnswer = this.fb.retrieveIQAnswer(uidIn, i);
      System.out.println(uidIn + " i quiz answer " + i + ": " + uidInAnswer);
      String uidRecAnswer = this.fb.retrieveIQAnswer(uidRec, i);
      System.out.println(uidRec + " i quiz answer " + i + ": " + uidRecAnswer);
      if (!(uidInAnswer.equals(
          uidRecAnswer))) { // guarantee this happens because they can't have all the same answers
        rec = uidRecAnswer;
        break;
      }
    }

    // String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    return new String[] {rec, uidRec};
  }

  /**
   * Compute match score of 2 uids
   *
   * @param uidIn
   * @param uidTest return -1 if they are identical in initial quiz answers TODO: change to private?
   *     or maybe not for unit testing TODO: debug daily quiz stuff...it's not working right
   */
  public double calculateMatchScore(String uidIn, String uidTest) {
    double numInitQuizQuestions = 4; // number of initial quiz questions
    double totalQuestions = numInitQuizQuestions; // track total answers both users had
    double matches = 0;

    // compute num matches in init quiz answers
    for (int i = 0; i < numInitQuizQuestions; i++) {
      String uidInAnswer = this.fb.retrieveIQAnswer(uidIn, i);
      System.out.println(uidIn + " i quiz answer " + i + ": " + uidInAnswer);
      String uidTestAnswer = this.fb.retrieveIQAnswer(uidTest, i);
      System.out.println(uidTest + " i quiz answer " + i + ": " + uidTestAnswer);
      if (uidInAnswer.equals(uidTestAnswer)) {
        matches++;
      }
    }
    // check if they are 100% the same
    if (matches == numInitQuizQuestions) {
      return -1;
    }

    System.out.println("matches after init q " + matches);

    // compute num matches in daily quiz answers
    // go through uidIns answers and check if uidTest also has these answers
    ArrayList<String[]> uidInDQAnswers = this.fb.getDQAnswers(uidIn);
    if (uidInDQAnswers == null) { // an error occurred!
      // TODO: figure out what (if anything) to do here when firebase has an issue
      return -1;
    }
    if (uidInDQAnswers.isEmpty()) { // just go off the initial quiz
      return matches / totalQuestions;
    } else { // compare both users' daily quiz answers
      for (String[] ansPair : uidInDQAnswers) {
        try {
          String otherAns = this.fb.retrieveDQAnswer(uidTest, ansPair[1]);
          totalQuestions++;
          if (ansPair[0].equals(otherAns)) {
            matches++;
          }
        } catch (DatabaseException e) { // other user doesn't have this question answered
          continue;
        }
      }
    }
    return matches / totalQuestions;
  }

  /**
   * Returns an array of init quiz length of the PERCENTAGE of users that answered the same as
   * inputted uid Strings will be formatted as percentages
   *
   * @param uid
   * @return
   */
  public String[] getBasicStats(String uid) {
    double totalUsers = this.fb.getTotalUsers();
    double numInitQuizQuestions = 4;
    DecimalFormat f = new DecimalFormat("##.00");
    String[] toReturn = new String[(int) numInitQuizQuestions];
    for (int i = 0; i < numInitQuizQuestions; i++) {
      String answer = this.fb.retrieveIQAnswer(uid, i);
      double answerCount = this.fb.getAnswerCount("q" + i, answer).doubleValue();
      double percentage = (answerCount / totalUsers) * 100;
      toReturn[i] = f.format(percentage) + "%";
    }
    return toReturn;
  }
}
