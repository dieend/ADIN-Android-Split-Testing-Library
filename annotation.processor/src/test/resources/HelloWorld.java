package test;

import com.dieend.adin.annotation.ParameterSplitTest;
import com.dieend.adin.annotation.RecordBeforeWith;
import com.dieend.adin.annotation.RecordAfterWith;
import com.dieend.adin.annotation.SimpleSplitTest;

public class HelloWorld {
	HelloWorld() {
	}
	@SimpleSplitTest(method="testAlternate", experimentName="testExperiment")
	public String testAnnotated(String ap) {
		return "A";
	}
	public String testAlternate(String ba) {
		return "B";
	}
	@SimpleSplitTest(method="testAlternateVoid", experimentName="testExperiment")
	public void testVoid(){
	}
	public void testAlternateVoid(){
	}
	
	@ParameterSplitTest(experimentName="testExperiment", variableIdentifier="identifier1")
	public void testParameter1(String parameter1){
	}
	
	@ParameterSplitTest(experimentName="testExperiment")
	public void testParameter2(String parameter1){
	}
	
	@RecordBeforeWith(eventName="record")
	public void testRecordBeforeVoid() {
	}
	
	@RecordBeforeWith(eventName="record", type=com.dieend.adin.annotation.Type.TIMED)
	public void testRecordBeforeTimed(int time, String a) {
	}
	@RecordBeforeWith(eventName="record", type=com.dieend.adin.annotation.Type.END_TIMED)
	public void testRecordBeforeTimedEnd() {
	}
	
	@RecordAfterWith(eventName="record")
	public void testRecordAfterVoid() {
	}
	
	@RecordAfterWith(eventName="record", type=com.dieend.adin.annotation.Type.TIMED)
	public int testRecordAfterTimed(int time, String a) {
		return 0;
	}
	@RecordAfterWith(eventName="record", type=com.dieend.adin.annotation.Type.END_TIMED)
	public String testRecordAfterTimedEnd() {
		return "";
	}
	
	
  public static void main(String[] args) {
    System.out.println("Hello World!");
  }
}