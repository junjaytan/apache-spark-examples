/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;

import java.util.ArrayList;
import java.util.List;

/*
 * Computes an approximation to pi
 * Usage: JavaSparkPi [slices]
 * Compile as JAR, run spark-submit bin, then look for result in terminal
 * E.g.: ./bin/spark-submit --master local ~/path/to/spark-example-1.0-SNAPSHOT.jar
 */
public final class JavaSparkPi {

    public static void main(String[] args) throws Exception {
        /*
        * Every spark program must create a JavaSparkContextObject, which
        * tells spark how to access the cluster. To create this you first
        * need to build a SparkConf object that contains info about your app
         */
        SparkConf sparkConf = new SparkConf().setAppName("JavaSparkPi").setMaster("local");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

        int slices = (args.length == 1) ? Integer.parseInt(args[0]) : 2;
        int n = 100000 * slices;
        List<Integer> l = new ArrayList<Integer>(n);
        for (int i = 0; i < n; i++) {
            l.add(i);
        }

        JavaRDD<Integer> dataSet = jsc.parallelize(l, slices);

        int count = dataSet.map(new Function<Integer, Integer>() {
                @Override
            public Integer call(Integer integer) {
                    double x = Math.random() * 2 - 1;
                    double y = Math.random() * 2 - 1;
                    return (x * x + y * y < 1) ? 1 : 0;
                }
            }).reduce(new Function2<Integer, Integer, Integer>() {
                @Override
                public Integer call(Integer integer, Integer integer2) {
                    return integer + integer2;
                }
        });

        System.out.println("\n\n\nPi is roughly " + 4.0 * count / n +"\n\n\n");

        jsc.stop();
    }
}