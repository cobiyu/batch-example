package com.example.batch1;

import org.springframework.batch.core.ItemWriteListener;

import java.util.List;

public class MultiThreadItemWriterListner implements ItemWriteListener<DealContents> {
	@Override
	public void beforeWrite(List<? extends DealContents> items) {
		
	}

	@Override
	public void afterWrite(List<? extends DealContents> items) {
		System.out.println("Thread : " + Thread.currentThread().getName() + " / write item size : " + items.size());
	}

	@Override
	public void onWriteError(Exception exception, List<? extends DealContents> items) {

	}
}
