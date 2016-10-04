package com.mikehelmick.coderetreat;

import com.google.common.io.Resources;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public final class Dictionary implements Iterable<String> {
  private static final String ALLOWED_CHARS = "[a-z]+";
  
  private Set<String> words = new TreeSet<>();

  // Throwing IOException from this constructor is poor practice, just for demo.
  public Dictionary() throws IOException {
    List<String> allWords = Resources.readLines(
        Resources.getResource(getClass(), "/words.txt"), Charset.defaultCharset());
    for (String word : allWords) {
      word = word.toLowerCase();
      if (word.matches(ALLOWED_CHARS)) {
        words.add(word);
      }
    }
  }

  public Iterator<String> iterator() {
    return words.iterator();
  }

  public int size() {
    return words.size();
  }

  public boolean isWord(String word) {
    return words.contains(word);
  }
}