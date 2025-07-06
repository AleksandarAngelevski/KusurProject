package com.kusur.Kusur.dto;

import java.util.List;

public record ExpenseDto(String groupId, String description, Double amount, List<String> users) {

}
