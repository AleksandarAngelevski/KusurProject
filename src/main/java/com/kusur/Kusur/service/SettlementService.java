package com.kusur.Kusur.service;


import com.kusur.Kusur.model.Expense;
import com.kusur.Kusur.model.Settlement;
import com.kusur.Kusur.repository.SettlementRepository;
import jakarta.persistence.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SettlementService {

    @Autowired
    NetBalanceCalculatorService netBalanceCalculatorService;
    public void calculateSettlement(Settlement settlement){

    }
}
