import java.util.*;

public class TFWeighted {

    private Map<String, Map<String, Double>> tfWeightedIndex;


    public TFWeighted() {
        this.tfWeightedIndex = new HashMap<>();
    }


    public void computeTFWeighted(Map<String, Map<String, Integer>> tfIndex) {
        for (String term : tfIndex.keySet()) {
            Map<String, Integer> docFrequencies = tfIndex.get(term);
            Map<String, Double> weightedValues = new HashMap<>();

            for (String doc : docFrequencies.keySet()) {
                int tf = docFrequencies.get(doc);
                double tfWeighted = 1 + Math.log10(tf);
                weightedValues.put(doc, tfWeighted);
            }
            tfWeightedIndex.put(term, weightedValues);
        }
    }


    public Map<String, Map<String, Double>> getTFWeightedIndex() {
        return tfWeightedIndex;
    }


    public void displayTFWeightedMatrix(Set<String> terms, Set<String> documents) {

        List<String> sortedTerms = new ArrayList<>(terms);
        Collections.sort(sortedTerms);


        List<String> sortedDocuments = new ArrayList<>(documents);
        sortedDocuments.sort(Comparator.comparingInt(doc -> Integer.parseInt(doc.replace(".txt", ""))));


        System.out.printf("%-15s", "Term");
        for (String doc : sortedDocuments) {
            System.out.printf("%-15s", doc);
        }
        System.out.println();

        for (String term : sortedTerms) {
            System.out.printf("%-15s", term);
            for (String doc : sortedDocuments) {
                double value = tfWeightedIndex.getOrDefault(term, new HashMap<>())
                        .getOrDefault(doc, 0.0);
                System.out.printf("%-15.2f", value);
            }
            System.out.println();
        }
    }
}
