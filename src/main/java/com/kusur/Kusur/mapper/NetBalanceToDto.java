package com.kusur.Kusur.mapper;

import com.kusur.Kusur.dto.BalanceDto;
import com.kusur.Kusur.dto.UserDetailsDto;
import com.kusur.Kusur.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NetBalanceToDto {
    @Mapping(source ="text",target = "message")
    BalanceDto balanceDto(UserDetailsDto debtor,UserDetailsDto debtee, String text,Double amount);
}
