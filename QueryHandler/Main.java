import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        try {
            String filePath = "C:\\Users\\muham\\Desktop\\ir project\\IR project\\src\\data.txt";


            PositionalIndex positionalIndex = new PositionalIndex();
            positionalIndex.loadPositionalIndex(filePath);


            TFWeighted tfWeighted = new TFWeighted();
            tfWeighted.computeTFWeighted(positionalIndex.getTFIndex());


            DFIDFCalculator dfidfCalculator = new DFIDFCalculator();
            dfidfCalculator.computeDFIDF(positionalIndex.getTFIndex(), positionalIndex.getDocuments().size());


            TFIDFMatrix tfidfMatrix = new TFIDFMatrix();
            tfidfMatrix.computeTFIDF(positionalIndex.getTFIndex(), positionalIndex.getDocuments().size());


            DocumentLengthCalculator docLengthCalc = new DocumentLengthCalculator();
            docLengthCalc.computeDocumentLengths(tfidfMatrix.getTFIDFMatrix(), positionalIndex.getDocuments());


            NormalizedTF normalizedTF = new NormalizedTF();
            normalizedTF.computeNormalizedTF(tfidfMatrix.getTFIDFMatrix(), docLengthCalc.getDocumentLengths());


            System.out.println("\nTF Weighted Matrix:");
            tfWeighted.displayTFWeightedMatrix(positionalIndex.getTerms(), positionalIndex.getDocuments());

            System.out.println("\nDF and IDF Table:");
            dfidfCalculator.displayDFIDFTable();

            System.out.println("\nTF-IDF Matrix:");
            tfidfMatrix.displayTFIDFMatrix(positionalIndex.getTerms(), positionalIndex.getDocuments());

            System.out.println("\nDocument Lengths:");
            docLengthCalc.displayDocumentLengths();

            System.out.println("\nNormalized TF-IDF Matrix:");
            normalizedTF.displayNormalizedTFMatrix(positionalIndex.getTerms(), positionalIndex.getDocuments());


            QueryProcessor queryProcessor = new QueryProcessor(filePath);


            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your query (e.g., 'fear fools AND calpurnia NOT caeser'):");
            String query = scanner.nextLine();


            queryProcessor.processQuery(query);


            List<String> queryTerms = Arrays.asList(query.split("\\s+"));


            TFIDFCalculator tfidfCalculator = new TFIDFCalculator(queryProcessor.getPositionalIndex());
            Set<String> matchedDocs = queryProcessor.getMatchedDocuments();


            tfidfCalculator.displayTFIDFCalculations(queryTerms);


            tfidfCalculator.displaySimilarity(queryTerms, matchedDocs);

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
