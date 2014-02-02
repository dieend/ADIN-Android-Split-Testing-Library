#ADIN-Android-Split-Testing-Library
---------
Do split testing in android easily!

See sample project at https://github.com/dieend/example-WhichLayoutTab

How simple is it?
```
@ADINSimpleAlternateWith(method="alternateMethod")
public void someMethod() {
  ...
}
public void alternateMethod() {
  ...
}
```

When execution reached someMethod, the library will check which version it should use, and continue execution based on that,  either go to `someMethod()` or to `alternateMethod()`

Target of this project is to enable part of android code sent from server to client as alternateMethod.
Suggestion appreciated!
