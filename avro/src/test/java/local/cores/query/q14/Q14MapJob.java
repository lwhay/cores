package local.cores.query.q14;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class Q14MapJob extends Configured implements Tool {
    public static class myMap extends Mapper<AvroKey<Record>, NullWritable, IntWritable, Text> {
        @Override
        public void map(AvroKey<Record> key, NullWritable value, Context context)
                throws IOException, InterruptedException {
            double result = 0.00;
            double sum = 0.00;
            Record r = key.datum();
            int no = 0;
            if (r.get(0).toString().startsWith("PROMO")) {
                List<Record> psL = (List<Record>) r.get(1);
                for (Record ps : psL) {
                    List<Record> lL = (List<Record>) ps.get(0);
                    no += lL.size();
                    for (Record l : lL) {
                        double res = (float) l.get(0) * (1 - (float) l.get(1));
                        sum += res;
                        result += res;
                    }
                }
            } else {
                List<Record> psL = (List<Record>) r.get(1);
                for (Record ps : psL) {
                    List<Record> lL = (List<Record>) ps.get(0);
                    no += lL.size();
                    for (Record l : lL) {
                        double res = (float) l.get(0) * (1 - (float) l.get(1));
                        sum += res;
                    }
                }
            }
            context.write(new IntWritable(1), new Text(no + "|" + result + "|" + sum));
        }
    }

    public static class myReduce extends Reducer<IntWritable, Text, NullWritable, Text> {
        public void reduce(IntWritable key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            double result = 0.00;
            double sum = 0.00;
            int no = 0;
            for (Text value : values) {
                String[] tmp = value.toString().split("\\|");
                result += Double.parseDouble(tmp[1]);
                sum += Double.parseDouble(tmp[2]);
                no += Integer.parseInt(tmp[0]);
            }
            result = result / sum * 100;
            java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
            nf.setGroupingUsed(false);
            context.write(NullWritable.get(), new Text(no + " | " + nf.format(result)));
        }
    }

    public static class myCombiner extends Reducer<IntWritable, Text, IntWritable, Text> {
        @Override
        protected void reduce(IntWritable key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            double result = 0.00;
            double sum = 0.00;
            int no = 0;
            for (Text value : values) {
                String[] tmp = value.toString().split("\\|");
                result += Double.parseDouble(tmp[1]);
                sum += Double.parseDouble(tmp[2]);
                no += Integer.parseInt(tmp[0]);
            }
            context.write(key, new Text(no + "|" + result + "|" + sum));
        }
    }

    public int run(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setStrings("args", args);
        int pathNum = Integer.parseInt(args[0]);
        Schema inputSchema = new Schema.Parser().parse(new File(args[1]));
        String result = args[2];
        int i = 5;

        Job job = new Job(conf, "Q14MapJob");
        job.setJarByClass(Q14MapJob.class);

        AvroJob.setInputKeySchema(job, inputSchema);

        job.setMapperClass(myMap.class);
        job.setReducerClass(myReduce.class);
        job.setCombinerClass(myCombiner.class);

        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        for (int m = 0; m < pathNum; m++) {
            FileInputFormat.addInputPath(job, new Path(args[i + m]));
        }
        FileOutputFormat.setOutputPath(job, new Path(result));

        job.setInputFormatClass(InputFormat_Q14.class);
        LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new Q14MapJob(), args);
        System.exit(res);
    }
}
