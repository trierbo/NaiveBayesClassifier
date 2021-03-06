package io.github.trierbo.train;

import io.github.trierbo.NaiveBayes;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 统计训练集中的所有不同的单词, 作为词典, 即文档中词所有可能的取值
 * 将其用于拉普拉斯平滑：
 * P(A|C) = (该类别下A出现的次数+1) / (该类别下文档的词数+词典大小)
 */
public class WordDict {

    public static class WordDictMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            context.write(value, NullWritable.get());
        }
    }

    public static class WordDictReducer extends Reducer<Text, NullWritable, Text, NullWritable> {
        protected void reduce(Text key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
            // 每次调用reduce得到一个不同的词
            NaiveBayes.wordDict++;
            context.write(key, NullWritable.get());
        }
    }

    public static void main(String args[]) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: WordDict <input path> <output path>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
        conf.set("mapreduce.ifile.readahead", "false");
        Job job = Job.getInstance(conf);

        job.setJarByClass(WordDict.class);
        job.setMapperClass(WordDictMapper.class);
        job.setReducerClass(WordDictReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        FileInputFormat.addInputPaths(job, args[0]);
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
