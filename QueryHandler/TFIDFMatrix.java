import java.util.*;

public class TFIDFMatrix {

    private Map<String, Map<String, Double>> tfidfMatrix;


    public TFIDFMatrix() {
        this.tfidfMatrix = new HashMap<>();
    }


    public void computeTFIDF(Map<String, Map<String, Integer>> tfIndex, int totalDocuments) {

        Map<String, Double> idfMap = new HashMap<>();
        for (String term : tfIndex.keySet()) {
            int df = tfIndex.get(term).size();
            double idf = Math.log10((double) totalDocuments / df);
            idfMap.put(term, idf);
        }


        for (String term : tfIndex.keySet()) {
            Map<String, Integer> termDocs = tfIndex.get(term);
            Map<String, Double> termTfidf = new HashMap<>();

            for (String doc : termDocs.keySet()) {
                int tf = termDocs.get(doc);
                double tfWeighted = 1 + Math.log10(tf);
                double tfidf = tfWeighted * idfMap.get(term);
                termTfidf.put(doc, tfidf);
            }
            tfidfMatrix.put(term, termTfidf);
        }
    }


    public Map<String, Map<String, Double>> getTFIDFMatrix() {
        return tfidfMatrix;
    }


    public void displayTFIDFMatrix(Set<String> terms, Set<String> documents) {

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
                double value = tfidfMatrix.getOrDefault(term, new HashMap<>())
                        .getOrDefault(doc, 0.0);
                System.out.printf("%-15.4f", value);
            }
            System.out.println();
        }
    }
}
