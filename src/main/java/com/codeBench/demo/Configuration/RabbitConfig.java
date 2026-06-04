package com.codeBench.demo.Configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitConfig {

    public static final String QUEUE_NAME     = "submission_queue";
    public static final String RUN_QUEUE_NAME = "run_queue";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Queue runQueue() {
        return new Queue(RUN_QUEUE_NAME, true);
    }
}
