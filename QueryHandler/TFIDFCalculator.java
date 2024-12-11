import java.util.*;

public class TFIDFCalculator {

    private final Map<String, Map<String, List<Integer>>> positionalIndex;
    private final Map<String, Double> idfCache;

    public TFIDFCalculator(Map<String, Map<String, List<Integer>>> positionalIndex) {
        this.positionalIndex = positionalIndex;
        this.idfCache = new HashMap<>();
    }


    public Map<String, Integer> calculateTF(String term) {
        Map<String, Integer> tfMap = new HashMap<>();
        if (positionalIndex.containsKey(term)) {
            positionalIndex.get(term).forEach((doc, positions) -> tfMap.put(doc, positions.size()));
        }
        return tfMap;
    }


    public Map<String, Double> calculateWeightedTF(String term) {
        Map<String, Double> wtfMap = new HashMap<>();
        Map<String, Integer> tfMap = calculateTF(term);
        tfMap.forEach((doc, tf) -> wtfMap.put(doc, 1 + Math.log10(tf)));
        return wtfMap;
    }


    public double calculateIDF(String term) {
        if (idfCache.containsKey(term)) {
            return idfCache.get(term);
        }

        int docCount = positionalIndex.containsKey(term) ? positionalIndex.get(term).size() : 0;
        int totalDocs = (int) positionalIndex.values().stream()
                .flatMap(docMap -> docMap.keySet().stream())
                .distinct()
                .count();

        double idf = docCount > 0 ? Math.log10((double) totalDocs / docCount) : 0;
        idfCache.put(term, idf);
        return idf;
    }


    public Map<String, Double> calculateTFIDF(String term) {
        Map<String, Double> tfidfMap = new HashMap<>();
        Map<String, Double> wtfMap = calculateWeightedTF(term);
        double idf = calculateIDF(term);
        wtfMap.forEach((doc, wtf) -> tfidfMap.put(doc, wtf * idf));
        return tfidfMap;
    }


    public Map<String, Double> calculateNormalizedTFIDF(String term) {
        Map<String, Double> tfidfMap = calculateTFIDF(term);
        Map<String, Double> docLengths = calculateDocumentLengths();
        Map<String, Double> normalizedMap = new HashMap<>();

        tfidfMap.forEach((doc, tfidf) -> {
            double docLength = docLengths.getOrDefault(doc, 1.0);
            normalizedMap.put(doc, tfidf / docLength);
        });

        return normalizedMap;
    }


    private Map<String, Double> calculateDocumentLengths() {
        Map<String, Double> docLengths = new HashMap<>();

        positionalIndex.keySet().forEach(term -> {
            Map<String, Double> tfidfMap = calculateTFIDF(term);
            tfidfMap.forEach((doc, tfidf) -> docLengths.merge(doc, tfidf * tfidf, Double::sum));
        });

        docLengths.replaceAll((doc, length) -> Math.sqrt(length));
        return docLengths;
    }


    public void displayTFIDFCalculations(List<String> queryTerms) {
        System.out.printf("%-10s %-10s %-10s %-10s %-10s %-10s%n", "Term", "TF-Raw", "wTF", "IDF", "TF*IDF", "Normalized");


        Set<String> uniquePairs = new HashSet<>();

        for (String term : queryTerms.stream().distinct().toList()) {
            double idf = calculateIDF(term);

            Map<String, Integer> tf = calculateTF(term);
            Map<String, Double> wtf = calculateWeightedTF(term);
            Map<String, Double> tfidf = calculateTFIDF(term);
            Map<String, Double> normalized = calculateNormalizedTFIDF(term);

            for (String doc : tf.keySet()) {
                String uniquePairKey = term + ":" + doc;

                if (!uniquePairs.contains(uniquePairKey)) {
                    uniquePairs.add(uniquePairKey);

                    System.out.printf("%-10s %-10d %-10.4f %-10.4f %-10.4f %-10.4f%n",
                            term,
                            tf.getOrDefault(doc, 0),
                            wtf.getOrDefault(doc, 0.0),
                            idf,
                            tfidf.getOrDefault(doc, 0.0),
                            normalized.getOrDefault(doc, 0.0));
                }
            }
        }
    }

    public Map<String, Double> computeDotProduct(List<String> queryTerms, Set<String> matchedDocuments) {
        Map<String, Double> dotProductMap = new HashMap<>();


        Map<String, Double> normalizedQueryTFIDF = computeNormalizedQueryTFIDF(queryTerms);

        for (String doc : matchedDocuments) {
            double dotProduct = 0.0;
            for (String term : queryTerms) {
                Map<String, Double> docNormalizedTFIDF = calculateNormalizedTFIDF(term);
                dotProduct += normalizedQueryTFIDF.getOrDefault(term, 0.0) * docNormalizedTFIDF.getOrDefault(doc, 0.0);
            }
            dotProductMap.put(doc, dotProduct);
        }

        return dotProductMap;
    }

    private Map<String, Double> computeNormalizedQueryTFIDF(List<String> queryTerms) {
        Map<String, Double> queryTFIDF = new HashMap<>();
        double queryLength = 0.0;

        for (String term : queryTerms) {
            double idf = calculateIDF(term);
            double tfWeighted = 1 + Math.log10(1);
            double tfidf = tfWeighted * idf;
            queryTFIDF.put(term, tfidf);
            queryLength += tfidf * tfidf;
        }

        queryLength = Math.sqrt(queryLength);


        double finalQueryLength = queryLength;
        queryTFIDF.replaceAll((term, value) -> value / finalQueryLength);

        return queryTFIDF;
    }

    public void displaySimilarity(List<String> queryTerms, Set<String> matchedDocuments) {
        Map<String, Double> dotProducts = computeDotProduct(queryTerms, matchedDocuments);

        System.out.println("\nDocument Similarities:");
        dotProducts.forEach((doc, similarity) -> {
            System.out.printf("Similarity (query, %s): %.4f%n", doc, similarity);
        });


        double sum = dotProducts.values().stream().mapToDouble(Double::doubleValue).sum();
        System.out.printf("\nSum of similarities: %.4f%n", sum);
    }
}