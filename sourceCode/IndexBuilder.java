import org.apache.hadoop.*;
import java.io.*;
import java.util.*;

public class IndexBuilder
{
    private String extractDocId(String inputSplit)
    {
        String fileName = inputSplit.substring(inputSplit.lastIndexOf("/") + 1);
        // This Line gets the file name, ex: 1.txt
        String fileBaseName = fileName.substring(0, fileName.lastIndexOf("."));
        // Removes the .txt extension
        return "doc" + fileBaseName;
        // Returns "doc1", "doc2", ..etc.
    }

    public static class TokenizerMapper extends Mapper<Object, Text, Text, Text>
    {
        private Text word = new Text();
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException
        {
            String[] tokens = value.toString().split(" ");
            String docID = extractDocId(context.getInputSplit().toString());
            for (int i = 0; i < tokens.length; i++)
            {
                word.set(tokens[i].toLowerCase());
                context.write(word, new Text(docID + ":" + i));
            }
        }
    }

    public static class PositionalIndexReducer extends Reducer<Text, Text, Text, Text>
    {
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException
        {
            Map<String, ArrayList<Integer>> positionMap = new HashMap<>();
            // The String is the docID and the ArrayList is the positions of the term in THIS DOCUMENT
            for (Text val : values)
            // ex: doc1:0
            {
                String[] parts = val.toString().split(":");
                String docID = parts[0];
                int position = Integer.parseInt(parts[1]);
                // we store the docID and the position
                positionMap.putIfAbsent(docID, new ArrayList<>());
                positionMap.get(docID).add(position);
                // for each value, we split and append the positions (values) to the docID (key)
                // the map will be like this: {"doc1": [0,3,7], "doc2": [1,2], "doc3":[3,8]}
            }
            StringBuilder result = new StringBuilder();
            for (Map.Entry<String, ArrayList<Integer>> entry : positionMap.entrySet())
            {
                result.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
                // we loop over the positionMap and write the desired output
                // key:value; ----> doc1:[0,3,7];doc2[1,2];doc3:[3,8]
            }
            context.write(key, new Text(result.toString()));
            // attaches the result (positional index) to each entry/distinct term
            // ex: this  doc1:[0,3,7];doc2[1,2];
        }
    }
    public static void main(String[] args) throws Exception 
    {
      if (args.length != 2)
      {
        System.err.println("Usage: IndexBuilder <input path> <output path>");
        System.exit(-1);
      }
      Configuration conf = new Configuration();
      Job job = Job.getInstance(conf, "Positional Index Builder");
      job.setJarByClass(IndexBuilder.class);
      job.setMapperClass(TokenizerMapper.class);
      job.setReducerClass(PositionalIndexReducer.class);
      job.setMapOutputKeyClass(Text.class);
      job.setMapOutputValueClass(Text.class);
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(Text.class);
      FileInputFormat.addInputPath(job, new Path(args[0]));
      FileOutputFormat.setOutputPath(job, new Path(args[1]));
      System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
