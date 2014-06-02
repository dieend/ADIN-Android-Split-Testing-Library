package com.dieend.adin.aphrodite.generator;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.dieend.adin.aphroditegenerator.AspectFileGenerator;

public class TemplateTest {
	@Test
	public void testGenerate(){
		try {
			new AspectFileGenerator("C:\\Users\\dieend\\SplitTestConfiguration.json").generate("C:\\Users\\dieend\\tmp");
			
			
		} catch(IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
