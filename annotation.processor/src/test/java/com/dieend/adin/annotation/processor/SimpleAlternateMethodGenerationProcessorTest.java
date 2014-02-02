package com.dieend.adin.annotation.processor;

import static org.junit.Assert.fail;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.io.ByteStreams;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;

@RunWith(JUnit4.class)
public class SimpleAlternateMethodGenerationProcessorTest {
	 @Test
	  public void generates() {
		 List<Processor> pc = new ArrayList<Processor>();
		 pc.add(new ADINAnnotationProcessor());
		 List<JavaFileObject> jfo = new ArrayList<JavaFileObject>();
		 jfo.add(JavaFileObjects.forResource("HelloWorld.java"));
		 
		 List<JavaFileObject> r = Compilation.compile(pc,jfo).generatedSources();
		 
		 try {
			 ByteStreams.copy(r.get(0).openInputStream(), new FileOutputStream("manual_check.aj"));
		 } catch (Exception e) {
			 e.printStackTrace();
			 fail();
		 }
	  }
}
