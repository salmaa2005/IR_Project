import java.util.*;
import java.util.stream.Collectors;

public class DocumentLengthCalculator {

    private Map<String, Double> documentLengths;


    public DocumentLengthCalculator() {
        this.documentLengths = new HashMap<>();
    }


    public void computeDocumentLengths(Map<String, Map<String, Double>> tfidfMatrix, Set<String> documents) {
        for (String doc : documents) {
            double sumOfSquares = 0.0;


            for (Map<String, Double> termTfidfMap : tfidfMatrix.values()) {
                double tfidf = termTfidfMap.getOrDefault(doc, 0.0);
                sumOfSquares += Math.pow(tfidf, 2);
            }


            double length = Math.sqrt(sumOfSquares);
            documentLengths.put(doc, length);
        }
    }


    public void displayDocumentLengths() {
        System.out.println("\nDocument Lengths:");
        List<String> sortedDocs = documentLengths.keySet().stream()
                .sorted(Comparator.comparingInt(doc -> Integer.parseInt(doc.replaceAll("\\D", ""))))
                .collect(Collectors.toList());

        for (String doc : sortedDocs) {
            System.out.printf("%-10s: %-10.6f%n", doc + " length", documentLengths.get(doc));
        }
    }


    public Map<String, Double> getDocumentLengths() {
        return documentLengths;
    }
}
