package com.codeBench.demo.Services;

import com.codeBench.demo.DTO.TopicRequest;
import com.codeBench.demo.DTO.TopicResponse;

import java.util.List;

public interface TopicService {

    TopicResponse createTopic(TopicRequest request);
    TopicResponse updateTopic(Long id, TopicRequest request);
    void deleteTopic(Long id);

    List<TopicResponse> getAllTopics();
    TopicResponse getTopicBySlug(String slug);
}
