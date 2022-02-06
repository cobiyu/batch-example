package com.example.batch1;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;

@RequiredArgsConstructor
@EnableBatchProcessing
@Configuration
public class JpaCursorReaderConfiguration {
	public final JobBuilderFactory jobBuilderFactory;

	public final StepBuilderFactory stepBuilderFactory;
	
	private final DataSource dataSource;
	private final EntityManagerFactory entityManagerFactory;

	private final int chunkSize =  10;
	@Bean
	public Job jpaReaderJob(){
		return jobBuilderFactory.get("jpaReaderJob")
				.start(jpaReaderStep())
				.build();
	}
	
	@Bean
	public Step jpaReaderStep(){
		return stepBuilderFactory.get("jpaReaderStep")
				.<DealContentsEntity, DealContentsEntity>chunk(chunkSize)
				.reader(dealContentsJpaItemReader())
				.writer(items -> {
					System.out.println("dela_contents = " + items);
				})
				.build();
	}
	
	@Bean
	public ItemReader<DealContentsEntity> dealContentsJpaItemReader(){
		Map<String, Object> parameters = Map.of(
				"width", 1
		);
		
		return new JpaCursorItemReaderBuilder<DealContentsEntity>()
				.name("jpaCursorItemReader")
				.entityManagerFactory(entityManagerFactory)
				.queryString("select d from DealContentsEntity d  where width >= :width")
				.parameterValues(parameters)
				.build();
	} 
}
