package com.mikehelmick.coderetreat;

import java.io.Console;
import java.util.List;
import java.util.ArrayList;

public class Version1 {
  // Convert a strong to a mutable list of characters.
  static List<Character> newCharList(String string) {
    List<Character> chars = new ArrayList<>();
    for (Character ch : string.toCharArray()) {
      chars.add(ch);
    }
    return chars;
  }

  // Scan the dictionary, seeing if the suggested plate maps
  // to that word. Keep searching until you find a word that
  // adds zero characters, or you reach the end of the dictionary.
  static String linearScan(String plate, Dictionary dict) {
    String chars = "";
    for (char ch : plate.toLowerCase().toCharArray()) {
      if (Character.isLetter(ch)) {
        chars = chars + ch;
      }
    }
    if (chars.equals("")) {
      throw new IllegalArgumentException("");
    }
    System.out.println("For characters: " + chars);
    
    final List<Character> startingChars = newCharList(chars);
    int charsAdded = Integer.MAX_VALUE;
    String originWord = null;
    int candidateCount = 0;
    for (String candidateWord : dict) {
      candidateCount++;
      List<Character> candidateChars = newCharList(candidateWord);
      
      boolean allCharsFound = true;
      for (Character ch : startingChars) {
        if (!candidateChars.remove(ch)) {
          allCharsFound = false;
          break;
        }
      }
      if (!allCharsFound) {
        continue;
      }
      System.out.println(" candidate: '" + candidateWord + "' - adds " + candidateChars.size());
 
      if (candidateChars.size() < charsAdded) {
        charsAdded = candidateChars.size();
        originWord = candidateWord;
        if (charsAdded == 0) {
          // we can't do better than this.
          break;
        }
      }
    }
    System.out.println("Considered " + candidateCount + " candidates.");
    
    return originWord;
  }

  public static void main( String[] args ) throws Exception {
    Dictionary dict = new Dictionary();
    System.out.println("Loaded dictionary of size " + dict.size());

    Console c = System.console();
    String plate = c.readLine("Enter license plate: ");
    while (!plate.equals("")) {
      String shortest = linearScan(plate, dict);
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
