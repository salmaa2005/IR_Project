import java.util.*;
import java.util.stream.Collectors;

public class DFIDFCalculator {

    private Map<String, Integer> dfMap;
    private Map<String, Double> idfMap;


    public DFIDFCalculator() {
        this.dfMap = new HashMap<>();
        this.idfMap = new HashMap<>();
    }


    public void computeDFIDF(Map<String, Map<String, Integer>> tfIndex, int totalDocuments) {

        for (String term : tfIndex.keySet()) {
            int df = tfIndex.get(term).size();
            dfMap.put(term, df);


            double idf = Math.log10((double) totalDocuments / df);
            idfMap.put(term, idf);
        }
    }


    public void displayDFIDFTable() {
        System.out.printf("%-15s%-15s%-15s%n", "Term", "DF", "IDF");
        System.out.println("------------------------------------------");


        List<String> sortedTerms = dfMap.keySet().stream().sorted().collect(Collectors.toList());
        for (String term : sortedTerms) {
            System.out.printf("%-15s%-15d%-15.6f%n", term, dfMap.get(term), idfMap.get(term));
        }
    }


    public Map<String, Integer> getDFMap() {
        return dfMap;
    }

    public Map<String, Double> getIDFMap() {
        return idfMap;
    }
}
