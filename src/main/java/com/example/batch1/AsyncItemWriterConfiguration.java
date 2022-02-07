package com.example.batch1;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.Map;

@RequiredArgsConstructor
@EnableBatchProcessing
@Configuration
public class AsyncItemWriterConfiguration {
	public final JobBuilderFactory jobBuilderFactory;

	public final StepBuilderFactory stepBuilderFactory;
	
	private final DataSource dataSource;

	private final int chunkSize =  10;
	@Bean
	public Job asyncWriterReaderJob() throws Exception {
		return jobBuilderFactory.get("asyncWriterReaderJob")
//				.start(normalStep())
				.start(asyncWriterReaderStep())
				.listener(new JobStopWatchListener())
				.build();
	}
	
	@Bean
	public Step asyncWriterReaderStep() throws Exception {
		return stepBuilderFactory.get("asyncWriterReaderStep")
				.<DealContents, DealContents>chunk(chunkSize)
				.reader(asyncWriterItemReader())
				.processor(asyncItemProcessor())
				.writer(asyncItemWriter())
				.build();
	}

	@Bean
	public Step normalStep() throws Exception {
		return stepBuilderFactory.get("normalStep")
				.<DealContents, DealContents>chunk(chunkSize)
				.reader(asyncWriterItemReader())
				.processor(normalItemProcessor())
				.writer(normalItemWriter())
				.build();
	}
	
	@Bean
	public ItemProcessor normalItemProcessor(){
		return item -> {
			Thread.sleep(50);
			System.out.println(item);
			return item;
		};
	}
	
	@Bean
	public ItemWriter<DealContents> normalItemWriter(){
		return new JdbcBatchItemWriterBuilder<DealContents>()
				.dataSource(dataSource)
				.sql("insert into deal_contents2 (deal_contents_name, column_count, width) values (:dealContentsName, :columnCount, :width)")
				.beanMapped()
				.build();
	}

	@Bean
	public AsyncItemProcessor asyncItemProcessor() {
		AsyncItemProcessor<DealContents, DealContents> asyncItemProcessor = new AsyncItemProcessor<>();
		asyncItemProcessor.setDelegate(normalItemProcessor());
		asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());
		
		
		return asyncItemProcessor; 
	}

	@Bean
	public AsyncItemWriter<? super DealContents> asyncItemWriter() {
		AsyncItemWriter<DealContents> asyncItemWriter = new AsyncItemWriter<>();
		asyncItemWriter.setDelegate(normalItemWriter());
		return asyncItemWriter;
	}

	@Bean
	public ItemReader<DealContents> asyncWriterItemReader() throws Exception {
		return new JdbcCursorItemReaderBuilder<DealContents>()
				.name("jdbcCursorItemReader")
				.fetchSize(chunkSize)
				.sql("select seq, deal_contents_name, column_count, width from adico.deal_contents where width >= ? order by seq")
				.beanRowMapper(DealContents.class)
				.queryArguments("1")
				.dataSource(dataSource)
				.build();
	}
}
