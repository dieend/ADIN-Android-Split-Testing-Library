package test;

import com.dieend.adin.annotation.ADINSimpleAlternateWith;

public class HelloWorld {
	HelloWorld() {
	}
	@ADINSimpleAlternateWith(method="testAlternate")
	public String testAnnotated(String ap) {
		return "A";
	}
	public String testAlternate(String ba) {
		return "B";
	}
  public static void main(String[] args) {
    System.out.println("Hello World!");
  }
  
}