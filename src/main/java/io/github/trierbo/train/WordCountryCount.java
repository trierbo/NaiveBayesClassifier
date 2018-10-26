package io.github.trierbo.train;

import io.github.trierbo.utils.TextPair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class WordCountryCount {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: WordCountryCount <input path> <output path>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
        conf.set("mapreduce.ifile.readahead", "false");
        Job job = Job.getInstance(conf);

        job.setJarByClass(WordCountryCount.class);
        job.setMapperClass(WordCountryCountMapper.class);
        job.setReducerClass(WordCountryCountReducer.class);
        job.setOutputKeyClass(TextPair.class);
        job.setOutputValueClass(IntWritable.class);

        MultipleOutputs.addNamedOutput(job, "wordCountryCount", TextOutputFormat.class, TextPair.class, IntWritable.class);
        MultipleOutputs.addNamedOutput(job, "countryCount", TextOutputFormat.class, Text.class, IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}