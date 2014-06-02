package com.dieend.adin.aphroditegenerator;

import java.io.Serializable;
import java.security.InvalidParameterException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class Parameter extends BaseContext implements Serializable{

	private static final long serialVersionUID = -3677382665756576006L;
	Parameter(ObjectNode data) {
		type = data.get("type").asText();
		name = data.get("name").asText();
		modifiable = data.get("modifiable").asBoolean();
		
		assignValue( data.get("value"), name);
	}
	void assignValue(JsonNode node, String def) {
		if (modifiable) {
			if (node == null) {
				value = def;
			} else {
				switch (node.getNodeType()) {
				case ARRAY:
					throw new InvalidParameterException("param.value for "+name+" is array");
				case BINARY:
					throw new InvalidParameterException("param.value for "+name+" is binary");
				case BOOLEAN:
					value = String.valueOf(node.asBoolean());
					break;
				case NUMBER:
					value = node.asText();
					break;
				case OBJECT:
					throw new InvalidParameterException("param.value for "+name+" is object");
				case POJO:
					throw new InvalidParameterException("param.value for "+name+" is pojo");
				case STRING:
					value = "\"" + node.textValue() + "\"";
					break;
				default:
					value = def;
					break;
				
				}
			}
		} else {
			value = name;
		}
	}
	String type;
	String name;
	boolean modifiable;
	String value;
}
