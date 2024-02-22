import org.junit.Test;

import static org.junit.Assert.*;

public class TestTriDistance2 {

    private static double[] doubles1 = new double[]{ 0.0d, 0.0d, 1.0d, 0.0d};
    private static double[] doubles2 = new double[]{ 1.0d, 0.0d, 0.0d, 0.0d};
    private static double[] zeros1 = new double[]{ 0.0d, 0.0d, 0.0d, 0.0d};
    private static double[] zeros2 = new double[]{ 0.0d, 0.0d, 0.0d, 0.0d};
    private static double[] doubles5 = new double[]{ 0.1d, 0.1d, 0.1d, 0.1d};
    private static double[] doubles6 = new double[]{ 0.2d, 0.2d, 0.2d, 0.1d};
    private static double[] doubles7 = new double[]{ 1.0d, 1.0d, 0.0d, 0.0d};
    private static double[] doubles8 = new double[]{ 0.0d, 1.0d, 0.0d, 1.0d};

    private static SparseRep sr1 = new SparseRep( doubles1 );
    private static SparseRep sr2 = new SparseRep( doubles2 );
    private static SparseRep sr3 = new SparseRep( zeros1 );
    private static SparseRep sr4 = new SparseRep( zeros2 );
    private static SparseRep sr5 = new SparseRep( doubles5 );
    private static SparseRep sr6 = new SparseRep( doubles6 );
    private static SparseRep sr7 = new SparseRep( doubles7 );
    private static SparseRep sr8 = new SparseRep( doubles8 );


    public TestTriDistance2() {
//        System.out.println( "Sr1: " + sr1.toString() );
//        System.out.println( "Sr2: " + sr2.toString() );
//        System.out.println( "Sr3: " + sr3.toString() );
//        System.out.println( "Sr4: " + sr4.toString() );
//        System.out.println( "Sr4: " + sr5.toString() );
//        System.out.println( "Sr4: " + sr6.toString() );
    }


    @Test
    public void testdistance1() {
        assertEquals(SparseRep.sparseTriDistance(doubles1,doubles2), SparseRep.triDistance2(sr1,sr2), 0.0001d);
    }

    @Test
    public void testdistance0() {
        assertEquals(SparseRep.sparseTriDistance(doubles2,zeros1), SparseRep.triDistance2(sr2,sr3), 0.0001d);
    }

    @Test
    public void testdistance3() {
        assertEquals(SparseRep.sparseTriDistance(doubles5,doubles6), SparseRep.triDistance2(sr5,sr6), 0.0001d);
    }

    @Test
    public void testdistance4() {
        assertEquals(SparseRep.sparseTriDistance(doubles7,doubles8), SparseRep.triDistance2(sr7,sr8), 0.0001d);
    }
}
