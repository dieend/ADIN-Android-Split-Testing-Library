package com.dieend.adin.generated.aspect;


public privileged aspect SplittingAspect {
    java.lang.String around(test.HelloWorld instance , java.lang.String ap
 ) : 
        target(instance) &&  
        call(java.lang.String test.HelloWorld.testAnnotated(java.lang.String)) &&
        args(ap)
        {
        switch(com.dieend.example.whichlayouttab.Splitter.whichVersioN()) {
            case 1:
                return proceed(instance, ap);
                            default:
                return instance.testAlternate(ap);
                        }
    }
}
