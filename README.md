#ADIN-Android-Split-Testing-Library
====================================


Do split testing in android easily!

See sample project at https://github.com/dieend/example-WhichLayoutTab

How simple is it?

##Simple Split Test
-------------------------

```java
@ADINSimpleSplitTest(experimentName="testExperiment", method="alternateMethod")
public void someMethod() {
  ...
}
public void alternateMethod() {
  ...
}
```

When execution reached someMethod, the library will check which version it should use, and continue execution based on that,  either go to `someMethod()` or to `alternateMethod()`
The checking version will be based on the interface of IADINSelector, where you can use custom splitter. This splitter will be called before every method called. Generally this what actually will happen when you call annotated method:
```java
if (IADINSelector.isInExperiment("testExperiment") && IADINSelector.isTreated()) {
	alternateMethod();
} else {
	someMethod();
}
```

##Parameter Injection Split Test
--------------------------------
You can supply different parameter for method you define!
```java
public void constructView() {
  setContentView(generateView(1)); // default to 1
}

@ADINParameterSplitTest(experiment="testExperiment")
public View generateView(int version) {
  if (version==1) {
  	return new CustomView1();
  } else {
  	return new CustomView2();
  }
}
```
The library will control whether you really give 1 as parameter, or other value you defined. The library will use IADINSelector that you can implements. The good news, you can use your custom IADINSelector to get the value from server!


You can also use this approach for other case to, confined by your creativity, and as long as you use primitive value as parameter (Hopefully later we will able to use serializable object as parameter. I mean, why not?).

```java
@ADINParameterSplitTest(experiment="testExperiment")
public JSONObject getJSONObject(int version, String json) { // can get value from other place
  if (version == 1) {
  	return new JSONObject( Util.InputStreamToString(context.getResources().getAssets().open(json)) );
  } else {
  	return new JSONObject(json);
  }
}
```

```java
@ADINParameterSplitTest(experiment="testExperiment", 
	variableIdentifier={"testExperiment-text1", "testExperiment-color1"}
)
public JSONObject setButtonTextAndColor(int text, int color) 	// will search from your data with 
  button.setText(text);						// "testExperiment-text1" and 
  button.setTextColor(color);					// "testExperiment-color1"
}
```

Of course, on the underlying it will still check whether the client is in experiment and treated.

==

Target of this project is to enable part of android code sent from server to client as split test options.
Suggestion appreciated!
