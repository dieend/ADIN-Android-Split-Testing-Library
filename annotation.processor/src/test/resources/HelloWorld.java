package test;

import com.dieend.adin.annotation.ADINParameterSplitTest;
import com.dieend.adin.annotation.ADINSimpleSplitTest;

public class HelloWorld {
	HelloWorld() {
	}
	@ADINSimpleSplitTest(method="testAlternate", experimentName="testExperiment")
	public String testAnnotated(String ap) {
		return "A";
	}
	public String testAlternate(String ba) {
		return "B";
	}
	@ADINSimpleSplitTest(method="testAlternateVoid", experimentName="testExperiment")
	public void testVoid(){
	}
	public void testAlternateVoid(){
	}
	
	@ADINParameterSplitTest(experimentName="testExperiment", variableIdentifier="identifier1")
	public void testParameter1(String parameter1){
	}
	
	@ADINParameterSplitTest(experimentName="testExperiment")
	public void testParameter2(String parameter1){
	}
  public static void main(String[] args) {
    System.out.println("Hello World!");
  }
  
}