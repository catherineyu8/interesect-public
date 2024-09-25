package edu.brown.cs.student.main.server.storage;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import edu.brown.cs.student.main.server.data_structures.DatabaseException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class FirebaseUtilities implements StorageInterface {

  /**
   * access Firebase account for user's directory initialize firebase
   *
   * @throws IOException
   */
  private final int NUM_QUESTIONS = 3;

  public FirebaseUtilities() throws IOException {
    // Create /resources/ folder with firebase_config.json and
    // add your admin SDK from Firebase. see:
    // https://docs.google.com/document/d/10HuDtBWjkUoCaVj_A53IFm5torB_ws06fW3KYFZqKjc/edit?usp=sharing
    String workingDirectory = System.getProperty("user.dir");
    Path firebaseConfigPath =
        Paths.get(workingDirectory, "src", "main", "resources", "firebase_config.json");
    // ^-- if your /resources/firebase_config.json exists but is not found,
    // try printing workingDirectory and messing around with this path.

    FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath.toString());

    FirebaseOptions options =
        new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();

    FirebaseApp.initializeApp(options);
  }

  /**
   * Method to add a user to our database using their uid and initial quiz answers
   *
   * @param uid
   * @param quizAnswers
   */
  public void addUser(String uid, String[] quizAnswers) {
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference initQuiz =
        db.collection("users").document(uid).collection("initial_quiz_answers");
    CollectionReference dailyQuiz =
        db.collection("users").document(uid).collection("daily_quiz_answers");
    CollectionReference pastRecs = db.collection("users").document(uid).collection("past_recs");

    // add initial quiz answers
    for (int i = 0; i < quizAnswers.length; i++) {
      String doc_id = "q" + i;
      String answer = quizAnswers[i];
      Map<String, String> answerPair = new HashMap<>();
      answerPair.put("answer" + i, answer);
      initQuiz.document(doc_id).set(answerPair);
      addIQAnswerStorage(doc_id, quizAnswers[i]);
    }
    // need to put uid in index-to-uid map
    this.addUidIndex(uid);
  }

  /**
   * check if a user exists in our database by checking if a document with their uid containing iq
   * collection containing q0 exists - just checking their uid document wasn't working for some
   * weird reason (probably bc user documents only contain collections, not fields?) - this should
   * be fine though bc of the way we add users
   *
   * @param uid
   * @return
   */
  public boolean userExists(String uid) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference user_iq0 =
        db.collection("users").document(uid).collection("initial_quiz_answers").document("q0");
    ApiFuture<DocumentSnapshot> future = user_iq0.get();
    try {
      DocumentSnapshot document = future.get();
      if (document.exists()) {
        return true;
      }
      return false;
    } catch (Exception e) {
      // TODO: figure out what we actually want to do here!
      System.out.println(e.getMessage());
      return false;
    }
  }

  /**
   * access IQ Answer Storage and update based on user's answer
   *
   * @param quizLabel
   * @param answer
   */
  public void addIQAnswerStorage(String quizLabel, String answer) {
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference all_data = db.collection("iq_answers");
    DocumentReference answerData = db.collection("iq_answers").document(quizLabel);
    ApiFuture<DocumentSnapshot> future = answerData.get();
    try {
      DocumentSnapshot answerDoc = future.get();
      if (answerDoc.exists()) {
        if (answerDoc.contains(answer)) {
          Long updatedCount = (Long) answerDoc.get(answer) + Long.valueOf(1);
          answerData.update(answer, updatedCount);
        } else {
          answerData.update(answer, 1);
        }
      } else {
        Map<String, Long> answerCount = new HashMap<>();
        answerCount.put(answer, Long.valueOf(1));
        all_data.document(quizLabel).set(answerCount);
      }
    } catch (Exception e) {
      System.out.println("error occurred getting document from firebase: " + e.getMessage());
    }
  }

  /**
   * Return the count of the provided answer number
   *
   * @param quizLabel
   * @param answer
   * @return
   */
  public Long getAnswerCount(String quizLabel, String answer) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference answerData = db.collection("iq_answers").document(quizLabel);
    ApiFuture<DocumentSnapshot> future = answerData.get();
    try {
      DocumentSnapshot document = future.get();
      // [END firestore_data_get_as_map]
      return (document.exists()) ? (Long) document.getData().get(answer) : null;
    } catch (Exception e) {
      // TODO: figure out what we actually want to do here!
      System.out.println(e.getMessage());
      return Long.valueOf(-1);
    }
  }

  /**
   * Retrieves the initial quiz answer for the provided user with the given question num
   *
   * @param uid
   * @param num
   * @return
   */
  public String retrieveIQAnswer(String uid, int num) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference answerDoc =
        db.collection("users").document(uid).collection("initial_quiz_answers").document("q" + num);

    ApiFuture<DocumentSnapshot> future = answerDoc.get();
    try {
      DocumentSnapshot document = future.get();
      // [END firestore_data_get_as_map]
      return (document.exists()) ? (String) document.getData().get("answer" + num) : null;
    } catch (Exception e) {
      // TODO: figure out what we actually want to do here!
      return e.getMessage();
    }
  }

  /** get all of a user's IQ answers at once */
  public String[] getAllIQAnswers(String uid) {
    int numIQQuestions = 4;
    String[] answers = new String[numIQQuestions];
    for (int i = 0; i < numIQQuestions; i++) {
      answers[i] = this.retrieveIQAnswer(uid, i);
    }
    return answers;
  }

  /**
   * try to get a daily quiz answer for the inputted user/date catch all the exceptions separately
   * so database exception goes through!
   */
  public String retrieveDQAnswer(String uid, String date) throws DatabaseException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference answerDoc =
        db.collection("users").document(uid).collection("daily_quiz_answers").document(date);
    ApiFuture<DocumentSnapshot> future = answerDoc.get();
    try {
      DocumentSnapshot document = future.get();
      if (document.exists()) {
        return (String) document.getData().get("answer");
      } else {
        throw new DatabaseException("daily quiz not found");
      }
    } catch (ExecutionException e) {
      // TODO: figure out what we actually want to do here!
      return e.getMessage();
    } catch (InterruptedException e) {
      // TODO: figure out what we actually want to do here!
      return e.getMessage();
    }
  }

  /**
   * Method to add a daily quiz answer for a given user Use today's dateTime as the document id
   *
   * @param uid
   * @param answer
   */
  public void addDailyQuiz(String uid, String answer) {
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference dailyQuiz =
        db.collection("users").document(uid).collection("daily_quiz_answers");
    String date = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
    Map<String, String> answerPair = new HashMap<>();
    answerPair.put("answer", answer);
    dailyQuiz.document(date).set(answerPair);
  }

  /**
   * stores first generated daily quiz answers for the day
   *
   * @param answers
   */
  public void storeDailyQuizAnswers(ArrayList<String> answers, String question) {
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference dq_storage = db.collection("dq_answers");
    String today = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
    Map<String, Object> todays_answers = new HashMap<>();
    todays_answers.put("answers", answers);
    todays_answers.put("question", question);
    dq_storage.document(today).set(todays_answers);
  }

  /**
   * Check if today has DQ answers already, if so return them If not, returns a Database Exception
   */
  public ArrayList<String> getTodaysDQAnswers() throws DatabaseException {
    Firestore db = FirestoreClient.getFirestore();
    String today = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
    DocumentReference dq = db.collection("dq_answers").document(today);
    ApiFuture<DocumentSnapshot> dqRef = dq.get();
    try {
      DocumentSnapshot document = dqRef.get();
      if (document.exists()) {
        return (ArrayList<String>) document.get("answers");
      } else {
        throw new DatabaseException("no daily quiz answers yet today");
      }
    } catch (ExecutionException e) { // catch individual exceptions to not mask DBException!
      System.out.println(e.getMessage());
    } catch (InterruptedException e) {
      System.out.println(e.getMessage());
    }
    return null;
  }

  /**
   * check database for today's daily quiz question and return if found - will never be called when
   * it doesn't exist anyway
   *
   * @return
   * @throws DatabaseException
   */
  public String getTodaysDQ() {
    Firestore db = FirestoreClient.getFirestore();
    String today = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
    DocumentReference dq = db.collection("dq_answers").document(today);
    ApiFuture<DocumentSnapshot> dqRef = dq.get();
    try {
      DocumentSnapshot document = dqRef.get();
      if (document.exists()) {
        return (String) document.get("question");
      }
    } catch (ExecutionException e) { // catch individual exceptions to not mask DBException!
      System.out.println(e.getMessage());
    } catch (InterruptedException e) {
      System.out.println(e.getMessage());
    }
    return null;
  }

  /**
   * Method to add a daily quiz answer with the date mocked (so we can add a bunch at once)
   *
   * @param uid
   * @param dateAnswer
   */
  public void addDailyQuizMock(String uid, String[] dateAnswer) {
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference dailyQuiz =
        db.collection("users").document(uid).collection("daily_quiz_answers");
    Map<String, String> answerPair = new HashMap<>();
    answerPair.put("answer", dateAnswer[1]);
    dailyQuiz.document(dateAnswer[0]).set(answerPair);
  }

  /** Get all of a user's daily quiz answers as string[]? */
  public ArrayList<String[]> getDQAnswers(String uid) {
    /**
     * get list of documents in collection for each document put fields back in rec objecct and add
     * to list to return
     */
    Firestore db = FirestoreClient.getFirestore();
    ApiFuture<QuerySnapshot> recsRef =
        db.collection("users").document(uid).collection("daily_quiz_answers").get();
    try {
      List<QueryDocumentSnapshot> documents = recsRef.get().getDocuments();
      ArrayList<String[]> answers = new ArrayList<>();
      for (QueryDocumentSnapshot document : documents) {
        String[] dateAnswerPair = new String[2];
        Map<String, Object> docData = document.getData();
        dateAnswerPair[0] = document.getId();
        dateAnswerPair[1] = (String) docData.get("answer");
        answers.add(dateAnswerPair);
      }
      return answers;
    } catch (Exception e) {
      // TODO: figure out what we actually want to do here!
      System.out.println(e.getMessage());
    }
    return null;
  }

  /**
   * Method that adds a recommendation (in the form of a string) to the previous recommendations
   * list in db. TODO: THIS METHOD CURRENTLY JUST REPLACES WHATEVER IS IN THE LIST THOUGH, I THINK?
   *
   * @param uid
   * @param rec
   */
  public void addRecString(String uid, String rec, String recUid) {
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference recsRef = db.collection("users").document(uid).collection("past_recs");
    String date = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
    Map<String, String> recData = new HashMap<>();
    recData.put("rec_uid", recUid);
    recData.put("recommendation", rec);
    recData.put("date", date);
    recsRef.document(date).set(recData);
  }

  /**
   * For a given user, check if they already have a recommendation in the database for today
   *
   * @param uid
   * @return
   */
  public boolean recExistsToday(String uid) {
    Firestore db = FirestoreClient.getFirestore();
    String dateToday = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
    DocumentReference docRef =
        db.collection("users").document(uid).collection("past_recs").document(dateToday);
    // asynchronously retrieve the document
    ApiFuture<DocumentSnapshot> future = docRef.get();
    try {
      DocumentSnapshot document = future.get(); // do this to check if doc exists
      if (document.exists()) { // we don't want to recommend
        System.out.println("date exists");
        return true;
      } else { // today's rec doesn't exist yet
        return false;
      }
    } catch (Exception e) {
      // TODO: figure out what we actually want to do here!
      System.out.println(e.getMessage());
      return false;
    }
  }

  /**
   * method to retrieve list of recommendations from database (format may have to change based on
   * what frontend needs)
   */
  public ArrayList<String[]> getRecs(String uid) {
    /**
     * get list of documents in collection for each document put fields back in rec objecct and add
     * to list to return
     */
    Firestore db = FirestoreClient.getFirestore();
    ApiFuture<QuerySnapshot> recsRef =
        db.collection("users").document(uid).collection("past_recs").get();
    try {
      List<QueryDocumentSnapshot> documents = recsRef.get().getDocuments();
      ArrayList<String[]> recs = new ArrayList<>();
      for (QueryDocumentSnapshot document : documents) {
        String[] recPair = new String[2];
        Map<String, Object> docData = document.getData();
        recPair[0] = (String) docData.get("date");
        recPair[1] = (String) docData.get("recommendation");
        recs.add(recPair);
      }
      return recs;
    } catch (Exception e) {
      // TODO: figure out what we actually want to do here!
      System.out.println(e.getMessage());
    }
    return new ArrayList<>();
  }

  /**
   * Method to add a statistic to list of other statistics stored in db
   *
   * @param uid
   * @param
   */
  public void addStat(String uid, String rec) {
    // TODO: implement
  }

  /**
   * Method to add a user's uid to the UID index storage collection If the document exists, will
   * update, if not, creates the doc
   *
   * @param uid
   */
  public void addUidIndex(String uid) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("index_map").document("indices");
    // asynchronously retrieve the document
    ApiFuture<DocumentSnapshot> future = docRef.get();
    try {
      DocumentSnapshot document = future.get(); // do this to check if doc exists
      if (document.exists()) { // just update document with new uid
        docRef.update("uid_list", FieldValue.arrayUnion(uid));
      } else { // must create document with set
        ArrayList<String> uids = new ArrayList<>();
        uids.add(uid);
        Map<String, ArrayList<String>> dumbHashmap = new HashMap<>();
        dumbHashmap.put("uid_list", uids);
        docRef.set(dumbHashmap, SetOptions.mergeFields(List.of("uid_list")));
      }
    } catch (Exception e) {
      // TODO: figure out what we actually want to do here!
      System.out.println(e.getMessage());
    }
  }

  /**
   * get the uid index for a string uid
   *
   * @param uid
   * @return
   */
  public int getUidIndex(String uid) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("index_map").document("indices");
    // asynchronously retrieve the document
    ApiFuture<DocumentSnapshot> future = docRef.get();
    try {
      DocumentSnapshot document = future.get(); // do this to check if doc exists
      ArrayList<String> users = (ArrayList<String>) document.get("uid_list");
      return users.indexOf(uid);

    } catch (Exception e) {
      // TODO: figure out what we actually want to do here!
      System.out.println(e.getMessage());
    }
    return -1;
  }

  /**
   * get uid for provided index
   *
   * @param index
   * @return
   */
  public String getIndexUid(int index) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("index_map").document("indices");
    // asynchronously retrieve the document
    ApiFuture<DocumentSnapshot> future = docRef.get();
    try {
      DocumentSnapshot document = future.get(); // do this to check if doc exists
      ArrayList<String> users = (ArrayList<String>) document.get("uid_list");
      return users.get(index);

    } catch (Exception e) {
      // TODO: figure out what we actually want to do here!
      return e.getMessage();
    }
  }

  /**
   * get the user corresponding to a given index
   *
   * @param requestUid
   * @param used
   * @return
   */
  public String getRandomUid(String requestUid, HashSet<Integer> used) {
    Firestore db = FirestoreClient.getFirestore();
    // add user's uid index to hashset if it's not there
    int userUid = this.getUidIndex(requestUid);
    if (!used.contains(userUid)) {
      used.add(userUid);
    }
    // get length of user list
    int numUsers = this.getTotalUsers();
    Random random = new Random();
    int index = random.nextInt(numUsers);
    if (!used.isEmpty()) { // check if hashset is empty (users that have been tried)
      while (used.contains(index)) { // find new random number if we've already seen this one
        index = random.nextInt(numUsers);
      }
    }
    return this.getIndexUid(index);
  }

  /**
   * method to get total users in database
   *
   * @return
   */
  public int getTotalUsers() {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("index_map").document("indices");
    // asynchronously retrieve the document
    ApiFuture<DocumentSnapshot> future = docRef.get();
    try {
      DocumentSnapshot document = future.get(); // do this to check if doc exists
      ArrayList<String> users = (ArrayList<String>) document.get("uid_list");
      return users.size();

    } catch (Exception e) {
      // TODO: figure out what we actually want to do here!
      System.out.println(e.getMessage());
    }
    return -1;
  }

  /** should clear entire database, doesn't rlly work tho. */
  public void clearEverything() {
    Firestore db = FirestoreClient.getFirestore();

    try {
      CollectionReference users = db.collection("users");
      deleteCollection(users);

      CollectionReference indices = db.collection("index_map");
      deleteCollection(indices);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
  }

  /** Method to populate database with fake answers */
  public void mockDatabase() {
    // create initial quiz answers
    String[] catherine = new String[] {"indie", "sit in a coffee shop", "dancing", "clean girl"};
    String[] isabelle = new String[] {"indie", "hiking", "climbing", "granola"};
    String[] tom = new String[] {"rock", "stay in bed and watch tv", "cricket", "preppy"};
    String[] tim = new String[] {"rock", "stay in bed and watch tv", "cricket", "skater boy"};
    String[] kathi = new String[] {"kathi", "country", "art", "skater boy"};
    String[] nick = new String[] {"folk", "art", "pickleball", "classy"};
    String[] kate = new String[] {"pop", "go out to dinner", "rugby", "granola"};
    String[] niko = new String[] {"r&b", "go out to dinner", "rugby", "preppy"};
    String[] spike = new String[] {"rap", "stay in bed and watch tv", "pickleball", "costco"};
    String[] amie = new String[] {"indie", "sit in a coffee shop", "dancing", "clean girl"};
    String[] ana = new String[] {"indie", "hiking", "swimming", "athleisure"};
    String[] morgan = new String[] {"pop", "stay in bed and watch tv", "swimming", "athleisure"};
    String[] billy = new String[] {"rap", "go to a museum", "badminton", "granola"};

    // add users to database with initial quiz answers
    this.addUser("catherine", catherine);
    this.addUser("isabelle", isabelle);
    this.addUser("tom", tom);
    this.addUser("tim", tim);
    this.addUser("kathi", kathi);
    this.addUser("nick", nick);
    this.addUser("kate", kate);
    this.addUser("niko", niko);
    this.addUser("spike", spike);
    this.addUser("amie", amie);
    this.addUser("ana", ana);
    this.addUser("morgan", morgan);
    this.addUser("billy", billy);

    // create some arbitrary daily quiz answers with fake dates
    String[] a1 = new String[] {"01-01-2024", "1"};
    String[] a2 = new String[] {"01-01-2024", "2"};
    String[] a3 = new String[] {"01-01-2024", "3"};
    String[] a4 = new String[] {"01-01-2024", "4"};
    String[][] As = new String[][] {a1, a2, a3, a4};

    String[] b1 = new String[] {"01-02-2024", "1"};
    String[] b2 = new String[] {"01-02-2024", "2"};
    String[] b3 = new String[] {"01-02-2024", "3"};
    String[] b4 = new String[] {"01-02-2024", "4"};
    String[][] Bs = new String[][] {b1, b2, b3, b4};

    String[] c1 = new String[] {"01-03-2024", "1"};
    String[] c2 = new String[] {"01-03-2024", "2"};
    String[] c3 = new String[] {"01-03-2024", "3"};
    String[] c4 = new String[] {"01-03-2024", "4"};
    String[][] Cs = new String[][] {c1, c2, c3, c4};

    String[] d1 = new String[] {"01-04-2024", "1"};
    String[] d2 = new String[] {"01-04-2024", "2"};
    String[] d3 = new String[] {"01-04-2024", "3"};
    String[] d4 = new String[] {"01-04-2024", "4"};
    String[][] Ds = new String[][] {d1, d2, d3, d4};

    String[] e1 = new String[] {"01-05-2024", "1"};
    String[] e2 = new String[] {"01-05-2024", "2"};
    String[] e3 = new String[] {"01-05-2024", "3"};
    String[] e4 = new String[] {"01-05-2024", "4"};
    String[][] Es = new String[][] {e1, e2, e3, e4};

    String[][][] all = new String[][][] {As, Bs, Cs, Ds, Es};

    // add daily quiz answers for some users
    Random random = new Random();

    // catherine
    for (int i = 0; i < 5; i++) {
      int randomNumber = random.nextInt(4);
      this.addDailyQuizMock("catherine", all[i][randomNumber]);
    }

    // isabelle
    for (int i = 0; i < 5; i++) {
      int randomNumber = random.nextInt(4);
      this.addDailyQuizMock("isabelle", all[i][randomNumber]);
    }

    // tom
    for (int i = 0; i < 5; i++) {
      int randomNumber = random.nextInt(4);
      this.addDailyQuizMock("tom", all[i][randomNumber]);
    }

    // tim
    for (int i = 0; i < 5; i++) {
      int randomNumber = random.nextInt(4);
      this.addDailyQuizMock("tim", all[i][randomNumber]);
    }

    // kathi -> only do 2 days
    for (int i = 0; i < 2; i++) {
      int randomNumber = random.nextInt(4);
      this.addDailyQuizMock("kathi", all[i][randomNumber]);
    }

    // nick -> only do 3 days
    for (int i = 0; i < 3; i++) {
      int randomNumber = random.nextInt(4);
      this.addDailyQuizMock("nick", all[i][randomNumber]);
    }

    // kate
    for (int i = 0; i < 5; i++) {
      int randomNumber = random.nextInt(4);
      this.addDailyQuizMock("kate", all[i][randomNumber]);
    }

    // niko --> 4 days
    for (int i = 0; i < 4; i++) {
      int randomNumber = random.nextInt(4);
      this.addDailyQuizMock("niko", all[i][randomNumber]);
    }

    // amie
    for (int i = 0; i < 5; i++) {
      int randomNumber = random.nextInt(4);
      this.addDailyQuizMock("amie", all[i][randomNumber]);
    }

    // ana
    for (int i = 0; i < 5; i++) {
      int randomNumber = random.nextInt(4);
      this.addDailyQuizMock("ana", all[i][randomNumber]);
    }

    // morgan
    for (int i = 0; i < 5; i++) {
      int randomNumber = random.nextInt(4);
      this.addDailyQuizMock("morgan", all[i][randomNumber]);
    }

    // billy -> just do 1 day
    int randomNumber = random.nextInt(4);
    this.addDailyQuizMock("billy", all[0][randomNumber]);

    // spike -> no days
  }

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

  /**
   * adds document with given information to a user's firebase database
   *
   * @param uid
   * @param collection_id
   * @param doc_id
   * @param data
   * @throws IllegalArgumentException
   */
  public void addDocument(String uid, String collection_id, String doc_id, Map<String, Object> data)
      throws IllegalArgumentException {
    if (uid == null || collection_id == null || doc_id == null || data == null) {
      throw new IllegalArgumentException(
          "addDocument: uid, collection_id, doc_id, or data cannot be null");
    }
    // adds a new document 'doc_name' to colleciton 'collection_id' for user 'uid'
    // with data payload 'data'.

    Firestore db = FirestoreClient.getFirestore();
    // 1: Get a ref to the collection that you created
    CollectionReference collectionRef =
        db.collection("users").document(uid).collection(collection_id);

    // 2: Write data to the collection ref
    collectionRef.document(doc_id).set(data);
  }

  // clears the collections inside of a specific user.

  public void clearUser(String uid) throws IllegalArgumentException {
    if (uid == null) {
      throw new IllegalArgumentException("removeUser: uid cannot be null");
    }
    try {
      // removes all data for user 'uid'
      Firestore db = FirestoreClient.getFirestore();
      // 1: Get a ref to the user document
      DocumentReference userDoc = db.collection("users").document(uid);
      // 2: Delete the user document
      deleteDocument(userDoc);
    } catch (Exception e) {
      System.err.println("Error removing user : " + uid);
      System.err.println(e.getMessage());
    }
  }

  /**
   * deletes the given document in a user's firestore database
   *
   * @param doc
   */
  private void deleteDocument(DocumentReference doc) {
    // for each subcollection, run deleteCollection()
    Iterable<CollectionReference> collections = doc.listCollections();
    for (CollectionReference collection : collections) {
      deleteCollection(collection);
    }
    // then delete the document
    doc.delete();
  }

  // recursively removes all the documents and collections inside a collection
  // https://firebase.google.com/docs/firestore/manage-data/delete-data#collections
  private void deleteCollection(CollectionReference collection) {
    try {

      // get all documents in the collection
      ApiFuture<QuerySnapshot> future = collection.get();
      List<QueryDocumentSnapshot> documents = future.get().getDocuments();

      // delete each document
      for (QueryDocumentSnapshot doc : documents) {
        deleteDocument(doc.getReference());
      }

      // NOTE: the query to documents may be arbitrarily large. A more robust
      // solution would involve batching the collection.get() call.
    } catch (Exception e) {
      System.err.println("Error deleting collection : " + e.getMessage());
    }
  }
}
