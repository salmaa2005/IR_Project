import java.util.*;

public class QueryProcessor
{
    private final Map<String, Map<String, List<Integer>>> positionalIndex;

    public QueryProcessor(Map<String, Map<String, List<Integer>>> index) {
        this.positionalIndex = index;
    }
    public Map<String, Double> processPhraseQuery(String query)
    {
        String[] terms = query.toLowerCase().split(" ");
        Set<String> relevantDocs = new HashSet<>(positionalIndex.getOrDefault(terms[0], new HashMap<>()).keySet());
        // here we get all the documents that has the first term.
        for (int i = 1; i < terms.length; i++)
        {
            //loops over the other terms
            Set<String> nextTermDocs;
            if (positionalIndex.containsKey(terms[i]))
                nextTermDocs = positionalIndex.get(terms[i]).keySet();
            else
                nextTermDocs = new HashSet<>();
            // if it finds the term in the index, it retrieves it, otherwise returns an empty hashSet
            relevantDocs.retainAll(nextTermDocs);
            // retrieves the common documents only (intersection) (34an de phrase query)
        }

        Map<String, Double> similarityScores = new HashMap<>();
        // key: DocID, value: similarity score
        for (String docID : relevantDocs)
        {
            double score = 0.0;
        }
        return similarityScores;
    }
}
