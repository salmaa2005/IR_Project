import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class TokenizerMapper extends Mapper<Object, Text, Text, Text>
{
    private Text word = new Text();

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException
    {
        String docId = value.toString().substring(0, value.toString().indexOf("\t"));
        String valueRaw = value.toString().substring(value.toString().indexOf("\t") + 1);

        String[] fullLine = valueRaw.split(" ");
        for (int position = 0; position < fullLine.length; position++)
        {
            String token = fullLine[position];
            context.write(new Text(token), new Text(docId + ":" + position));
        }
    }
}
