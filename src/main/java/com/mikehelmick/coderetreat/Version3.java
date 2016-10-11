package com.mikehelmick.coderetreat;

import java.io.Console;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


public class Version3 {
  // A constant to repsent no word, null doesn't work in
  // the concurrent hash map.
  private static final String NO_WORD = "NOWORD";

  // An iterator that enumerates all valid letter combinations
  // from AA to ZZZZZ. This does not de-duplicate.
  static class PlateGenerator implements Iterator<String> {
    private String position;

    PlateGenerator() {
      position = "" + 'A' + "" + ((char) ('A' - 1));
    }

    @Override
    public boolean hasNext() {
      return !position.equals("ZZZZZ"); 
    }

    @Override
    public String next() {
      if (!hasNext()) {
        throw new NoSuchElementException("Iterator has run out.");
      }
      // Convert corrent position to an array, and add 1 from right
      // to left, accounting for carryover at 'Z'. It is possible
      // that we need to add a new diget.
      boolean overflow = false;
      char[] chars = position.toCharArray();
      for (int i = chars.length - 1; i >= 0; i--) {
        if (chars[i] == 'Z') {
          chars[i] = 'A';
          overflow = i == 0;
        } else {
          chars[i] = (char) (chars[i] + 1);
          break;
        }
      }
      if (overflow) {
        position = "A" + new String(chars);
      } else {
        position = new String(chars); 
      }
      return position;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("not implemented");
    }
  }

  static List<Character> newCharList(String string) {
    List<Character> chars = new ArrayList<>();
    for (Character ch : string.toCharArray()) {
      chars.add(ch);
    }
    return chars;
  }

  static String linearScan(String chars, Map<Integer, List<String>> dict, int longest) {
    final List<Character> startingChars = newCharList(chars);
    
    for (int i = chars.length(); i <= longest; i++) {
      List<String> level = dict.get(i);
      if (level == null) {
        continue;
      }
      for (String candidateWord : level) {
        List<Character> candidateChars = newCharList(candidateWord);
      
        boolean allCharsFound = true;
        for (Character ch : startingChars) {
          if (!candidateChars.remove(ch)) {
            allCharsFound = false;
            break;
          }
        }
        if (allCharsFound) {
          return candidateWord;
        }
      }
    }
    return NO_WORD;
  }

  private static class Worker implements Runnable {
    private final BlockingQueue<String> workQueue;
    private final Map<String, String> index;
    private final Map<Integer, List<String>> dictionary;
    private final int longestWord;
    private final CountDownLatch latch;
    private final int workerNum;

    Worker(BlockingQueue<String> workQueue, Map<String, String> index,
        Map<Integer, List<String>> dictionary, int longestWord,
        CountDownLatch latch, int workerNum) {
      this.workQueue = workQueue;
      this.index = index;
      this.dictionary = dictionary;
      this.longestWord = longestWord;
      this.latch = latch;
      this.workerNum = workerNum;
    }

    @Override
    public void run() {
      try {
        // Take a plate from the queue and calculate the word.
        String plate = workQueue.poll(1, TimeUnit.SECONDS);
        while (plate != null) {
          String shortestWord = linearScan(plate, dictionary, longestWord);
          index.put(plate, shortestWord);
          plate = workQueue.poll(1, TimeUnit.SECONDS);
        }
      } catch (InterruptedException iex) {
        System.err.println("Interrupted.");
        // Not robust, but whatever.
        System.exit(1);
      }

      latch.countDown();
      System.out.println("Worker " + workerNum + " finished.");
    }
  }

  public static void main( String[] args ) throws Exception {
    Dictionary dict = new Dictionary();
    System.out.println("Loaded dictionary of size " + dict.size());

    int longestWord = 0;
    Map<Integer, List<String>> dictionary = new HashMap<>(30);
    for (String word : dict) {
      Integer len = word.length();
      if (!dictionary.containsKey(len)) {
        dictionary.put(len, new ArrayList<String>());
      }
      dictionary.get(len).add(word);
      longestWord = Integer.max(longestWord, len);
    }

    System.out.println("Building index");
    // Pre-allocate enough room.
    Map<String, String> index = new ConcurrentHashMap<>(1500000);
    BlockingQueue<String> workQueue = new LinkedBlockingQueue<>();
    Iterator<String> plateIter = new PlateGenerator();
    System.out.println(" - Enumerating license plates and de-duping.");
    int count = 0;
    while (plateIter.hasNext()) {
      count++;
      final String source = plateIter.next().toLowerCase();
      char[] chars = source.toCharArray();
      Arrays.sort(chars);
      String plate = new String(chars);
      if (!index.containsKey(plate)) {
        index.put(plate, NO_WORD);
        workQueue.offer(plate);
      }
      if (count % 100000 == 0) {
        System.out.print(".");
      }
    }
    System.out.println("\n - Done. " + workQueue.size() + " license plates generated.");

    System.out.println("Building index concurrently");
    ExecutorService executor = Executors.newCachedThreadPool();
    int workers = 8;
    final CountDownLatch latch = new CountDownLatch(workers);
    for (int i = 0; i < workers; i++) {
      System.out.println(" - Starting task #" + i);
      executor.submit(new Worker(workQueue, index, dictionary, longestWord, latch, i));
    }
    while (workQueue.size() > 3000) {
      Thread.sleep(5000);
      System.out.println("Plates remaining: " + workQueue.size());
    }
    latch.await();
    System.out.println("Index has been built.");
    System.out.println("Index size: " + index.size());

    Console c = System.console();
    String plate = c.readLine("Enter license plate: ");
    while (!plate.equals("")) {
      String justChars = "";
      for (char ch : plate.toLowerCase().toCharArray()) {
        if (Character.isLetter(ch)) {
          justChars = justChars + ch;
        }
      }
      // Sorting the array is very important, otherse it won't match index.
      char[] chars = justChars.toCharArray();
      Arrays.sort(chars);
      plate = new String(chars);
      System.out.println("For characters: " + plate);

      String shortest = index.get(plate);
      if (shortest == null || shortest.equals(NO_WORD)) {
        System.out.println("I couldn't find a word.");
      } else {
        System.out.println("Shortest word: " + shortest);
      }
      
      plate = c.readLine("Enter license plate: ");
    }
    System.out.println("goodbye");
  }
}
