import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.format.Mat5File;
import us.hebi.matlab.mat.types.Matrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    static final int DATA_POINTS = 1000;

    // Distance functions. None are square rooted, but of course you could for each!
    static double sparseTriDistance(double[] a, double[] b) {
        double accumulator = 0d;

        for (int i = 0; i < a.length; i++) {
            if (a[i] != 0.0 && b[i] != 0.0) {
                accumulator += (2 * a[i] * b[i]) / (a[i] + b[i]);
            }
        }

        return 1.0d - accumulator;
    }

    static double triDistance(double[] a, double[] b) {
        double accumulator = 0d;

        for (int i = 0; i < a.length; i++) {
            accumulator += (2 * a[i] * b[i]) / (a[i] + b[i]);
        }

        return 1.0d - accumulator;
    }


    static double euclideanDistance(double[] a, double[] b) {
        double accumulator = 0d;

        for (int i = 0; i < a.length; i++) {
            accumulator += Math.pow(a[i] - b[i], 2);
        }

        return accumulator;
    }

    static long measureSparseSM(ArrayList<double[]> data) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < DATA_POINTS; i++) {
            for (int j = 0; j < i; j++) {
                sparseTriDistance(data.get(i), data.get(j));
            }
        }
        long end = System.currentTimeMillis();
        return end - start;
    }

    static long measureSparseSM0Impl0(List<SparseRep> data) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < DATA_POINTS; i++) {
            for (int j = 0; j < i; j++) {
                SparseRep.triDistance(data.get(i), data.get(j));
            }
        }
        long end = System.currentTimeMillis();
        return end - start;
    }

    static long measureSparseSMImpl1(List<SparseRep> data) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < DATA_POINTS; i++) {
            for (int j = 0; j < i; j++) {
                SparseRep.triDistance2(data.get(i), data.get(j));
            }
        }
        long end = System.currentTimeMillis();
        return end - start;
    }

    static long measureSparseZipSMImpl(List<SparseRep> data) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < DATA_POINTS; i++) {
            for (int j = 0; j < i; j++) {
                SparseRep.zipTriDistance(data.get(i), data.get(j));
            }
        }
        long end = System.currentTimeMillis();
        return end - start;
    }

    static long measureFullSM(ArrayList<double[]> data) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < DATA_POINTS; i++) {
            for (int j = 0; j < i; j++) {
                triDistance(data.get(i), data.get(j));
            }
        }

        long end = System.currentTimeMillis();
        return end - start;
    }

    static long measureFC6(ArrayList<double[]> data) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < DATA_POINTS; i++) {
            for (int j = 0; j < i; j++) {
                euclideanDistance(data.get(i), data.get(j));
            }
        }

        long end = System.currentTimeMillis();
        return end - start;
    }

    static ArrayList<double[]> loadMat(String path) throws IOException {
        Mat5File data = Mat5.readFromFile(path);
        Matrix features;

        try {
            features = data.getMatrix("probVecs");
        } catch (IllegalArgumentException e) {
            features = data.getMatrix("features");
        }


        ArrayList<double[]> listData = new ArrayList<>();
        int dimensions = features.getNumCols();

        for (int row = 0; row < DATA_POINTS; row++) {
            double[] point = new double[features.getNumCols()];
            for (int col = 0; col < dimensions; col++) {
                point[col] = features.getDouble(row, col);
            }
            listData.add(point);
        }

        return listData;
    }

    public static void main(String[] args) throws IOException {
        ArrayList<double[]> smData = loadMat("/home/bc89/Documents/data/mf_softmax/0.mat");
        ArrayList<double[]> prunedSmData = loadMat("/home/bc89/Documents/data/mf_softmax_pruned/0.mat");
        ArrayList<double[]> fc6Data = loadMat("/home/bc89/Documents/data/mf_alexnet_fc6/0.mat");

        List<SparseRep> sparsePrunedSMData = prunedSmData.stream().map(a -> new SparseRep( a ) ).collect(Collectors.toList());



        long sm = measureFullSM(smData);
        long pruned = measureSparseSM(prunedSmData);
        long al1 = measureSparseSM0Impl0(sparsePrunedSMData);
        long al2 = measureSparseSMImpl1(sparsePrunedSMData);
        long zip = measureSparseZipSMImpl(sparsePrunedSMData);
        long fc6 = measureFC6(fc6Data);

        // Warm up
        for (int i = 0; i < 4; i++) {
            sm = measureFullSM(smData);
            pruned = measureSparseSM(prunedSmData);
            fc6 = measureFC6(fc6Data);
            al1 = measureSparseSM0Impl0(sparsePrunedSMData);
            al2 = measureSparseSMImpl1(sparsePrunedSMData);
            zip = measureSparseZipSMImpl(sparsePrunedSMData);
        }

        int repetitions = 20;

        long smTime = 0;
        long prunedSmTimes = 0;
        long fc6Times = 0;
        long al1Times = 0;
        long al2Times = 0;

        for (int i = 0; i < repetitions; i++) {
            sm = measureFullSM(smData);
            pruned = measureSparseSM(prunedSmData);
            al1 = measureSparseSM0Impl0(sparsePrunedSMData);
            al2 = measureSparseSMImpl1(sparsePrunedSMData);
            fc6 = measureFC6(fc6Data);

            smTime += sm;
            prunedSmTimes += pruned;
            fc6Times += fc6;
            al1Times += al1;
            al2Times += al2;
        }

        System.out.println("Softmax took " + smTime / repetitions + " ms on average over " + repetitions + " repetitions");
        System.out.println("Softmax Pruned took " + prunedSmTimes / repetitions + " ms on average over " + repetitions + " repetitions");
        System.out.println("Al Softmax Pruned0 took " + al1Times / repetitions + " ms on average over " + repetitions + " repetitions");
        System.out.println("Al Softmax Pruned1 took " + al2Times / repetitions + " ms on average over " + repetitions + " repetitions");
        System.out.println("Softmax took " + fc6Times / repetitions + " ms on average over " + repetitions + " repetitions");
    }
}