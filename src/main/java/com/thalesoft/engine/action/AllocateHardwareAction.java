package com.thalesoft.engine.action;

import com.thalesoft.engine.model.ProcessInstance;
import org.springframework.stereotype.Component;
import com.thalesoft.engine.model.ProcessTemplate;
@Component
public class AllocateHardwareAction implements ServiceTaskAction {

    @Override
    public void execute(ProcessInstance instance, ProcessTemplate.StateDefinition stateDef) {
        System.out.println(">>> SİSTEM OTOMASYONU (IT): IT Envanter sistemine kayıt açıldı. Talep edilen donanım (Laptop/Monitör) depodan ayrıldı.");
        System.out.println(">>> Süreç ID: " + instance.getId());
    }

    @Override
    public String getActionName() {
        return "ALLOCATE_HARDWARE";
    }
}