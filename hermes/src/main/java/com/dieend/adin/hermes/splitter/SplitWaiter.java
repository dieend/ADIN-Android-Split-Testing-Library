package com.dieend.adin.hermes.splitter;

public interface SplitWaiter {
	public void configProgressed(Long percentage);
	public void finished();
}
