package com.thalesoft.engine.action;

import com.thalesoft.engine.model.ProcessInstance;
import com.thalesoft.engine.model.ProcessTemplate;

import org.springframework.stereotype.Component;

@Component
public class SaveToHrSystemAction implements ServiceTaskAction {

    @Override
    public void execute(ProcessInstance instance, ProcessTemplate.StateDefinition stateDef) {
        // Gerçek bir senaryoda burada başka bir mikroservise REST isteği atılır veya veritabanına kayıt atılır.
        System.out.println(">>> SİSTEM OTOMASYONU ÇALIŞIYOR: İzin talebi İK sistemine kaydedildi!");
        System.out.println(">>> İlgili Süreç ID: " + instance.getId());
    }

    @Override
    public String getActionName() {
        return "SAVE_TO_HR_SYSTEM";
    }
}