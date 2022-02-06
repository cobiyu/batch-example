package com.example.batch1;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.Map;

@RequiredArgsConstructor
@EnableBatchProcessing
@Configuration
public class JdbcBatchItemWriterConfiguration {
	public final JobBuilderFactory jobBuilderFactory;

	public final StepBuilderFactory stepBuilderFactory;
	
	private final DataSource dataSource;

	private final int chunkSize =  10;
	@Bean
	public Job jdbcItemWriterReaderJob() throws Exception {
		return jobBuilderFactory.get("jdbcItemWriterReaderJob")
				.start(jdbcItemWriterReaderStep())
				.build();
	}
	
	@Bean
	public Step jdbcItemWriterReaderStep() throws Exception {
		return stepBuilderFactory.get("jdbcItemWriterReaderStep")
				.<DealContents, DealContents>chunk(chunkSize)
				.reader(dealContentsPagingItemReaderForWriter())
				.writer(jdbcItemWriter())
				.build();
	}
	
	@Bean
	public ItemWriter<? super DealContents> jdbcItemWriter(){
		return new JdbcBatchItemWriterBuilder<>()
				.dataSource(dataSource)
				.sql("insert into deal_contents2 (deal_contents_name, column_count, width) values (:dealContentsName, :columnCount, :width)")
				.beanMapped()
				.build();
	}
	
	@Bean
	public ItemReader<DealContents> dealContentsPagingItemReaderForWriter() throws Exception {
		Map<String, Object> param = Map.of("width", 1);


		return new JdbcPagingItemReaderBuilder<DealContents>()
				.name("jdbcItemWriterItemReader")
				.pageSize(chunkSize)
				.dataSource(dataSource)
				.rowMapper(new BeanPropertyRowMapper<>(DealContents.class))
				.queryProvider(createQueryProvider())
				.parameterValues(param)
				.build();
	} 
	
	public PagingQueryProvider createQueryProvider() throws Exception {
		SqlPagingQueryProviderFactoryBean sqlPagingQueryProviderFactoryBean 
				= new SqlPagingQueryProviderFactoryBean();

		sqlPagingQueryProviderFactoryBean.setDataSource(dataSource);
		sqlPagingQueryProviderFactoryBean.setSelectClause("seq, deal_contents_name, column_count, width");
		sqlPagingQueryProviderFactoryBean.setFromClause("adico.deal_contents");
		sqlPagingQueryProviderFactoryBean.setWhereClause("width >= :width");

		Map<String, Order> sortKeys = Map.of("seq", Order.DESCENDING);
		sqlPagingQueryProviderFactoryBean.setSortKeys(sortKeys);
		
		return sqlPagingQueryProviderFactoryBean.getObject();
	}
}
