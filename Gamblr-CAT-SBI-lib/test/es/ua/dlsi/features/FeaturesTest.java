/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ua.dlsi.features;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import es.ua.dlsi.segmentation.Evidence;
import static org.junit.Assert.*;

/**
 *
 * @author miquel
 */
public class FeaturesTest {

    public FeaturesTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of Cover method, of class Features.
     */
    @Test
    public void testCover() {
        System.out.println("Cover");
        assertEquals(true, Evidence.Cover(0, 0, 1));
        assertEquals(false, Evidence.Cover(0, 1, 1));
        assertEquals(false, Evidence.Cover(1, 0, 1));
        assertEquals(true, Evidence.Cover(1, 1, 1));
        assertEquals(true, Evidence.Cover(5, 1, 10));
        assertEquals(true, Evidence.Cover(9, 0, 10));
    }

    /**
     * Test of PositiveSource method, of class Features.
     */
    /*@Test
    public void testPositiveSource() {
        System.out.println("PositiveSource");
        TranslationUnit tu=new TranslationUnit("serveix per a comprovar que", "sirve para comprobar que");
        SegmentDictionary sd=new SegmentDictionary();
        sd.LoadSegments("./test/xx/uni/dept/translationmemory/testfiles/caseg", "./test/xx/uni/dept/translationmemory/testfiles/esseg", false);
        tu.CollectEvidences(sd, 5, false);

        double feature=Features.PositiveSource(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 1, false);
        //"serveix": 1/1
        assertEquals(1.0,feature,Double.MIN_VALUE);
        feature=Features.PositiveSource(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 2, false);
        //there are not evidences because "serveix per" was translated by "sirve por": 0.5
        assertEquals(0.5,feature,Double.MIN_VALUE);
        feature=Features.PositiveSource(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 3, false);
        //"serveix per a": 1/1
        assertEquals(1.0,feature,Double.MIN_VALUE);
        feature=Features.PositiveSource(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 4, false);
        //"serveix per a comprovar": 1/1
        assertEquals(1.0,feature,Double.MIN_VALUE);
        feature=Features.PositiveSource(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 5, false);
        //"serveix per a comprovar que": 1/1
        assertEquals(1.0,feature,Double.MIN_VALUE);
    }*/

    /**
     * Test of PositiveTarget method, of class Features.
     */
    /*@Test
    public void testPositiveTarget() {
        System.out.println("PositiveTarget");
        TranslationUnit tu=new TranslationUnit("serveix per a comprovar que", "sirve para comprobar que");
        SegmentDictionary sd=new SegmentDictionary();
        sd.LoadSegments("./test/xx/uni/dept/translationmemory/testfiles/caseg", "./test/xx/uni/dept/translationmemory/testfiles/esseg", false);
        tu.CollectEvidences(sd, 5, false);
        double feature=Features.PositiveTarget(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 1, 5, false);
        //"sirve": 1/1
        assertEquals(1.0,feature,Double.MIN_VALUE);
        feature=Features.PositiveTarget(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 2, 5, false);
        //"serveix per" is translated by "serveix per a": 1/1
        assertEquals(1.0,feature,Double.MIN_VALUE);
        feature=Features.PositiveTarget(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 3, 5, false);
        //"sirve para comprobar": 1/1
        assertEquals(1.0,feature,Double.MIN_VALUE);
        feature=Features.PositiveTarget(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 4, 5, false);
        //"sirve para comprobar que": 1/1
        assertEquals(1.0,feature,Double.MIN_VALUE);
        feature=Features.PositiveTarget(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 5, 5, false);
        //There are not evidences for this lenght
        assertEquals(0.5,feature,Double.MIN_VALUE);
    }*/

