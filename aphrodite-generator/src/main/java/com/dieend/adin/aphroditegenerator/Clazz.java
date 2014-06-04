package com.dieend.adin.aphroditegenerator;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Clazz extends BaseContext {
	Method method;
	List<Method> alternateMethods;
	Clazz(ObjectNode data) {
		method = new Method(data.get("package").asText(),
							data.get("class").asText(), 
							data.get("experimentName").asText(), 
							(ObjectNode) data.get("methodPair").get("mainMethod"));
		
		alternateMethods = new ArrayList<Method>();
		ArrayNode alternate = (ArrayNode) data.get("methodPair").get("alternateMethods");
		for (int i=0; i<alternate.size(); i++) {
			ObjectNode var = (ObjectNode) alternate.get(i);
			Method m = org.apache.commons.lang3.SerializationUtils.clone(method);
			m.name = var.get("name").asText();
			m.bucket = var.get("bucket").asInt();
			ArrayNode p = (ArrayNode) var.get("params");
			for (int j=0; j< p.size(); j++) {
				
				String name = p.get(j).get("name").asText();
				Parameter varian = null;
				for (Parameter v: m.parameters()) {
					if (v.name.equals(name)) {
						varian = v;
					}
				}
				varian.assignValue(p.get(j).get("value"), varian.value);
			}
			
			alternateMethods.add(m);
		}
		
	}
}
