package com.dieend.adin.hermes.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.CLASS)
@Target(ElementType.METHOD)

public @interface RecordAfterWith {
	String eventName();
	Type type() default Type.DEFAULT;
}
