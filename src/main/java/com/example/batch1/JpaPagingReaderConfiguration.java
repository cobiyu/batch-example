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
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@RequiredArgsConstructor
@EnableBatchProcessing
@Configuration
public class JpaPagingReaderConfiguration {
	public final JobBuilderFactory jobBuilderFactory;

	public final StepBuilderFactory stepBuilderFactory;
	
	private final DataSource dataSource;
	private final EntityManagerFactory entityManagerFactory;


	private final int chunkSize =  10;
	@Bean
	public Job jpaItemWriterJob() throws Exception {
		return jobBuilderFactory.get("jpaItemWriterJob")
				.start(jpaPagingReaderStepForJpaWriter())
				.build();
	}
	
	@Bean
	public Step jpaPagingReaderStepForJpaWriter() throws Exception {
		return stepBuilderFactory.get("jpaPagingReaderStep")
				.<DealContentsEntity, DealContentsEntity2>chunk(chunkSize)
				.reader(dealContentsJpaPagingItemReaderForJpaWriter())
				.processor((ItemProcessor<? super DealContentsEntity, ? extends DealContentsEntity2>) dealContentsEntity -> 
						new DealContentsEntity2(
								null,
								dealContentsEntity.getDealContentsName(),
								dealContentsEntity.getColumnCount(),
								dealContentsEntity.getWidth()
						)
				)
				.writer(jpaItemWriter())
				.build();
	}

	@Bean
	public ItemWriter<? super DealContentsEntity2> jpaItemWriter() {
		return new JpaItemWriterBuilder<DealContentsEntity2>()
				.usePersist(true)
				.entityManagerFactory(entityManagerFactory)
				.build();
	}

	@Bean
	public ItemReader<DealContentsEntity> dealContentsJpaPagingItemReaderForJpaWriter() throws Exception {
		return new JpaPagingItemReaderBuilder<DealContentsEntity>()
				.name("jpaPagingItemReaderForJpaWriter")
				.pageSize(chunkSize)
				.entityManagerFactory(entityManagerFactory)
				.queryString("select d from DealContentsEntity d")
				.build();
	}
}
