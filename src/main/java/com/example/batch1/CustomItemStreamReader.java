package com.example.batch1;

import org.springframework.batch.item.*;

import java.util.List;

public class CustomItemStreamReader implements ItemStreamReader<String> {
	private final List<String> list;
	private boolean restart = false;
	private int index = -1;
	
	public CustomItemStreamReader(List<String> list) {
		this.list = list;
	}

	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		String item = null;
		
		if(this.index < this.list.size()){
			item = list.get(index++);
		}
		
		if(this.index == 6 && !restart){
			throw new RuntimeException("testest");
		}
		
		return item;
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		if(executionContext.containsKey("index")) {
			this.index = executionContext.getInt("index");
			this.restart = true;
		} else{
			index = 0;
			executionContext.put("index", index);
		}
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		executionContext.put("index", index);
	}

	@Override
	public void close() throws ItemStreamException {
		System.out.println("close");
	}
}
