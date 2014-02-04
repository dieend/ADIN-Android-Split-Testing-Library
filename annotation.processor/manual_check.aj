package com.dieend.adin.generated.aspect;


public privileged aspect SplittingAspect {
    java.lang.String around(test.HelloWorld instance , java.lang.String ap ) : 
        target(instance) &&  
        call(java.lang.String test.HelloWorld.testAnnotated(java.lang.String)) &&
        args(ap)
        {
        if(com.dieend.adin.android.library.ADINAgent.isInExperiment("testExperiment")) {
                return proceed(instance, ap);
        } else {
                return instance.testAlternate(ap);
}
    }
    void around(test.HelloWorld instance  ) : 
        target(instance) &&  
        call(void test.HelloWorld.testVoid()) {
        if(com.dieend.adin.android.library.ADINAgent.isInExperiment("testExperiment")) {
                proceed(instance );
        } else {
                instance.testAlternateVoid();
}
    }
}
