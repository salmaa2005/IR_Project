package ir;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PostingListReducer extends Reducer<Text, Text, Text, Text> {
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // Use a HashMap to store document IDs and their corresponding positions
        HashMap<String, StringBuilder> postings = new HashMap<>();

        for (Text value : values) {
            String[] docIdAndPos = value.toString().split(":");
            String docId = docIdAndPos[0];
            String position = docIdAndPos[1];

            // Append the position to the existing document entry, or create a new one
            postings.computeIfAbsent(docId, k -> new StringBuilder()).append(position).append(",");
        }

        // Build the final output format
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, StringBuilder> entry : postings.entrySet()) {
            String docId = entry.getKey();
            String positions = entry.getValue().toString();
            // Remove trailing comma from positions and format the entry
            result.append(docId).append(":").append(positions.substring(0, positions.length() - 1)).append("; ");
        }

        // Write the key (word) and its posting list to the context
        context.write(key, new Text(result.toString().trim()));
    }
}
