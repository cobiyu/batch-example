package com.example.batch1;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class CustomItemStreamWriter implements ItemStreamWriter<String> {
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		System.out.println("open");
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		System.out.println("update");
	}

	@Override
	public void close() throws ItemStreamException {
		System.out.println("writer close");
	}

	@Override
	public void write(List<? extends String> items) throws Exception {
		System.out.println("write");
		System.out.println(items);
	}
}
