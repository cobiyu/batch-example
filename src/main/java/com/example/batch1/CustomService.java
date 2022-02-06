package com.example.batch1;

public class CustomService<T> {
	private int count = 0;

	public T adapterMethod(){
		if(count>100){
			return null;
		}
		return (T)("item" + count++);
	}

	public void adapterWriterMethod(T item){
		System.out.println("customer service writer" + item);
	}
}
