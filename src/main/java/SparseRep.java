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

    static double zipTriDistance(SparseRep a, SparseRep b) {

        if( a.indices.length == 0 || b.indices.length == 0 ) { //  one empty
            return 1;
        } else {
            return 1 - zipAccumulator( 0, a.indices, a.data, 0, b.indices, b.data, 0.0d );
        }
    }

    static double zipAccumulator(int a_index, int[] aindices, double[] adata, int b_index, int[] bindices, double[] bdata, double accumulator) {

        if( a_index >= aindices.length || b_index >= bindices.length ) { // either have run off the end
            return accumulator;
        } else if (aindices[a_index] == bindices[b_index]) { // two indices match - do accumulator and move both on
            return zipAccumulator( a_index + 1, aindices, adata, b_index + 1,bindices, bdata,
                    accumulator + (2 * adata[a_index] * bdata[b_index]) / (adata[a_index] + bdata[b_index] ) );
        } else if (aindices[a_index] < bindices[b_index]) {         // a behind - move aindex on
            return zipAccumulator( a_index + 1, aindices, adata, b_index,bindices, bdata, accumulator );
        } else { // must be the case that b is behind
            return zipAccumulator( a_index, aindices, adata, b_index + 1,bindices, bdata, accumulator );
        }
    }

    static double zipTriDistance2(SparseRep a, SparseRep b) {

        if( a.indices.length == 0 || b.indices.length == 0 ) { //  one empty
            return 1;
        } else {
            return 1 - zipAccumulator2( 0, a.indices, a.data, 0, b.indices, b.data );
        }
    }

    static double zipAccumulator2(int a_index, int[] aindices, double[] adata, int b_index, int[] bindices, double[] bdata) {

        double result = 0;

        while (true) {

            if (a_index >= aindices.length || b_index >= bindices.length) { // either have run off the end
                return result;
            } else if (aindices[a_index] == bindices[b_index]) { // two indices match - do accumulator and move both on

                result += (2 * adata[a_index] * bdata[b_index]) / (adata[a_index] + bdata[b_index]);
                a_index++;
                b_index++;

            } else if (aindices[a_index] < bindices[b_index]) {         // a behind - move aindex on
                a_index++;
            } else { // must be the case that b is behind
                b_index++;
            }
        }
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
        int b_max_index = bindices[blen-1];

        if( a_max_index < b_min_index || b_max_index < a_min_index ) { // no overlap
            return 1;
        }

        int b_index = 0;
        double accumulator = 0d;

        for( int a_index = 0; a_index < alen; a_index++ ) {
            if( b_index >= blen - 1 ) {
                // run out of bs
                break; // the for loop
            } else if (aindices[a_index] == bindices[b_index]) { // two indices match - do accumulator
                accumulator += (2 * adata[a_index] * bdata[b_index]) / (adata[a_index] + bdata[b_index]);
                b_index = b_index + 1;
            } else {
                // move b on
                while( aindices[a_index] > bindices[b_index] ) {
                    b_index = b_index + 1;

                    if (b_index == blen - 1) { // run out of bs so stop incrementing but b_index still legal and we may have a match
                        break; // the while loop
                    }
                }
                if (aindices[a_index] == bindices[b_index]) { // if the indices are equal need to do accumulator
                    accumulator += (2 * adata[a_index] * bdata[b_index]) / (adata[a_index] + bdata[b_index]);
                    b_index = b_index + 1;
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
