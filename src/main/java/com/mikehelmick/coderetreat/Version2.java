package com.mikehelmick.coderetreat;

import java.io.Console;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Version2 {

  static List<Character> newCharList(String string) {
    List<Character> chars = new ArrayList<>();
    for (Character ch : string.toCharArray()) {
      chars.add(ch);
    }
    return chars;
  }

  static String linearScan(String plate, Map<Integer, List<String>> dict, int maxWordLength) {
    String chars = "";
    for (char ch : plate.toLowerCase().toCharArray()) {
      if (Character.isLetter(ch)) {
        chars = chars + ch;
      }
    }
    System.out.println("For characters: " + chars);
    
    final List<Character> startingChars = newCharList(chars);
    int candidateCount = 0;
    for (int len = startingChars.size(); len <= maxWordLength; len++) {
      for (String candidateWord : dict.get(len)) {
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
          System.out.println("Considered " + candidateCount + " candidates.");
          System.out.println(" candidate: '" + candidateWord + "' - adds " + candidateChars.size());
          return candidateWord;
        }
      }
    }
    System.out.println("Considered " + candidateCount + " candidates.");
    
    return null;
  }

  public static void main( String[] args ) throws Exception {
    Dictionary dict = new Dictionary();
    System.out.println("Loaded dictionary of size " + dict.size());

    Map<Integer, List<String>> newDict = new HashMap<>();
    int longestWord = 0;
    for (String word : dict) {
      Integer len = word.length();
      if (!newDict.containsKey(len)) {
        newDict.put(len, new ArrayList<String>());
      }
      longestWord = Integer.max(longestWord, len);
      newDict.get(len).add(word);
    }

    Console c = System.console();
    String plate = c.readLine("Enter license plate: ");
    while (!plate.equals("")) {
      String shortest = linearScan(plate, newDict, longestWord);
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
