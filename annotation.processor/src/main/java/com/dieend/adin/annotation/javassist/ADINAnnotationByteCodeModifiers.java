package com.dieend.adin.annotation.javassist;

import com.dieend.adin.annotation.SimpleSplitTest;
import com.impetus.annovention.listener.MethodAnnotationDiscoveryListener;

public class ADINAnnotationByteCodeModifiers {
	/** Dummy FieldAnnotation listener */
    static class MyMethodAnnotationListener implements MethodAnnotationDiscoveryListener {
 
        @Override
        public void discovered(String clazz, String method, String signature, String annotation) {
            System.out.println("Discovered Method(" + clazz + "." + method + "(" + signature + ")" + ") " +
                    "with Annotation(" + annotation + ")");
        }
 
        @Override
        public String[] supportedAnnotations() {
            // Listens for @PrePersist, @PreRemove and @PostPersist annotations.
            return new String[] {
                    SimpleSplitTest.class.getName()
            };
        }
    }
}
