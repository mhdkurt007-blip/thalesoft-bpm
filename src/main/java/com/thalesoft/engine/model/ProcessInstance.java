package com.thalesoft.engine.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "process_instances")
public class ProcessInstance {
    
    @Id
    private String id;
    private String templateId; 
    private String initiator;  
    private String currentState; 
    private String status; // ACTIVE, COMPLETED, REJECTED
    
    private Map<String, Object> variables; // Form dataları burada duracak
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> auditLogs = new java.util.ArrayList<>();
}