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

    static double triDistance2(SparseRep a, SparseRep b) {
        double accumulator = 0d;

        double[] adata = a.data;
        double[] bdata = b.data;

        int[] aindices = a.indices;
        int[] bindices = b.indices;

        int alen = a.indices.length;
        int blen = b.indices.length;

        if( alen == 0 || blen == 0 ) { //  one empty
            return 1;
        }

        int a_min_index = aindices[0];
        int a_max_index = aindices[alen-1];
        int b_min_index = bindices[0];
        int b_max_index = aindices[blen-1];

        if( a_max_index < b_min_index || b_max_index < a_min_index ) { // no overlap
            return 1;
        }

        int b_index = 0;

        for( int a_index = 0; a_index < alen; a_index++ ) {
            if (aindices[a_index] == bindices[b_index]) { // two indices match - do accumulator
                accumulator += (2 * adata[a_index] * bdata[b_index]) / (adata[a_index] + bdata[b_index]);
                b_index = b_index + 1;
            } else if (aindices[a_index] > bindices[b_index]) {
                // move b on
                while( aindices[a_index] > bindices[b_index] ) {
                    b_index = b_index +1;
                    if(b_index == blen) {
                        // run out of bs so exit
                        return 1 - accumulator;
                    }
                }

            }
        }

        return 1 - accumulator;
    }

    // Distance functions. None are square rooted, but of course you could for each!
    static double sparseTriDistance(double[] a, double[] b) {
        double accumulator = 0f;

        for (int i = 0; i < a.length; i++) {
            if (a[i] != 0 && b[i] != 0) {
                accumulator += (2 * a[i] * b[i]) / (a[i] + b[i]);
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
