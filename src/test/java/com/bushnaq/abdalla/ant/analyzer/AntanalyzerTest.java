package com.bushnaq.abdalla.ant.analyzer;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class AntanalyzerTest {

	@Test
	void test() throws IOException {
		String[] args = {"C:/AzureDevops/SLNX/Client/PCClient/build/build.xml","build.xml/ris3.msiinstaller.all,build.xml/ris3.debug.x64,build.xml/ris3.debug.x86"};
        Antanalyzer antAnalyzer = new Antanalyzer();
        antAnalyzer.start(args);

	}

}
