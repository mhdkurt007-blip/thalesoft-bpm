package com.thalesoft.engine.repository;

import com.thalesoft.engine.model.ProcessTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;



public interface ProcessTemplateRepository extends MongoRepository<ProcessTemplate, String> {
    List<ProcessTemplate> findByStatus(String status);
    Optional<ProcessTemplate> findByTemplateId(String templateId);
}
