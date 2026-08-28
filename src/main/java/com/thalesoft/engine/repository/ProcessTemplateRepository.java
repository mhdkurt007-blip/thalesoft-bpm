package com.thalesoft.engine.repository;

import com.thalesoft.engine.model.ProcessTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ProcessTemplateRepository extends MongoRepository<ProcessTemplate, String> {
    Optional<ProcessTemplate> findByTemplateId(String templateId);
}