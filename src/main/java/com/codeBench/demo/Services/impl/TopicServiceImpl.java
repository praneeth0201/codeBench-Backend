package com.codeBench.demo.Services.impl;

import com.codeBench.demo.DAO.TopicRepository;
import com.codeBench.demo.DTO.TopicRequest;
import com.codeBench.demo.DTO.TopicResponse;
import com.codeBench.demo.Entity.Topic;
import com.codeBench.demo.Services.TopicService;
import com.codeBench.demo.exception.DuplicateResourceException;
import com.codeBench.demo.exception.ResourceNotFoundException;
import com.codeBench.demo.util.SlugUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    public TopicServiceImpl(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Override
    @Transactional
    public TopicResponse createTopic(TopicRequest request) {
        String slug = SlugUtil.generateSlug(request.getName());
        if (topicRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Topic with this name already exists.");
        }

        Topic topic = new Topic();
        topic.setName(request.getName());
        topic.setSlug(slug);
        topic.setDescription(request.getDescription());

        return toResponse(topicRepository.save(topic));
    }

    @Override
    @Transactional
    public TopicResponse updateTopic(Long id, TopicRequest request) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        String newSlug = SlugUtil.generateSlug(request.getName());
        if (!topic.getSlug().equals(newSlug) && topicRepository.existsBySlug(newSlug)) {
            throw new DuplicateResourceException("Another topic with this name already exists.");
        }

        topic.setName(request.getName());
        topic.setSlug(newSlug);
        topic.setDescription(request.getDescription());

        return toResponse(topicRepository.save(topic));
    }

    @Override
    @Transactional
    public void deleteTopic(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
        topicRepository.delete(topic);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopicResponse> getAllTopics() {
        return topicRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TopicResponse getTopicBySlug(String slug) {
        return topicRepository.findBySlug(slug)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
    }

    private TopicResponse toResponse(Topic topic) {
        return TopicResponse.builder()
                .id(topic.getId())
                .name(topic.getName())
                .slug(topic.getSlug())
                .description(topic.getDescription())
                .createdAt(topic.getCreatedAt())
                .build();
    }
}
