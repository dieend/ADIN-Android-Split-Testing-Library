package com.dieend.adin.aphroditegenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.dieend.adin.hermes.annotations.SimpleSplitTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class AspectFileGenerator {
	String json;
	Pointcut pointcut;
	public AspectFileGenerator(String json) throws IOException{
		this.json = json;
		ObjectMapper om = new ObjectMapper();
		ArrayNode root = (ArrayNode) om.readTree(new File(json));
		for (int i=0; i<root.size(); i++) {
			String name = root.get(i).get("name").textValue();
			if (name.equals(SimpleSplitTest.class.getName())) {
				ArrayNode simplesplit = (ArrayNode) root.get(i).get("content");
				pointcut = new Pointcut();
				for (int j=0; j< simplesplit.size(); j++) {
					ObjectNode cl = (ObjectNode) simplesplit.get(j);
					pointcut.pkg = "com.dieend.adin.aspect.generated";
					pointcut.pointcut.add(new Clazz(cl));
				}
			}
		}
	}
	public void generate(String outFolder) throws IOException{
		File dir = new File(outFolder, pointcut.pkg.replace('.', File.separatorChar));
	    dir.mkdirs();
	    generate(new FileWriter(new File(dir, "SimpleSplitTestAspect.java")));
	}
	public void generate(Writer writer) throws IOException{
		
		MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("simplesplit.mustache");
	    
		mustache.execute(writer, 
	    		pointcut).flush();
	}
	
	
}
