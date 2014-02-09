package com.dieend.adin.annotation.processor;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.junit.Assert.fail;
import static org.truth0.Truth.ASSERT;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;

@RunWith(JUnit4.class)
public class SimpleAlternateMethodGenerationProcessorTest extends AbstractAspectJPolicyEnforcementTest {
	 @Test
	  public void generates() {
		 ASSERT.about(javaSource())
	        .that(JavaFileObjects.forResource(Resources.getResource("HelloWorld.java")))
	        .compilesWithoutError();
		 List<Processor> pc = new ArrayList<Processor>();
		 pc.add(new ADINAnnotationProcessor());
		 List<JavaFileObject> jfo = new ArrayList<JavaFileObject>();
		 jfo.add(JavaFileObjects.forResource(Resources.getResource("HelloWorld.java")));
		 
		 List<JavaFileObject> r = Compilation.compile(pc,jfo).generatedSources();
		 
		 boolean gen = false;
		 for (int i=0; i<r.size(); i++) {
			 gen = true;
			 JavaFileObject o = r.get(i);
			 try {
				 String name = "target/" + o.getName();
				 new File(name).getParentFile().mkdirs();
				 ByteStreams.copy(o.openInputStream(), new FileOutputStream("target/" + o.getName())) ;
				 File java = new File("src/test/resources/HelloWorld.java");
				 File aspect = new File(name);
				 Assert.assertTrue(java.exists());
				 Assert.assertTrue(aspect.exists());
				 List<String> infos = new ArrayList<String>();
				 Result result = compile(java, aspect, infos);
				 if (Result.SUCCESS != result) for (String info : infos) {
					 System.out.println(info);
				 }
				 Assert.assertEquals(Result.SUCCESS, result);
			 } catch (Exception e) {
				 e.printStackTrace();
				 fail(e.getMessage());
			 }
		 }
		 if (!gen) fail();
	  }
	 public static void main(String args[]) {
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
				 File java = new File("src/test/resources/HelloWorld.java");
				 File aspect = new File("target/generated_aspect/manual_check" + i + ".aj");
				 Assert.assertTrue(java.exists());
				 Assert.assertTrue(aspect.exists());
				 List<String> infos = new ArrayList<String>();
				 Result result = new SimpleAlternateMethodGenerationProcessorTest().compile(java, aspect, infos);
				 if (Result.SUCCESS != result) for (String info : infos) {
					 System.out.println(info);
				 }
				 Assert.assertEquals(Result.SUCCESS, result);
			 } catch (Exception e) {
				 e.printStackTrace();
				 fail();
			 }
		 }
	 }
}
