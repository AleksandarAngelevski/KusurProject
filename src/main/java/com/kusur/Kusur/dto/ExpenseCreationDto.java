package com.kusur.Kusur.dto;

import java.util.List;

public record ExpenseCreationDto(String userId,String groupId, String description, Double amount, List<String> users) {

}
