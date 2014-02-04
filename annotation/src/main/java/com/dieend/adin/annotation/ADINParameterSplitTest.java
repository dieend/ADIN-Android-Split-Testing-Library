package com.dieend.adin.annotation;

public @interface ADINParameterSplitTest {
	String experimentName();
	String[] variableIdentifier() default {};
}
