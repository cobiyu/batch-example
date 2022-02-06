package com.example.batch1;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@EnableBatchProcessing
@Configuration
public class ChunkConfiguration {
	public final JobBuilderFactory jobBuilderFactory;

	public final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job chunkJob(){
		return jobBuilderFactory.get("chunkJob")
				.start(chunkStep())
				.build();
	}
	
	@Bean
	public Step chunkStep(){
		return stepBuilderFactory.get("chunkStep")
				.<String, String>chunk(4)
				.reader(itemReader())
				.writer(itemWriter())
				.build();
	}
	
	@Bean
	public ItemReader<String> itemReader(){
		List<String> list = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			list.add(String.valueOf(i));
		}
		return new CustomItemStreamReader(list);
	}
	
	@Bean
	public ItemWriter<String> itemWriter(){
		return new CustomItemStreamWriter();
	}
}
