package com.example.batch1;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@EnableBatchProcessing
@Configuration
public class BatchConfiguration {
	public final JobBuilderFactory jobBuilderFactory;
	
	public final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job helloJob(){
		return jobBuilderFactory.get("helloJob")
				.start(helloStep())
				.next(helloStep2())
				.next(helloStep3())
				.build();
	}
	
	@Bean
	public Job flowJob(){
		return jobBuilderFactory.get("flowJob")
				.start(helloStep())
				.on(BatchStatus.COMPLETED.name()).to(helloStep3())
				.from(helloStep())
				.on("FAILED").to(helloStep2())
				.end()
				.build();
	}

	@Bean
	public Job flowJob2(){
		return jobBuilderFactory.get("flowJob2")
				.start(helloFlow1())
				.next(helloStep2())
				.end()
				.build();
	}

	@Bean
	public Step helloStep(){
		return stepBuilderFactory.get("helloStep")
				.tasklet((contribution, chunkContext) -> {
					System.out.println("hello spring batch 111111");
//					throw new RuntimeException("te");
					return RepeatStatus.FINISHED;
				})
				.build();
	}

	@Bean
	public Step helloStep2(){
		return stepBuilderFactory.get("helloStep2")
				.tasklet((contribution, chunkContext) -> {
					System.out.println("hello spring batch 222222");
					return RepeatStatus.FINISHED;
				})
				.build();
	}

	@Bean
	public Step helloStep3(){
		return stepBuilderFactory.get("helloStep33333")
				.tasklet((contribution, chunkContext) -> {
					System.out.println("hello spring batch 33333");
					return RepeatStatus.FINISHED;
				})
				.build();
	}
	
	@Bean
	public Flow helloFlow1(){
		FlowBuilder<Flow> flowBuilder = new FlowBuilder("helloFlow1111");

		return flowBuilder.from(helloStep3())
				.next(helloStep2())
				.build();
	}
}
