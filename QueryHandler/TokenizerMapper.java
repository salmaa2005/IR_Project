import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class TokenizerMapper extends Mapper<Object, Text, Text, Text> {
	private Text word = new Text();

	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {


	    String DocId = value.toString().substring(0, value.toString().indexOf("\t"));
	    String value_raw =  value.toString().substring(value.toString().indexOf("\t") + 1);

	    String fullLine[] = value_raw.split(" ");
	    
		for(String st : fullLine) {
			//word.set(st);
			context.write(new Text(st), new Text(DocId));
		}
		

	}

}
