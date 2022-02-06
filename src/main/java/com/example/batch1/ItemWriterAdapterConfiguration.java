package com.example.batch1;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@RequiredArgsConstructor
@EnableBatchProcessing
@Configuration
public class ItemWriterAdapterConfiguration {
	public final JobBuilderFactory jobBuilderFactory;

	public final StepBuilderFactory stepBuilderFactory;
	
	private final DataSource dataSource;
	private final EntityManagerFactory entityManagerFactory;


	private final int chunkSize =  10;
	@Bean
	public Job itemWriterAdapterJob() throws Exception {
		return jobBuilderFactory.get("itemWriterAdapterJob")
				.start(itemWriterAdapterStep())
				.build();
	}
	
	@Bean
	public Step itemWriterAdapterStep() throws Exception {
		return stepBuilderFactory.get("itemWriterAdapterStep")
				.<String, String>chunk(chunkSize)
				.reader(itemAdapterReaderForWriter())
				.writer(itemAdapterWriter())
				.build();
	}

	@Bean
	public ItemWriter<? super String> itemAdapterWriter() {
		ItemWriterAdapter<String> stringItemWriterAdapter = new ItemWriterAdapter<>();

		stringItemWriterAdapter.setTargetObject(customService());
		stringItemWriterAdapter.setTargetMethod("adapterWriterMethod");

		return stringItemWriterAdapter;
	}

	@Bean
	public ItemReader<String> itemAdapterReaderForWriter() throws Exception {
		ItemReaderAdapter<String> stringItemReaderAdapter = new ItemReaderAdapter<>();

		stringItemReaderAdapter.setTargetObject(customService());
		stringItemReaderAdapter.setTargetMethod("adapterMethod");
		
		return stringItemReaderAdapter;
	}
	
	public CustomService<String> customService(){
		return new CustomService<>();
	}
}
