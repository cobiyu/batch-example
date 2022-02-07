package com.example.batch1;

import org.springframework.batch.core.ItemProcessListener;

public class MultiThreadItemProcessorListner implements ItemProcessListener<DealContents, DealContents> {
	@Override
	public void beforeProcess(DealContents item) {
		
	}

	@Override
	public void afterProcess(DealContents item, DealContents result) {
		System.out.println("Thread : " + Thread.currentThread().getName() + " / process item : " + item);
	}

	@Override
	public void onProcessError(DealContents item, Exception e) {

	}
}
