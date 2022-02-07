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
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.util.Map;

@RequiredArgsConstructor
@EnableBatchProcessing
@Configuration
public class MultiThreadStepConfiguration {
	public final JobBuilderFactory jobBuilderFactory;

	public final StepBuilderFactory stepBuilderFactory;
	
	private final DataSource dataSource;

	private final int chunkSize =  10;
	@Bean
	public Job multiThreadStepJob() throws Exception {
		return jobBuilderFactory.get("multiThreadStepJob")
				.start(multiThreadStep1())// single thread
				.listener(new JobStopWatchListener())
				.build();
	}


	@Bean
	public Step multiThreadStep1() throws Exception {
		return stepBuilderFactory.get("normalStep")
				.<DealContents, DealContents>chunk(chunkSize)
				.reader(multiThreadItemReader())
				.listener(new MultiThreadItemReaderListner())
				.processor(multiThreadItemProcessor())
				.listener(new MultiThreadItemProcessorListner())
				.writer(multiThreadItemWriter())
				.listener(new MultiThreadItemWriterListner())
				.taskExecutor(taskExecutor())
				.build();
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(3);
		taskExecutor.setMaxPoolSize(9);
		taskExecutor.setThreadNamePrefix("cobi-thread");
		
		return taskExecutor;
	}

	@Bean
	public ItemProcessor multiThreadItemProcessor(){
		return item -> {
			return item;
		};
	}
	
	@Bean
	public ItemWriter<DealContents> multiThreadItemWriter(){
		return new JdbcBatchItemWriterBuilder<DealContents>()
				.dataSource(dataSource)
				.sql("insert into deal_contents2 (deal_contents_name, column_count, width) values (:dealContentsName, :columnCount, :width)")
				.beanMapped()
				.build();

	}

	// only thread safe reader
	@Bean
	public ItemReader<DealContents> multiThreadItemReader() throws Exception {
		return new JdbcPagingItemReaderBuilder<DealContents>()
				.name("jdbcPagingItemReader")
				.pageSize(chunkSize)
				.dataSource(dataSource)
				.rowMapper(new BeanPropertyRowMapper<>(DealContents.class))
				.queryProvider(createQueryProvider())
				.build();
	}

	public PagingQueryProvider createQueryProvider() throws Exception {
		SqlPagingQueryProviderFactoryBean sqlPagingQueryProviderFactoryBean
				= new SqlPagingQueryProviderFactoryBean();

		sqlPagingQueryProviderFactoryBean.setDataSource(dataSource);
		sqlPagingQueryProviderFactoryBean.setSelectClause("seq, deal_contents_name, column_count, width");
		sqlPagingQueryProviderFactoryBean.setFromClause("adico.deal_contents");

		Map<String, Order> sortKeys = Map.of("seq", Order.DESCENDING);
		sqlPagingQueryProviderFactoryBean.setSortKeys(sortKeys);

		return sqlPagingQueryProviderFactoryBean.getObject();
	}
}
