#ADIN-Android-Split-Testing-Library
---------
Do split testing in android easily!

See sample project at https://github.com/dieend/example-WhichLayoutTab

How simple is it?
```
@ADINSimpleAlternateWith(experimentName="testExperiment", method="alternateMethod")
public void someMethod() {
  ...
}
public void alternateMethod() {
  ...
}
```

When execution reached someMethod, the library will check which version it should use, and continue execution based on that,  either go to `someMethod()` or to `alternateMethod()`
The checking version will be based on the interface of IADINSelector, where you can use custom splitter. This splitter will be called before every method called. Generally this what actually will happen when you call annotated method:
```
if (IADINSelector.isInExperiment("testExperiment") && IADINSelector.isTreated()) {
	alternateMethod();
} else {
	someMethod();
}
```


Target of this project is to enable part of android code sent from server to client as alternateMethod.
Suggestion appreciated!
