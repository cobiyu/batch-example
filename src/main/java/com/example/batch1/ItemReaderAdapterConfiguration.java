package com.example.batch1;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@RequiredArgsConstructor
@EnableBatchProcessing
@Configuration
public class ItemReaderAdapterConfiguration {
	public final JobBuilderFactory jobBuilderFactory;

	public final StepBuilderFactory stepBuilderFactory;
	
	private final DataSource dataSource;
	private final EntityManagerFactory entityManagerFactory;


	private final int chunkSize =  10;
	@Bean
	public Job itemReaderAdapterJob() throws Exception {
		return jobBuilderFactory.get("itemReaderAdapterJob")
				.start(itemReaderAdapterStep())
				.build();
	}
	
	@Bean
	public Step itemReaderAdapterStep() throws Exception {
		return stepBuilderFactory.get("itemReaderAdapterStep")
				.<String, String>chunk(chunkSize)
				.reader(itemAdapterReader())
				.writer(items -> {
					System.out.println(items);
				})
				.build();
	}
	
	@Bean
	public ItemReader<String> itemAdapterReader() throws Exception {
		ItemReaderAdapter<String> stringItemReaderAdapter = new ItemReaderAdapter<>();

		stringItemReaderAdapter.setTargetObject(customService());
		stringItemReaderAdapter.setTargetMethod("adapterMethod");
		
		return stringItemReaderAdapter;
	}
	
	public CustomService<String> customService(){
		return new CustomService<>();
	}
}
