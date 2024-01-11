package com.bushnaq.abdalla.ant.analyzer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AntanalyzerTest {

    //    @Test
    void test() throws IOException {
        String[] args = {"C:/AzureDevops/SLNX/Client/PCClient/build/build.xml", "build.xml/ris3.msiinstaller.all,build.xml/ris3.debug.x64,build.xml/ris3.debug.x86"};
        Antanalyzer antAnalyzer = new Antanalyzer();
        antAnalyzer.start(args);
        assertEquals(33, antAnalyzer.context.projectSet.size(), "unexpected number of ant files");
        assertEquals(1061, antAnalyzer.context.targetMap.values().size(), "unexpected number of ant targets");
        assertEquals(14, antAnalyzer.context.usedAntFiles.size(), "unexpected number of used ant files");
        assertEquals(19, antAnalyzer.context.unusedAntFiles.size(), "unexpected number of unused ant files");
        assertEquals(1, antAnalyzer.context.exceptionList.size(), "unexpected number of exceptions");
        assertEquals(1, antAnalyzer.context.missingAntFiles.size(), "unexpected number of missing ant files");
    }

    @Test
    @DisplayName("test_pcclient( the big test )")
    void test_pcclient() throws IOException {
        String[] args = {"references/internal-build/build.xml", "build.xml/ris3.msiinstaller.all,build.xml/ris3.debug.x64,build.xml/ris3.debug.x86"};
        Antanalyzer antAnalyzer = new Antanalyzer();
        antAnalyzer.start(args);
        assertEquals(33, antAnalyzer.context.projectSet.size(), "unexpected number of ant files");
        assertEquals(1061, antAnalyzer.context.targetMap.values().size(), "unexpected number of ant targets");
        assertEquals(342, getUsedTargetCount(antAnalyzer.context.targetMap.values()), "unexpected number of used ant targets");
        assertEquals(15, antAnalyzer.context.usedAntFiles.size(), "unexpected number of used ant files");
        assertEquals(18, antAnalyzer.context.unusedAntFiles.size(), "unexpected number of unused ant files");
        assertEquals(1, antAnalyzer.context.exceptionList.size(), "unexpected number of exceptions");
        assertEquals(1, antAnalyzer.context.missingAntFiles.size(), "unexpected number of missing ant files");
    }

    @Test
    @DisplayName("testCase_1_1( ant file, default target )")
    @Order(11)
    void testCase_1_1() throws IOException {
        String[] args = {"references/case_1_1/build.xml"};
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
    void testCase_1_2() throws IOException {
        String[] args = {"references/case_1_2/build.xml", "build.xml/compile"};
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
    void testCase_1_3() throws IOException {
        String[] args = {"references/case_1_3/build.xml"};
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
    void testCase_1_4() throws IOException {
        String[] args = {"references/case_1_4/build.xml", "build.xml/clean"};
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
    void testCase_2_1() throws IOException {
        String[] args = {"references/case_2_1/build.xml"};
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
    void testCase_2_2() throws IOException {
        String[] args = {"references/case_2_2/build.xml"};
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

    private int getUsedTargetCount(Collection<MultiAntTarget> values) {
        int count = 0;
        for (MultiAntTarget target : values) {
            if (target.isUsed)
                count++;
        }
        return count;
    }

}
