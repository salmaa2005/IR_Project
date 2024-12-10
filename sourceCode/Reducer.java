import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PostingListReducer extends Reducer<Text, Text, Text, Text>
{
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException
    {
        HashMap<String, StringBuilder> postings = new HashMap<>();

        for (Text value : values)
        {
            String[] docIdAndPos = value.toString().split(":");
            String docId = docIdAndPos[0];
            String position = docIdAndPos[1];

            postings.putIfAbsent(docId, new StringBuilder());
            postings.get(docId).append(position).append(",");
        }

        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, StringBuilder> entry : postings.entrySet())
        {
            String docId = entry.getKey();
            String positions = entry.getValue().toString();
            result.append(docId).append(":").append(positions.substring(0, positions.length() - 1)).append(" ");
        }
        context.write(key, new Text(result.toString().trim()));
    }
}
