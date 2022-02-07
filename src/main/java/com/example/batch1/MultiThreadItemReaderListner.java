package com.example.batch1;

import org.springframework.batch.core.ItemReadListener;

public class MultiThreadItemReaderListner implements ItemReadListener<DealContents> {
	@Override
	public void beforeRead() {
		
	}

	@Override
	public void afterRead(DealContents item) {
		System.out.println("Thread : " + Thread.currentThread().getName() + " / read item : " + item);
	}

	@Override
	public void onReadError(Exception ex) {

	}
}
