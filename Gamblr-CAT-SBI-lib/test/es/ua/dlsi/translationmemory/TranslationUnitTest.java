/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ua.dlsi.translationmemory;

import es.ua.dlsi.translationmemory.SegmentDictionary;
import es.ua.dlsi.translationmemory.TranslationUnit;
import es.ua.dlsi.segmentation.Segment;
import es.ua.dlsi.segmentation.Evidence;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author miquel
 */
public class TranslationUnitTest {

    public TranslationUnitTest() {
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
     * Test of equals method, of class TranslationUnit.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        TranslationUnit tu1=new TranslationUnit("aquestes són iguals", "thisones are equal");
        TranslationUnit tu2=new TranslationUnit("aquestes són iguals", "thisones are equal");
        TranslationUnit tu3=new TranslationUnit("aquesta menor", "thisone smaller");
        TranslationUnit tu4=new TranslationUnit("aquesta és molt més llarga", "thisone is more much longer");
        assertEquals(true, tu1.equals(tu2));
        assertEquals(false, tu1.equals(tu3));
        assertEquals(false, tu1.equals(tu4));
    }

    /**
     * Test of compareTo method, of class TranslationUnit.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        TranslationUnit tu1=new TranslationUnit("aquestes són iguals", "thisones are equal");
        TranslationUnit tu2=new TranslationUnit("aquestes són iguals", "thisones are equal");
        TranslationUnit tu3=new TranslationUnit("aquesta menor", "thisone smaller");
        TranslationUnit tu4=new TranslationUnit("aquesta és molt més llarga", "thisone is more much longer");
        assertEquals(0, tu1.compareTo(tu2));
        assertEquals(-1, tu1.compareTo(tu3));
        assertEquals(1, tu1.compareTo(tu4));
    }

    /**
     * Test of compareTo method, of class TranslationUnit.
     */
    @Test
    public void testCollectEvidences() {
        System.out.println("CollectEvidences");
        SegmentDictionary sd=new SegmentDictionary();
        sd.LoadSegments("./test/xx/uni/dept/translationmemory/testfiles/caseg", "./test/xx/uni/dept/translationmemory/testfiles/esseg", false);
        TranslationUnit tu1=new TranslationUnit("aquesta prova", "esta prueba");
        tu1.CollectEvidences(sd, 5, false);
        TranslationUnit tu2=new TranslationUnit("aquestes són iguals", "");
        tu2.CollectEvidences(sd, 5, false);
        TranslationUnit tu3=new TranslationUnit("aquesta és una altra prova", "esta es otra prueba");
        tu3.CollectEvidences(sd, 5, false);
        Set<Evidence> ev1=new LinkedHashSet<Evidence>();
        ev1.add(new Evidence(new Segment("aquesta"), new Segment("esta"), 0, 0));
        ev1.add(new Evidence(new Segment("prova"), new Segment("prueba"), 1, 1));
        ev1.add(new Evidence(new Segment("aquesta prova"), new Segment("esta prueba"), 0, 0));
        Set<Evidence> ev2=new LinkedHashSet<Evidence>();
        ev2.add(new Evidence(new Segment("aquesta"), new Segment("esta"), 0, 0));
        ev2.add(new Evidence(new Segment("prova"), new Segment("prueba"), 4, 3));
        assertEquals(ev1, tu1.getEvidences());
        assertEquals(new LinkedHashSet<Evidence>(), tu2.getEvidences());
        assertEquals(ev2, tu3.getEvidences());
    }

}