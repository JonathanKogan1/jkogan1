package Projects;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class wordle {
    static Hashtable<Character, Integer> chars = new Hashtable<>();
    static Hashtable<Integer, Boolean> correctPositions = new Hashtable<>();
    static Hashtable<String, Boolean> alreadyGuessed = new Hashtable<>();
    static Hashtable<Character, Boolean> not = new Hashtable<>();
    static String[] words = new String[2315];
    static List<String> validWords = new ArrayList<>();
    static ArrayList<word> wordsGuessed = new ArrayList<>();
    static String bestType;

    public static void main(String[] args) throws FileNotFoundException {
        Scanner inp = new Scanner(new File("Projects/words.in"));
        words = new String[2315];
        for (int i = 0; i < 2315; i++) {
            words[i] = inp.next();
            validWords.add(words[i]);
            for (int j = 0; j < 5; j++) {
                if (chars.get(words[i].charAt(j)) == null) {
                    chars.put(words[i].charAt(j), 1);
                } else {
                    chars.put(words[i].charAt(j), chars.get(words[i].charAt(j)) + 1);
                }
            }
        }
        double cnt = 0;


        //booty
        /*
        guess("stare", "01001");
        System.out.println(getNextWord(wordsGuessed));
        System.out.println(validWords);
        System.out.println(validWords.size());
        guess("fetid", "02100");
        System.out.println(getNextWord(wordsGuessed));
        System.out.println(validWords);
        System.out.println(validWords.size());
        guess("tempo", "22000");
        System.out.println(getNextWord(wordsGuessed));
        System.out.println(validWords);
        System.out.println(validWords.size());
        
         */

    }

    public static String compare(String a, String b) {
        String cnt = "";
        Hashtable<Integer, Boolean> counted = new Hashtable<>();
        for (int i = 0; i < 5; i++) {
            if (a.charAt(i) == b.charAt(i)) {
                cnt += ("2");
                counted.put(i, true);
            } else {
                if (!b.contains(a.charAt(i) + "")) {
                    cnt += ("0");
                } else {
                    boolean found = false;
                    for (int j = 0; j < 5; j++) {
                        if (b.charAt(j) == a.charAt(i) && !found && counted.get(j) == null) {
                            cnt += ("1");
                            found = true;
                            counted.put(j, true);
                        }
                    }
                    if (!found) {
                        cnt += "0";
                    }
                }
            }

        }
        return cnt;
    }

    public static int getNum(String b) {
        String current = "stare";
        guess("stare", compare("stare", b));
        int cnt = 1;
        while (!current.equals(b)) {
            current = getNextWord(wordsGuessed);
            guess(current, compare(current, b));
            cnt++;
            if (current.equals(b)) {
                break;
            }
        }
        alreadyGuessed.clear();
        not.clear();
        validWords = Arrays.asList(words);
        wordsGuessed.clear();
        correctPositions.clear();
        return cnt;
    }


    public static int evaluate(String a) {
        int cnt = 0;
        Hashtable<Character, Boolean> b = new Hashtable<>();
        Hashtable<Character, Integer> newWords = new Hashtable<>();
        for (int i = 0; i < validWords.size(); i++) {
            for (int j = 0; j < 5; j++) {
                if (newWords.get(validWords.get(i).charAt(j)) == null) {
                    newWords.put(validWords.get(i).charAt(j), 1);
                } else {
                    newWords.put(validWords.get(i).charAt(j), newWords.get(validWords.get(i).charAt(j)) + 1);
                }
            }
        }
        for (int i = 0; i < 5; i++) {
            if (b.get(a.charAt(i)) == null) {
                cnt += newWords.get(a.charAt(i));
                b.put(a.charAt(i), true);
            }
        }
        return cnt;
    }

    public static word guess(String a, String type) {
        ArrayList<Character> allLettersInWord = new ArrayList<>();
        Hashtable<Character, Integer> inTheRightPosition = new Hashtable<>();
        ArrayList<Character> allLettersInTheRightPosition = new ArrayList<>();
        Hashtable<Character, Integer> lettersInWord = new Hashtable<>();
        for (int i = 0; i < 5; i++) {
            if (type.charAt(i) == '0') {
                not.put(a.charAt(i), true);
            }
            if (type.charAt(i) == '1') {
                allLettersInWord.add(a.charAt(i));
                lettersInWord.put(a.charAt(i), i);
            }
            if (type.charAt(i) == '2') {
                inTheRightPosition.put(a.charAt(i), i);
                allLettersInTheRightPosition.add(a.charAt(i));
                correctPositions.put(i, true);
            }
        }
        yellowLetters yellow = new yellowLetters(allLettersInWord, lettersInWord);
        greenLetters green = new greenLetters(inTheRightPosition, allLettersInTheRightPosition);
        word newWord = new word(yellow, green);
        wordsGuessed.add(newWord);
        alreadyGuessed.put(a, true);
        return newWord;
    }

    public static String getNextWord(ArrayList<word> wordsGuessed) {
        ArrayList<String> newValidWords = new ArrayList<>();
        for (int i = 0; i < validWords.size(); i++) {
            boolean valid = true;
            for (int j = 0; j < wordsGuessed.size(); j++) {
                if (!valid(validWords.get(i), wordsGuessed.get(j))) {
                    valid = false;
                    j = wordsGuessed.size();
                }
            }
            if (valid && alreadyGuessed.get(validWords.get(i)) == null) {
                newValidWords.add(validWords.get(i));
            }
        }
        validWords = newValidWords;
        int greatest = 0;
        String best = "cigar";
        for (int i = 0; i < validWords.size(); i++) {
            int evaluate = evaluate(validWords.get(i));
            if (evaluate > greatest) {
                greatest = evaluate;
                best = validWords.get(i);
            }

        }
        return best;
    }

    public static boolean valid(String a, word guessedWord) {
        boolean b1 = validLetters(a, guessedWord);
        boolean b2 = lettersInRightPlace(a, guessedWord);
        boolean b3 = notInWord(a, not);
        boolean b = b1 && b2 && b3;
        return b;
    }

    public static boolean validLetters(String a, word guessedWord) {
        int cnt = 0;
        for (int i = 0; i < guessedWord.yellow.allLettersInWord.size(); i++) {
            if (!a.contains(guessedWord.yellow.allLettersInWord.get(i) + "") || !inDifferentPlace(a, guessedWord)) {
                return false;
            } else {
                cnt++;
            }
        }
        return cnt == guessedWord.yellow.allLettersInWord.size();
    }

    public static boolean notInWord(String a, Hashtable<Character, Boolean> not) {
        String newS = "";
        for (int i = 0; i < 5; i++) {
            if (correctPositions.get(i) == null) {
                newS += a.charAt(i);
            }
        }
        for (int i = 0; i < newS.length(); i++) {
            if (a.equals("paint")) {
                int h = 2;
            }
            if (not.get(newS.charAt(i)) != null) {
                if (a.equals("paint")) {
                    int h = 2;
                }
                return false;
            }
        }
        if (a.equals("paint")) {
            int h = 2;
        }
        return true;
    }

    public static boolean inDifferentPlace(String a, word guessedWord) {
        for (int i = 0; i < guessedWord.yellow.allLettersInWord.size(); i++) {
            int posInGuessedWord = guessedWord.yellow.lettersInWord.get(guessedWord.yellow.allLettersInWord.get(i));
            int posInWord = 0;
            for (int j = 0; j < 5; j++) {
                if (a.charAt(j) == guessedWord.yellow.allLettersInWord.get(i)) {
                    posInWord = j;
                }
            }
            if (posInWord == posInGuessedWord) {
                return false;
            }
        }
        return true;
    }

    public static boolean lettersInRightPlace(String a, word guessedWord) {
        for (int i = 0; i < guessedWord.green.allLettersInTheRightPosition.size(); i++) {
            boolean yes = false;
            for (int j = 0; j < 5; j++) {
                if (a.charAt(j) == guessedWord.green.allLettersInTheRightPosition.get(i) && j == guessedWord.green.inTheRightPosition.get(a.charAt(j))) {
                    yes = true;
                }
            }
            if (!yes) {
                return false;
            }
        }
        return true;
    }

    public static class word {
        yellowLetters yellow;
        greenLetters green;

        public word(yellowLetters yellow, greenLetters green) {
            this.yellow = yellow;
            this.green = green;
        }

        public yellowLetters getYellow() {
            return yellow;
        }

        public void setYellow(yellowLetters yellow) {
            this.yellow = yellow;
        }

        public greenLetters getGreen() {
            return green;
        }

        public void setGreen(greenLetters green) {
            this.green = green;
        }

        @Override
        public String toString() {
            return "word{" +
                    "yellow=" + yellow +
                    ", green=" + green +
                    '}';
        }
    }

    public static class yellowLetters {
        ArrayList<Character> allLettersInWord;
        Hashtable<Character, Integer> lettersInWord;

        public yellowLetters(ArrayList<Character> allLettersInWord, Hashtable<Character, Integer> lettersInWord) {
            this.allLettersInWord = allLettersInWord;
            this.lettersInWord = lettersInWord;
        }

        public Hashtable<Character, Integer> getLettersInWord() {
            return lettersInWord;
        }

        public void setLettersInWord(Hashtable<Character, Integer> lettersInWord) {
            this.lettersInWord = lettersInWord;
        }

        public ArrayList<Character> getAllLettersInWord() {
            return allLettersInWord;
        }

        public void setAllLettersInWord(ArrayList<Character> allLettersInWord) {
            this.allLettersInWord = allLettersInWord;
        }

        @Override
        public String toString() {
            return "yellowLetters{" +
                    "allLettersInWord=" + allLettersInWord +
                    ", lettersInWord=" + lettersInWord +
                    '}';
        }
    }

    public static class greenLetters {
        Hashtable<Character, Integer> inTheRightPosition;
        ArrayList<Character> allLettersInTheRightPosition;

        public greenLetters(Hashtable<Character, Integer> inTheRightPosition, ArrayList<Character> allLettersInTheRightPosition) {
            this.inTheRightPosition = inTheRightPosition;
            this.allLettersInTheRightPosition = allLettersInTheRightPosition;
        }

        public Hashtable<Character, Integer> getInTheRightPosition() {
            return inTheRightPosition;
        }

        public void setInTheRightPosition(Hashtable<Character, Integer> inTheRightPosition) {
            this.inTheRightPosition = inTheRightPosition;
        }

        public ArrayList<Character> getAllLettersInTheRightPosition() {
            return allLettersInTheRightPosition;
        }

        public void setAllLettersInTheRightPosition(ArrayList<Character> allLettersInTheRightPosition) {
            this.allLettersInTheRightPosition = allLettersInTheRightPosition;
        }

        @Override
        public String toString() {
            return "greenLetters{" +
                    "inTheRightPosition=" + inTheRightPosition +
                    ", allLettersInTheRightPosition=" + allLettersInTheRightPosition +
                    '}';
        }
    }

}



