import java.util.*;
import java.util.stream.Collectors;

public class NormalizedTF {

    private Map<String, Map<String, Double>> normalizedTFMatrix;


    public NormalizedTF() {
        this.normalizedTFMatrix = new HashMap<>();
    }


    public void computeNormalizedTF(Map<String, Map<String, Double>> tfidfMatrix, Map<String, Double> documentLengths) {
        for (String term : tfidfMatrix.keySet()) {
            Map<String, Double> tfidfValues = tfidfMatrix.get(term);
            Map<String, Double> normalizedValues = new HashMap<>();

            for (String doc : tfidfValues.keySet()) {
                double tfidf = tfidfValues.get(doc);
                double docLength = documentLengths.getOrDefault(doc, 1.0);
                double normalizedTF = tfidf / docLength;
                normalizedValues.put(doc, normalizedTF);
            }

            normalizedTFMatrix.put(term, normalizedValues);
        }
    }


    public void displayNormalizedTFMatrix(Set<String> terms, Set<String> documents) {

        List<String> sortedTerms = new ArrayList<>(terms);
        Collections.sort(sortedTerms);

        List<String> sortedDocuments = new ArrayList<>(documents);
        sortedDocuments.sort(Comparator.comparingInt(doc -> Integer.parseInt(doc.replaceAll("\\D", ""))));


        System.out.printf("%-15s", "Term");
        for (String doc : sortedDocuments) {
            System.out.printf("%-15s", doc);
        }
        System.out.println();


        for (String term : sortedTerms) {
            System.out.printf("%-15s", term);
            for (String doc : sortedDocuments) {
                double value = normalizedTFMatrix.getOrDefault(term, new HashMap<>())
                        .getOrDefault(doc, 0.0);
                System.out.printf("%-15.6f", value);
            }
            System.out.println();
        }
    }


    public Map<String, Map<String, Double>> getNormalizedTFMatrix() {
        return normalizedTFMatrix;
    }
}
