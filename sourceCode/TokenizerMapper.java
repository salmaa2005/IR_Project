package ir;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class TokenizerMapper extends Mapper<Object, Text, Text, Text> {
    private Text word = new Text();

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        // Get the file name as the document ID
        String fileName = ((FileSplit) context.getInputSplit()).getPath().getName();

        // Read the line from the file
        String line = value.toString();
        String[] tokens = line.split(" ");

        // Process each token and write to context
        for (int position = 0; position < tokens.length; position++) {
            String token = tokens[position];
            context.write(new Text(token), new Text(fileName + ":" + position));
        }
    }
}
