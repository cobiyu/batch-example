package com.example.batch1;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@EnableBatchProcessing
@Configuration
public class FlatFilesConfiguration {
	public final JobBuilderFactory jobBuilderFactory;

	public final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job flatFileJob(){
		return jobBuilderFactory.get("flatFileJob")
				.start(flatStep())
				.build();
	}
	
	@Bean
	public Step flatStep(){
		return stepBuilderFactory.get("flatStep")
				.<String, String>chunk(3)
				.reader(flatFileItemReader())
				.writer(items -> {
					System.out.println("items = " + items);
				})
				.build();
	}
	
	@Bean
	public ItemReader flatFileItemReader(){
		FlatFileItemReader<Person> itemReader = new FlatFileItemReader<>();
		itemReader.setResource(new ClassPathResource("/sample-data.csv"));
		
		DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();
		lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
		lineMapper.setFieldSetMapper(new CustomerFieldSetMapper());
		
		itemReader.setLineMapper(lineMapper);
		itemReader.setLinesToSkip(1);
		
		return itemReader;
	} 
}
