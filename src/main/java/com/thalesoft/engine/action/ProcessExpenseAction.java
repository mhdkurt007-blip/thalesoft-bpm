package com.thalesoft.engine.action;

import com.thalesoft.engine.model.ProcessInstance;
import com.thalesoft.engine.model.ProcessTemplate;

import org.springframework.stereotype.Component;

@Component
public class ProcessExpenseAction implements ServiceTaskAction {

    @Override
    public void execute(ProcessInstance instance, ProcessTemplate.StateDefinition stateDef) {
        System.out.println(">>> SİSTEM OTOMASYONU (MUHASEBE): Masraf onaylandı. ERP sistemine ödeme emri iletildi ve çalışanın hesabına aktarım başlatıldı.");
        System.out.println(">>> Süreç ID: " + instance.getId());
    }

    @Override
    public String getActionName() {
        return "PROCESS_EXPENSE";
    }
}