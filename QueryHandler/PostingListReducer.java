import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class PostingListReducer extends Reducer<Text, Text, Text, Text> {

	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		HashSet<String> distinctDocID = new HashSet<String>();

		for (Text docID : values) {
			distinctDocID.add(docID.toString());

		}
		
		StringBuilder docIdStr = new StringBuilder();
		for (String docID : distinctDocID) {
			docIdStr.append(docID + ",");
		}
		context.write(key, new Text(docIdStr.toString()));
	}

}
