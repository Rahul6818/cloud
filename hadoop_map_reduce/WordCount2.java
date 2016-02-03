import java.util.*;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount2 {

 public static  HashMap<String, Integer> stdcount = new HashMap<String, Integer>();

  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, IntWritable>{

    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();
    

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString(), ",");
      itr.nextToken();
      String courseName = itr.nextToken();
      int courseGrade = Integer.valueOf(itr.nextToken());
     
//Configuration conf = new Configuration();
//conf.set("test", "123");
 
      if(!stdcount.containsKey(courseName)) {
        stdcount.put(courseName, 1);
      }
      else {
        stdcount.put(courseName, stdcount.get(courseName) + 1);
      }

      word.set(courseName);
      for(int i=0;i <courseGrade;i++){
        context.write(word, one);
      }
      
    }
  }

  public static class IntSumReducer
       extends Reducer<Text,IntWritable,Text,DoubleWritable> {
    private DoubleWritable result = new DoubleWritable();

    public void reduce(Text key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {
	//Configuration conf = new Configuration();
//System.out.println(conf.get("test")+ "============ " );
System.out.println("hashmap==== " + stdcount);

      int sum = 0;
      for (IntWritable val : values) {
        sum += val.get();
      }
System.out.println(key + " " + sum );
	//TokenizerMapper tm = new TokenizerMapper();
	int temp = (int) stdcount.get(key.toString());
	double avg = (double)sum/temp;
	System.out.println(temp + "  " + avg);

      result.set((avg));
      context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "word count");
    job.setJarByClass(WordCount2.class);
    job.setMapperClass(TokenizerMapper.class);
    //job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}

