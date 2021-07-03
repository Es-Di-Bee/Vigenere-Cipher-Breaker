import java.util.*;
import edu.duke.*;
import java.lang.*;
import java.io.*;

public class VigenereBreaker {
    public String sliceString(String message, int whichSlice, int totalSlices) {
        StringBuilder sb = new StringBuilder();
        for (int i = whichSlice; i < message.length(); i += totalSlices) {
            sb.append(message.charAt(i));
        }
        return sb.toString();
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        for (int i = 0; i < klength; ++i) {
            String slice = sliceString(encrypted, i, klength);
            CaesarCracker cc = new CaesarCracker(mostCommon);
            int k = cc.getKey(slice);
            key[i] = k;
        }
        return key;
    }
    
    public HashSet<String> readDictionary (FileResource fr) {
        HashSet<String> dictionary = new HashSet<String>();
        for (String word : fr.lines()) {
            word = word.toLowerCase();
            dictionary.add(word);
        }
        return dictionary;
    }
    
    public int countWords (String message, HashSet<String> dictionary) {
        int count = 0;
        String[] words = message.split("\\W+");
        for (String part : words) {
            part = part.toLowerCase();
            if (dictionary.contains(part)) ++count;
        }
        return count;
    }
    
    public String breakForLanguage (String encrypted, HashSet<String> dictionary) {
        String decrypted = null;
        int mxWords = 0;
        int keyLength = 0;
        char mostCommon = mostCommonCharIn (dictionary);
        // System.out.println(mostCommon);
        for (int i = 1; i <= 100; ++i) {
            int[] key = tryKeyLength(encrypted, i, mostCommon);
            // System.out.println(mostCommon);
            // if (i == 5) System.out.println(Arrays.toString(key));
            
            VigenereCipher vc = new VigenereCipher(key);
            String message = vc.decrypt(encrypted);
            int words = countWords(message, dictionary);
            if (words > mxWords) {
                mxWords = words;
                decrypted = message;
                keyLength = key.length;
            }
        }
        System.out.println("The Key Length is: " + keyLength);
        System.out.println("Num of Valid Words are: " + mxWords);
        return decrypted;
    }
    
    public char mostCommonCharIn (HashSet<String> dictionary) {
        int[] freq = new int[1000];
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        for (String str : dictionary) {
            str = str.toLowerCase();
            for (int i = 0; i < str.length(); ++i) {
                // System.out.println(str.charAt(i));
                int x = alphabet.indexOf(str.charAt(i));
                if (x != -1) freq[x]++;
            }
        }
        int mx = 0;
        char c = '\u0000';
        for (int i = 0; i < 26; ++i) {
            if (freq[i] > mx) {
                mx = freq[i];
                c = alphabet.charAt(i);
            }
        }
        return c;
    }
    
    public void breakForAllLangs (String encrypted, HashMap<String, HashSet<String>> languages) {
        String decrypted = null;
        int maxWords = 0;
        String language = null;
        for (String langName : languages.keySet()) {
            HashSet<String> dictionary = languages.get(langName);
            System.out.println("\nLanguage: " + langName);
            String message = breakForLanguage(encrypted, dictionary);
            int words = countWords (message, dictionary);
            if (words > maxWords) {
                language = langName;
                maxWords = words;
                decrypted = message;
            }
        }
        System.out.println ("\nThe language in which the encrypted text is \"" + language + "\"");
        System.out.println ("The number of valid words are: " + maxWords);
        System.out.println ("\nThe decrypted message is:\n" + decrypted);
    }

    public void breakVigenere () {
        System.out.println("Select the encrypted text file please");
        FileResource fr = new FileResource();
        String message = fr.asString();
        System.out.println("Select the Dictionaries please");
        DirectoryResource dr = new DirectoryResource();
        HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
        for (File f : dr.selectedFiles()) {
            FileResource fr2 = new FileResource(f);
            map.put(f.getName(), readDictionary(fr2));
            System.out.println(f.getName().toUpperCase() + " dictionary Reading Done");
        }
        breakForAllLangs (message, map);
        // String decrypted = breakForLanguage (message, dictionary);
        // int[] key = tryKeyLength(message, 4, 'e'); // key already defined
        // System.out.println(Arrays.toString(key));
        // VigenereCipher vc = new VigenereCipher(key);
        // String decrypted = vc.decrypt(message);
        // System.out.println(decrypted);
    }
    
}