    /**
     * Test of NegativeSource method, of class Features.
     */
    /*@Test
    public void testNegativeSource() {
        System.out.println("NegativeSource");
        TranslationUnit tu=new TranslationUnit("serveix per a comprovar que", "sirve para comprobar que");
        SegmentDictionary sd=new SegmentDictionary();
        sd.LoadSegments("./test/xx/uni/dept/translationmemory/testfiles/caseg", "./test/xx/uni/dept/translationmemory/testfiles/esseg", false);
        tu.CollectEvidences(sd, 5, false);
        double feature=Features.NegativeSource(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 1, false);
        //No different segment between s' and s_i containing "serveix"
        assertEquals(0.0,feature,Double.MIN_VALUE);
        feature=Features.NegativeSource(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 2, false);
        //No different segment between s' and s_i containing "serveix" 
        assertEquals(0.5,feature,Double.MIN_VALUE);
        feature=Features.NegativeSource(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 3, false);
        //No different segment between s' and s_i containing "serveix"
        assertEquals(0.0,feature,Double.MIN_VALUE);
        feature=Features.NegativeSource(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 4, false);
        //No different segment between s' and s_i containing "serveix"
        assertEquals(0.0,feature,Double.MIN_VALUE);
        feature=Features.NegativeSource(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 5, false);
        //No different segment between s' and s_i containing "serveix"
        assertEquals(0.0,feature,Double.MIN_VALUE);
    }*/

    /**
     * Test of NegativeTarget method, of class Features.
     */
    /*@Test
    public void testNegativeTarget() {
        System.out.println("NegativeTarget");
        TranslationUnit tu=new TranslationUnit("serveix per a comprovar que", "sirve para comprobar que");
        SegmentDictionary sd=new SegmentDictionary();
        sd.LoadSegments("./test/xx/uni/dept/translationmemory/testfiles/caseg", "./test/xx/uni/dept/translationmemory/testfiles/esseg", false);
        tu.CollectEvidences(sd, 5, false);
        double feature=Features.NegativeTarget(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 1, 5, false);
        //No different segment between s' and s_i containing "serveix"
        assertEquals(0.0,feature,Double.MIN_VALUE);
        feature=Features.NegativeTarget(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 2, 5, false);
        //No different segment between s' and s_i containing "serveix"
        assertEquals(0.0,feature,Double.MIN_VALUE);
        feature=Features.NegativeTarget(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 3, 5, false);
        //No different segment between s' and s_i containing "serveix"
        assertEquals(0.0,feature,Double.MIN_VALUE);
        feature=Features.NegativeTarget(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 4, 5, false);
        //No different segment between s' and s_i containing "serveix"
        assertEquals(0.0,feature,Double.MIN_VALUE);
        feature=Features.NegativeTarget(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 5, 5, false);
        //No different segment between s' and s_i containing "serveix"
        assertEquals(0.5,feature,Double.MIN_VALUE);
    }*/

    /**
     * Test of CalculateAllFeatures method, of class Features.
     */
    /*@Test
    public void testCalculateAllFeatures() {
        System.out.println("CalculateAllFeatures");
        TranslationUnit tu=new TranslationUnit("serveix per a comprovar que", "sirve para comprobar que");
        SegmentDictionary sd=new SegmentDictionary();
        sd.LoadSegments("./test/xx/uni/dept/translationmemory/testfiles/caseg", "./test/xx/uni/dept/translationmemory/testfiles/esseg", false);
        tu.CollectEvidences(sd, 5, false);

        List<Pair<String,double[]>> feat=Features.CalculateAllFeatures(new Segment("serveix per a comprovar que"), tu, 0, 5, false);
        double[] feature=Features.PositiveSource(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 1, false);
        //"serveix": 1/1
        assertEquals(1.0,feature,Double.MIN_VALUE);
        feature=Features.PositiveSource(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 2, false);
        //there are not evidences because "serveix per" was translated by "sirve por": 0.5
        assertEquals(0.5,feature,Double.MIN_VALUE);
        feature=Features.PositiveSource(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 3, false);
        //"serveix per a": 1/1
        assertEquals(1.0,feature,Double.MIN_VALUE);
        feature=Features.PositiveSource(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 4, false);
        //"serveix per a comprovar": 1/1
        assertEquals(1.0,feature,Double.MIN_VALUE);
        feature=Features.PositiveSource(new Segment("serveix per a comprovar que"), tu, "sirve", 0, 5, false);
        //"serveix per a comprovar que": 1/1
        assertEquals(1.0,feature,Double.MIN_VALUE);
    }*/
    

}