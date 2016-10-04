import java.util.Set;
import java.util.HashSet;

public final class Dictionary {
  private Set<String> words = new HashSet<>();

  public Dictionary(String filename) {
    
  }

  public boolean isWord(String word) {
    return words.contains(word);
  }
}