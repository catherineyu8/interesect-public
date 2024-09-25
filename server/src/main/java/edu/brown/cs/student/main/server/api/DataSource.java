package edu.brown.cs.student.main.server.api;

import java.util.ArrayList;

/**
 * DataSource interface implemented by our different api fetching data sources so that we can use
 * them for our daily quiz interchangeably
 */
public interface DataSource {
  ArrayList<String> getAnswers(int numAnswers) throws DatasourceException, IllegalArgumentException;
}
