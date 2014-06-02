package com.dieend.adin.aphroditegenerator;

import java.util.ArrayList;
import java.util.List;

public class Pointcut extends BaseContext {
	String pkg;
	List<Clazz> pointcut;
	Pointcut() {
		pointcut = new ArrayList<Clazz>();
	}
}
