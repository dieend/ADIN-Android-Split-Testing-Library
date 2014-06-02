package com.dieend.adin.aphroditegenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Method extends BaseContext implements Serializable{
	
	private static final long serialVersionUID = -2781056775502306857L;
	String experimentName;
	String pkg;
	String clazz;
	String returnType;
	String name;
	int bucket; // TODO in APT
	
	private List<Parameter> params;
	int paramIdx = 0;
	Method(String pkg, String clazz, String experimentName, ObjectNode data) {
		this.pkg = pkg;
		this.clazz = clazz;
		this.experimentName = experimentName;
		this.returnType = data.get("returnType").asText();
		this.name = data.get("name").asText();
		params = new ArrayList<Parameter>();
		JsonNode node = data.get("bucket");
		this.bucket =  node == null? 0 : node.asInt(); 
		ArrayNode ps = (ArrayNode) data.get("params");
		for (int i=0; i < ps.size(); i++) {
			ObjectNode p = (ObjectNode) ps.get(i);
			params.add(new Parameter(p));
		}
	}
	List<Parameter> parameters() {
		paramIdx = 0;
		return params;
	}
	boolean isFirstParam() {
		boolean ans = paramIdx == 0;
		paramIdx++;
		return ans;
	}
	boolean isHaveParam() {
		return params.size() != 0;
	}
	boolean isNotVoid() {
		return !returnType.equals("void");
	}
}
