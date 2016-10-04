package com.mikehelmick.coderetreat;

import junit.framework.TestCase;

public class DictionaryTest extends TestCase {
  public void testDictionary() throws Exception {
    Dictionary dict = new Dictionary();
    assertTrue(dict.isWord("moose"));
    // Something we know is in the file, but should be filtered out.
    assertFalse(dict.isWord("'d"));
  }   
}
