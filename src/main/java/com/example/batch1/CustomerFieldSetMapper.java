package com.example.batch1;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class CustomerFieldSetMapper implements FieldSetMapper<Person> {
	@Override
	public Person mapFieldSet(FieldSet fieldSet) throws BindException {
		if(fieldSet==null){
			return null;
		}
		
		
		return new Person(fieldSet.readString(0), fieldSet.readString(1));
	}
}
