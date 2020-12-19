package edu.coursera.distributed;

import org.apache.spark.api.java.JavaPairRDD;
import scala.Tuple2;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A wrapper class for the implementation of a single iteration of the iterative
 * PageRank algorithm.
 */
public final class PageRank {
    /**
     * Default constructor.
     */
    private PageRank() {
    }

    /**
     * TODO Given an RDD of websites and their ranks, compute new ranks for all
     * websites and return a new RDD containing the updated ranks.
     *
     * For this assignment, you are responsible for implementing this PageRank
     * algorithm using the Spark Java APIs.
     *
     *   1) JavaPairRDD.join
     *   2) JavaRDD.flatMapToPair
     *   3) JavaPairRDD.reduceByKey
     *   4) JavaRDD.mapValues
     *
     * @author Kevin Zhijun Wang
     *
     * @param sites The connectivity of the website graph, keyed on unique
     *              website IDs.
     * @param ranks The current ranks of each website, keyed on unique website
     *              IDs.
     * @return The new ranks of the websites graph, using the PageRank
     *         algorithm to update site ranks.
     */
    public static JavaPairRDD<Integer, Double> sparkPageRank(
            final JavaPairRDD<Integer, Website> sites,
            final JavaPairRDD<Integer, Double> ranks) {

        JavaPairRDD<Integer, Double> newRanks =
                sites.
                        join(ranks).
                        flatMapToPair(kv -> {
                            Integer websiteId = kv._1();
                            Tuple2<Website, Double> value = kv._2();
                            Website edges = kv._2()._1();
                            Double currentRank = kv._2()._2();

                            List<Tuple2<Integer, Double>> contributions = new LinkedList<>();
                            Iterator<Integer> iterator = edges.edgeIterator();
                            while (iterator.hasNext()) {
                                final int target = iterator.next();
                                contributions.add(new Tuple2<>(target, currentRank / (double) edges.getNEdges()));
                            }
                            return contributions;
                        });

        return newRanks
                .reduceByKey(Double::sum)
                .mapValues(v -> 0.15 + 0.85 * v);
    }
}
