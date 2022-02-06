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
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@RequiredArgsConstructor
@EnableBatchProcessing
@Configuration
public class FlatFileItemWriterConfiguration {
	public final JobBuilderFactory jobBuilderFactory;

	public final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job flatFileWriterJob(){
		return jobBuilderFactory.get("flatFileWriterJob")
				.start(flatFileWriterStep())
				.build();
	}
	
	@Bean
	public Step flatFileWriterStep(){
		return stepBuilderFactory.get("flatFileWriterStep")
				.<String, String>chunk(3)
				.reader(flatFileWriterItemReader())
				.writer(flatFileWriterItemWriter())
				.build();
	}
	
	@Bean
	public ItemReader flatFileWriterItemReader(){
		FlatFileItemReader<Person> itemReader = new FlatFileItemReader<>();
		itemReader.setResource(new ClassPathResource("/sample-data.csv"));
		
		DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();
		lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
		lineMapper.setFieldSetMapper(new CustomerFieldSetMapper());
		
		itemReader.setLineMapper(lineMapper);
		itemReader.setLinesToSkip(1);
		
		return itemReader;
	} 
	
	@Bean
	public ItemWriter flatFileWriterItemWriter(){
		return new FlatFileItemWriterBuilder<>()
				.name("flatFileWriter")
				.resource(new FileSystemResource("../../sample-data-writer.csv"))
				.delimited()
				.delimiter(",")
				.names(new String[]{"lastName","firstName"})
				.build();
	}
}
