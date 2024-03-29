package de.bushnaq.abdalla.antanalyzer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AntanalyzerTest {

    private int getUsedTargetCount(Collection<MultiAntTarget> values) {
        int count = 0;
        for (MultiAntTarget target : values) {
            if (target.isNeeded)
                count++;
        }
        return count;
    }

    @Test
    @DisplayName("testCase_0_1( ant file, default target )")
    @Order(1)
    void testCase_0_1() throws Exception {
        String[] args = {"-ant-file", "build.xml"};

        Antanalyzer antAnalyzer = new Antanalyzer();
        antAnalyzer.start(args);
        assertEquals(1, antAnalyzer.context.projectSet.size(), "unexpected number of ant files");
        assertEquals(4, antAnalyzer.context.targetMap.values().size(), "unexpected number of ant targets");
        assertEquals(3, getUsedTargetCount(antAnalyzer.context.targetMap.values()), "unexpected number of used ant targets");
        assertEquals(1, antAnalyzer.context.usedAntFiles.size(), "unexpected number of used ant files");
        assertEquals(0, antAnalyzer.context.unusedAntFiles.size(), "unexpected number of unused ant files");
        assertEquals(0, antAnalyzer.context.exceptionList.size(), "unexpected number of exceptions");
        assertEquals(0, antAnalyzer.context.missingAntFiles.size(), "unexpected number of missing ant files");
    }

    @Test
    @DisplayName("testCase_1_1( ant file, default target )")
    @Order(11)
    void testCase_1_1() throws Exception {
        String[] args = {"-ant-file", "references/case_1_1/build.xml"};

        Antanalyzer antAnalyzer = new Antanalyzer();
        antAnalyzer.start(args);
        assertEquals(1, antAnalyzer.context.projectSet.size(), "unexpected number of ant files");
        assertEquals(4, antAnalyzer.context.targetMap.values().size(), "unexpected number of ant targets");
        assertEquals(3, getUsedTargetCount(antAnalyzer.context.targetMap.values()), "unexpected number of used ant targets");
        assertEquals(1, antAnalyzer.context.usedAntFiles.size(), "unexpected number of used ant files");
        assertEquals(0, antAnalyzer.context.unusedAntFiles.size(), "unexpected number of unused ant files");
        assertEquals(0, antAnalyzer.context.exceptionList.size(), "unexpected number of exceptions");
        assertEquals(0, antAnalyzer.context.missingAntFiles.size(), "unexpected number of missing ant files");
    }

    @Test
    @DisplayName("testCase_1_2( ant file, specified target )")
    @Order(12)
    void testCase_1_2() throws Exception {
        String[] args = {"-ant-file", "references/case_1_2/build.xml", "-ant-targets", "compile"};
        Antanalyzer antAnalyzer = new Antanalyzer();
        antAnalyzer.start(args);
        assertEquals(1, antAnalyzer.context.projectSet.size(), "unexpected number of ant files");
        assertEquals(4, antAnalyzer.context.targetMap.values().size(), "unexpected number of ant targets");
        assertEquals(2, getUsedTargetCount(antAnalyzer.context.targetMap.values()), "unexpected number of used ant targets");
        assertEquals(1, antAnalyzer.context.usedAntFiles.size(), "unexpected number of used ant files");
        assertEquals(0, antAnalyzer.context.unusedAntFiles.size(), "unexpected number of unused ant files");
        assertEquals(0, antAnalyzer.context.exceptionList.size(), "unexpected number of exceptions");
        assertEquals(0, antAnalyzer.context.missingAntFiles.size(), "unexpected number of missing ant files");
    }

    @Test
    @DisplayName("testCase_1_3( ant file, ant task, specified target )")
    @Order(13)
    void testCase_1_3() throws Exception {
        String[] args = {"-ant-file", "references/case_1_3/build.xml"};
        Antanalyzer antAnalyzer = new Antanalyzer();
        antAnalyzer.start(args);
        assertEquals(1, antAnalyzer.context.projectSet.size(), "unexpected number of ant files");
        assertEquals(4, antAnalyzer.context.targetMap.values().size(), "unexpected number of ant targets");
        assertEquals(4, getUsedTargetCount(antAnalyzer.context.targetMap.values()), "unexpected number of used ant targets");
        assertEquals(1, antAnalyzer.context.usedAntFiles.size(), "unexpected number of used ant files");
        assertEquals(0, antAnalyzer.context.unusedAntFiles.size(), "unexpected number of unused ant files");
        assertEquals(0, antAnalyzer.context.exceptionList.size(), "unexpected number of exceptions");
        assertEquals(0, antAnalyzer.context.missingAntFiles.size(), "unexpected number of missing ant files");
    }

    @Test
    @DisplayName("testCase_1_4( ant file, ant task, default target )")
    @Order(14)
    void testCase_1_4() throws Exception {
        String[] args = {"-ant-file", "references/case_1_4/build.xml", "-ant-targets", "clean"};
        Antanalyzer antAnalyzer = new Antanalyzer();
        antAnalyzer.start(args);
        assertEquals(1, antAnalyzer.context.projectSet.size(), "unexpected number of ant files");
        assertEquals(4, antAnalyzer.context.targetMap.values().size(), "unexpected number of ant targets");
        assertEquals(4, getUsedTargetCount(antAnalyzer.context.targetMap.values()), "unexpected number of used ant targets");
        assertEquals(1, antAnalyzer.context.usedAntFiles.size(), "unexpected number of used ant files");
        assertEquals(0, antAnalyzer.context.unusedAntFiles.size(), "unexpected number of unused ant files");
        assertEquals(0, antAnalyzer.context.exceptionList.size(), "unexpected number of exceptions");
        assertEquals(0, antAnalyzer.context.missingAntFiles.size(), "unexpected number of missing ant files");
    }

    @Test
    @DisplayName("testCase_2_1( ant file, ant task, antfile, specified target )")
    @Order(21)
    void testCase_2_1() throws Exception {
        String[] args = {"-ant-file", "references/case_2_1/build.xml"};
        Antanalyzer antAnalyzer = new Antanalyzer();
        antAnalyzer.start(args);
        assertEquals(2, antAnalyzer.context.projectSet.size(), "unexpected number of ant files");
        assertEquals(8, antAnalyzer.context.targetMap.values().size(), "unexpected number of ant targets");
        assertEquals(6, getUsedTargetCount(antAnalyzer.context.targetMap.values()), "unexpected number of used ant targets");
        assertEquals(2, antAnalyzer.context.usedAntFiles.size(), "unexpected number of used ant files");
        assertEquals(0, antAnalyzer.context.unusedAntFiles.size(), "unexpected number of unused ant files");
        assertEquals(0, antAnalyzer.context.exceptionList.size(), "unexpected number of exceptions");
        assertEquals(0, antAnalyzer.context.missingAntFiles.size(), "unexpected number of missing ant files");
    }

    @Test
    @DisplayName("testCase_2_2( ant file, ant task, antfile, default target )")
    @Order(22)
    void testCase_2_2() throws Exception {
        String[] args = {"-ant-file", "references/case_2_2/build.xml", "-pt", "-paf", "-put"};
        Antanalyzer antAnalyzer = new Antanalyzer();
        antAnalyzer.start(args);
        assertEquals(2, antAnalyzer.context.projectSet.size(), "unexpected number of ant files");
        assertEquals(8, antAnalyzer.context.targetMap.values().size(), "unexpected number of ant targets");
        assertEquals(6, getUsedTargetCount(antAnalyzer.context.targetMap.values()), "unexpected number of used ant targets");
        assertEquals(2, antAnalyzer.context.usedAntFiles.size(), "unexpected number of used ant files");
        assertEquals(0, antAnalyzer.context.unusedAntFiles.size(), "unexpected number of unused ant files");
        assertEquals(0, antAnalyzer.context.exceptionList.size(), "unexpected number of exceptions");
        assertEquals(0, antAnalyzer.context.missingAntFiles.size(), "unexpected number of missing ant files");
    }

    @Test
    @DisplayName("testCase_3_1( ant file, ant task, missing antfile, default target )")
    @Order(31)
    void testCase_3_1() throws Exception {
        String[] args = {"-ant-file", "references/case_3_1/build.xml", "-pt", "-paf", "-put"};
        Antanalyzer antAnalyzer = new Antanalyzer();
        antAnalyzer.start(args);
        assertEquals(1, antAnalyzer.context.projectSet.size(), "unexpected number of ant files");
        assertEquals(5, antAnalyzer.context.targetMap.values().size(), "unexpected number of ant targets");
        assertEquals(3, getUsedTargetCount(antAnalyzer.context.targetMap.values()), "unexpected number of used ant targets");
        assertEquals(1, antAnalyzer.context.usedAntFiles.size(), "unexpected number of used ant files");
        assertEquals(0, antAnalyzer.context.unusedAntFiles.size(), "unexpected number of unused ant files");
        assertEquals(1, antAnalyzer.context.exceptionList.size(), "unexpected number of exceptions");
        assertEquals(1, antAnalyzer.context.missingAntFiles.size(), "unexpected number of missing ant files");
    }

    @Test
    @DisplayName("testNoParameters( no parameters )")
    @Order(1000)
    void testNoParameters() throws Exception {
        String[] args = {};
        Antanalyzer antAnalyzer = new Antanalyzer();
        antAnalyzer.start(args);
        assertEquals(0, antAnalyzer.context.projectSet.size(), "unexpected number of ant files");
        assertEquals(0, antAnalyzer.context.targetMap.values().size(), "unexpected number of ant targets");
        assertEquals(0, getUsedTargetCount(antAnalyzer.context.targetMap.values()), "unexpected number of used ant targets");
        assertEquals(0, antAnalyzer.context.usedAntFiles.size(), "unexpected number of used ant files");
        assertEquals(0, antAnalyzer.context.unusedAntFiles.size(), "unexpected number of unused ant files");
        assertEquals(0, antAnalyzer.context.exceptionList.size(), "unexpected number of exceptions");
        assertEquals(0, antAnalyzer.context.missingAntFiles.size(), "unexpected number of missing ant files");
    }

    @Test
    @DisplayName("testPrintHelp( -help )")
    @Order(1001)
    void testPrintHelp() throws Exception {
        String[] args = {"-h"};
        Antanalyzer antAnalyzer = new Antanalyzer();
        antAnalyzer.start(args);
        assertEquals(0, antAnalyzer.context.projectSet.size(), "unexpected number of ant files");
        assertEquals(0, antAnalyzer.context.targetMap.values().size(), "unexpected number of ant targets");
        assertEquals(0, getUsedTargetCount(antAnalyzer.context.targetMap.values()), "unexpected number of used ant targets");
        assertEquals(0, antAnalyzer.context.usedAntFiles.size(), "unexpected number of used ant files");
        assertEquals(0, antAnalyzer.context.unusedAntFiles.size(), "unexpected number of unused ant files");
        assertEquals(0, antAnalyzer.context.exceptionList.size(), "unexpected number of exceptions");
        assertEquals(0, antAnalyzer.context.missingAntFiles.size(), "unexpected number of missing ant files");
    }

    @Test
        //@DisplayName("test_pcclient( the big test )")
    void test_pcclient() throws Exception {
        String[] args = {"-ant-file", "references/internal-build/build.xml", "-ant-targets", "ris3.msiinstaller.all,ris3.debug.x64,ris3.debug.x86", "-pt", "-paf", "-put"};
        Antanalyzer antAnalyzer = new Antanalyzer();
        antAnalyzer.start(args);
        assertEquals(33, antAnalyzer.context.projectSet.size(), "unexpected number of ant files");
        assertEquals(1061, antAnalyzer.context.targetMap.values().size(), "unexpected number of ant targets");
        assertEquals(344, getUsedTargetCount(antAnalyzer.context.targetMap.values()), "unexpected number of used ant targets");
        assertEquals(16, antAnalyzer.context.usedAntFiles.size(), "unexpected number of used ant files");
        assertEquals(17, antAnalyzer.context.unusedAntFiles.size(), "unexpected number of unused ant files");
        assertEquals(1, antAnalyzer.context.exceptionList.size(), "unexpected number of exceptions");
        assertEquals(1, antAnalyzer.context.missingAntFiles.size(), "unexpected number of missing ant files");
    }

}
