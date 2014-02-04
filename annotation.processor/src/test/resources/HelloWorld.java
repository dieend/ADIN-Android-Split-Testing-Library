package test;

import com.dieend.adin.annotation.ADINSimpleAlternateWith;

public class HelloWorld {
	HelloWorld() {
	}
	@ADINSimpleAlternateWith(method="testAlternate", experimentName="testExperiment")
	public String testAnnotated(String ap) {
		return "A";
	}
	public String testAlternate(String ba) {
		return "B";
	}
	@ADINSimpleAlternateWith(method="testAlternateVoid", experimentName="testExperiment")
	public void testVoid(){
	}
	public void testAlternateVoid(){
	}
  public static void main(String[] args) {
    System.out.println("Hello World!");
  }
  
}