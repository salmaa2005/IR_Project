import java.io.*;
import java.util.*;

public class PositionalIndex {

    private Map<String, Map<String, Integer>> tfIndex;


    public PositionalIndex() {
        this.tfIndex = new HashMap<>();
    }


    public void loadPositionalIndex(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\\s+", 2);
            String term = parts[0];
            Map<String, Integer> docFrequencies = new HashMap<>();

            if (parts.length > 1) {
                String[] occurrences = parts[1].split(";");
                for (String occurrence : occurrences) {
                    if (!occurrence.trim().isEmpty()) {
                        String[] docInfo = occurrence.split(":");
                        String doc = docInfo[0].trim();
                        docFrequencies.put(doc, docFrequencies.getOrDefault(doc, 0) + 1);
                    }
                }
            }
            tfIndex.put(term, docFrequencies);
        }
        br.close();
    }


    public Map<String, Map<String, Integer>> getTFIndex() {
        return tfIndex;
    }


    public Set<String> getTerms() {
        return tfIndex.keySet();
    }


    public Set<String> getDocuments() {
        Set<String> documents = new TreeSet<>();
        for (Map<String, Integer> docMap : tfIndex.values()) {
            documents.addAll(docMap.keySet());
        }
        return documents;
    }
}
