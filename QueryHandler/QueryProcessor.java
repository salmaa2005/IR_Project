import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class QueryProcessor {

    private Map<String, Map<String, List<Integer>>> positionalIndex;
    private Set<String> matchedDocuments;

    public QueryProcessor(String filePath) throws IOException {
        this.positionalIndex = loadPositionalIndex(filePath);
        this.matchedDocuments = new HashSet<>();
    }

    private Map<String, Map<String, List<Integer>>> loadPositionalIndex(String filePath) throws IOException {
        Map<String, Map<String, List<Integer>>> index = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\\s+", 2);
            String term = parts[0];
            Map<String, List<Integer>> docPositions = new HashMap<>();

            if (parts.length > 1) {
                String[] occurrences = parts[1].split(";");
                for (String occurrence : occurrences) {
                    if (!occurrence.trim().isEmpty()) {
                        String[] docInfo = occurrence.split(":");
                        String doc = docInfo[0].trim();
                        int position = Integer.parseInt(docInfo[1]);
                        docPositions.computeIfAbsent(doc, k -> new ArrayList<>()).add(position);
                    }
                }
            }
            index.put(term, docPositions);
        }
        br.close();
        return index;
    }

    public void processQuery(String query) {
        List<String> tokens = new ArrayList<>();
        StringBuilder phraseBuilder = new StringBuilder();
        List<String> booleanOperators = Arrays.asList("and", "or", "not");

        for (String word : query.toLowerCase().split("\\s+")) {
            if (booleanOperators.contains(word)) {
                if (phraseBuilder.length() > 0) {
                    tokens.add(phraseBuilder.toString().trim());
                    phraseBuilder.setLength(0);
                }
                tokens.add(word);
            } else {
                if (phraseBuilder.length() > 0) {
                    phraseBuilder.append(" ");
                }
                phraseBuilder.append(word);
            }
        }
        if (phraseBuilder.length() > 0) {
            tokens.add(phraseBuilder.toString().trim());
        }


        this.matchedDocuments = handleBooleanQuery(tokens, booleanOperators);
        System.out.println("\nMatched Documents:");
        if (matchedDocuments.isEmpty()) {
            System.out.println("No documents matched the query.");
        } else {
            matchedDocuments.forEach(System.out::println);
        }
    }

    private Set<String> handleBooleanQuery(List<String> tokens, List<String> booleanOperators) {
        Set<String> resultDocs = null;
        boolean isNot = false;
        boolean isOr = false;

        for (String token : tokens) {
            if (token.equals("and")) {
                isNot = false;
                isOr = false;
            } else if (token.equals("or")) {
                isNot = false;
                isOr = true;
            } else if (token.equals("not")) {
                isNot = true;
            } else {
                Set<String> currentDocs = handlePhrase(token);
                if (isNot) {
                    if (resultDocs == null) {
                        resultDocs = positionalIndex.values().stream()
                                .flatMap(docMap -> docMap.keySet().stream())
                                .collect(Collectors.toSet());
                    }
                    resultDocs.removeAll(currentDocs);
                } else if (isOr) {
                    if (resultDocs == null) {
                        resultDocs = new HashSet<>(currentDocs);
                    } else {
                        resultDocs.addAll(currentDocs);
                    }
                } else {
                    if (resultDocs == null) {
                        resultDocs = new HashSet<>(currentDocs);
                    } else {
                        resultDocs.retainAll(currentDocs);
                    }
                }
                isNot = false;
                isOr = false;
            }
        }
        return resultDocs == null ? new HashSet<>() : resultDocs;
    }

    private Set<String> handlePhrase(String phrase) {
        String[] terms = phrase.split("\\s+");
        if (terms.length == 1) {
            return positionalIndex.getOrDefault(terms[0], new HashMap<>()).keySet();
        }

        Map<String, List<Integer>> firstTermDocs = positionalIndex.getOrDefault(terms[0], new HashMap<>());
        Set<String> resultDocs = new HashSet<>();
        for (String doc : firstTermDocs.keySet()) {
            List<Integer> validPositions = new ArrayList<>(firstTermDocs.get(doc));
            boolean phraseFound = true;

            for (int i = 1; i < terms.length; i++) {
                Map<String, List<Integer>> nextTermDocs = positionalIndex.getOrDefault(terms[i], new HashMap<>());
                List<Integer> nextPositions = nextTermDocs.getOrDefault(doc, new ArrayList<>());
                List<Integer> updatedPositions = new ArrayList<>();
                for (int pos : validPositions) {
                    if (nextPositions.contains(pos + 1)) {
                        updatedPositions.add(pos + 1);
                    }
                }

                if (updatedPositions.isEmpty()) {
                    phraseFound = false;
                    break;
                }
                validPositions = updatedPositions;
            }

            if (phraseFound) {
                resultDocs.add(doc);
            }
        }
        return resultDocs;
    }

    public Map<String, Map<String, List<Integer>>> getPositionalIndex() {
        return positionalIndex;
    }

    public Set<String> getMatchedDocuments() {
        return matchedDocuments;
    }
}
