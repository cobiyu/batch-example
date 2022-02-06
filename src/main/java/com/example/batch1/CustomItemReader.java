package com.example.batch1;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.ArrayList;
import java.util.List;

public class CustomItemReader implements ItemReader<String> {
	private List<String> personList = new ArrayList<>();

	public CustomItemReader(List<String> personList) {
		this.personList.addAll(personList);
	}

	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if(!personList.isEmpty()) {
			System.out.println("read : " + personList.get(0));
			return personList.remove(0);
		}
		return null;
	}
}
