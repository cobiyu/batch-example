package com.example.batch1;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@RequiredArgsConstructor
@EnableBatchProcessing
@Configuration
public class JdbcCursorReaderConfiguration {
	public final JobBuilderFactory jobBuilderFactory;

	public final StepBuilderFactory stepBuilderFactory;
	
	private final DataSource dataSource;

	private final int chunkSize =  10;
	@Bean
	public Job jdbcReaderJob(){
		return jobBuilderFactory.get("jdbcReaderJob")
				.start(jdbcReaderStep())
				.build();
	}
	
	@Bean
	public Step jdbcReaderStep(){
		return stepBuilderFactory.get("jdbcReaderStep")
				.<DealContents, DealContents>chunk(chunkSize)
				.reader(dealContentsItemReader())
				.writer(items -> {
					System.out.println("dela_contents = " + items);
				})
				.build();
	}
	
	@Bean
	public ItemReader<DealContents> dealContentsItemReader(){
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
