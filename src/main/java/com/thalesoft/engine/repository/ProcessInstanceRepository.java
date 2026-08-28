package com.thalesoft.engine.repository;

import com.thalesoft.engine.model.ProcessInstance;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProcessInstanceRepository extends MongoRepository<ProcessInstance, String> {
}