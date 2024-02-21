import java.util.ArrayList;
import java.util.Arrays;

public class SparseRep {

    private int[] indices;
    private double[] data;

    public SparseRep( double[] doubles ) {

        ArrayList<Integer> ar_indices = new ArrayList<>();
        ArrayList<Double> ar_data = new ArrayList<>();

        for (int i = 0; i < doubles.length; i++) {
            if (doubles[i] != 0.0d ) {
                ar_indices.add(i);
                ar_data.add(doubles[i]);
            }
        }

        indices = ar_indices.stream().mapToInt( a -> a ).toArray();
        data = ar_data.stream().mapToDouble( a-> a ).toArray();
    }

    static double triDistance(SparseRep a, SparseRep b) {
        double accumulator = 0d;

        double[] adata = a.data;
        double[] bdata = b.data;

        int[] aindices = a.indices;
        int[] bindices = b.indices;

        int alen = a.indices.length;
        int blen = b.indices.length;

        int a_index = 0;
        int b_index = 0;

        while( a_index < alen && b_index < blen) {
            if (aindices[a_index] < bindices[b_index]) {         // a behind
                a_index = a_index + 1;                           // so increment index
            } else if (bindices[b_index] < aindices[a_index]) {  // b behind
                b_index = b_index + 1;                           // so increment index
            } else if (aindices[a_index] == bindices[b_index]) { // two indices match - do accumulator
                accumulator += (2 * adata[a_index] * bdata[b_index]) / (adata[a_index] + bdata[b_index] );
                a_index = a_index + 1;
                b_index = b_index + 1;
            }
        }

        return 1 - accumulator;
    }


    @Override
    public String toString() {
        return "SparseRep{" +
                "indices=" + Arrays.toString(indices) +
                ", data=" + Arrays.toString(data) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SparseRep)) return false;
        SparseRep sparseRep = (SparseRep) o;
        return Arrays.equals(indices, sparseRep.indices) && Arrays.equals(data, sparseRep.data);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(indices);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

}
