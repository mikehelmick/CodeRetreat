package com.mikehelmick.coderetreat;

import java.io.Console;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class Version2 {
  static char[] ALPHABET = {
      'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
      'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
      'y', 'z'};

  static List<Character> newCharList(String string) {
    List<Character> chars = new ArrayList<>();
    for (Character ch : string.toCharArray()) {
      chars.add(ch);
    }
    return chars;
  }

  static String linearScan(String chars, Map<Integer, Set<String>> dict, int maxLen) {
    final List<Character> startingChars = newCharList(chars);
    
    int candidateCount = 0;
    for (int length = startingChars.size(); length <= maxLen; length++) {
      Set<String> wordsOfThisLength = dict.get(length);
      if (wordsOfThisLength == null) {
        continue;
      }
      for (String candidateWord : wordsOfThisLength) {
        candidateCount++;
        List<Character> candidateChars = newCharList(candidateWord);
      
        boolean allCharsFound = true;
        for (Character ch : startingChars) {
          if (!candidateChars.remove(ch)) {
            allCharsFound = false;
            break;
          }
        }
        if (allCharsFound) {
          System.out.println(" candidate: '" + candidateWord + "' - adds " + candidateChars.size());
          System.out.println("Considered " + candidateCount + " candidates.");
          return candidateWord;
        }

      }
    }
    System.out.println("Considered " + candidateCount + " candidates.");  
    return null;
  }

  public static void main( String[] args ) throws Exception {
    Dictionary dict = new Dictionary();

    int maxLen = 0;
    Map<Integer, Set<String>> wordsByLength = new HashMap<>();
    for (String word : dict) {
      int len = word.length();
      maxLen = Integer.max(len, maxLen);
      if (!wordsByLength.containsKey(len)) {
        wordsByLength.put(len, new TreeSet<String>());
      }
      wordsByLength.get(len).add(word);
    }
 

    System.out.println("Loaded dictionary of size " + dict.size());

    Console c = System.console();
    String plate = c.readLine("Enter license plate: ");
    while (!plate.equals("")) {
      String chars = "";
      for (char ch : plate.toLowerCase().toCharArray()) {
        if (Character.isLetter(ch)) {
          chars = chars + ch;
        }
      }
      System.out.println("For characters: " + chars);

      String shortest = linearScan(chars, wordsByLength, maxLen);
      if (shortest == null) {
        System.out.println("I couldn't find a word.");
      } else {
        System.out.println("Shortest word: " + shortest);
      }
      
      plate = c.readLine("Enter license plate: ");
    }
    System.out.println("goodbye");
  }
}
