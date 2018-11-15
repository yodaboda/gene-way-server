package com.nutrinfomics.geneway.server.domain.plan;

public interface Repetition {
	public int getCycleLength();
	public int getRemainingLength();
	public void advanceBySingleUnit();
}
