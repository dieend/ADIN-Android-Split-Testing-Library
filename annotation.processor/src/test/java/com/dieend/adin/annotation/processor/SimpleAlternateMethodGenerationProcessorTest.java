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
		 
		 boolean gen = false;
		 for (int i=0; i<r.size(); i++) {
			 gen = true;
			 JavaFileObject o = r.get(i);
			 try {
				 ByteStreams.copy(o.openInputStream(), new FileOutputStream("target/manual_check" + i + ".aj") );
			 } catch (Exception e) {
				 e.printStackTrace();
				 fail();
			 }
		 }
		 if (!gen) fail();
	  }
}
