package com.thalesoft.engine.action;

import com.thalesoft.engine.model.ProcessInstance;
import com.thalesoft.engine.model.ProcessTemplate; // Bunu ekledik

public interface ServiceTaskAction {
    // stateDef parametresini ekledik ki URL'leri okuyabilelim
    void execute(ProcessInstance instance, ProcessTemplate.StateDefinition stateDef);
    
    String getActionName();
}