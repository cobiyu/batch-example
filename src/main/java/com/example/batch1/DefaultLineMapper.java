package com.example.batch1;

import lombok.Setter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.LineTokenizer;

public class DefaultLineMapper<T> implements LineMapper<T> {
	@Setter
	private LineTokenizer lineTokenizer;
	@Setter
	private FieldSetMapper<T> fieldSetMapper;
	
	
	@Override
	public T mapLine(String line, int lineNumber) throws Exception {
		return fieldSetMapper.mapFieldSet(lineTokenizer.tokenize(line));
	}
}
