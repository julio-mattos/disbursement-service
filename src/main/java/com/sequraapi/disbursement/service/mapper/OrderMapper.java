package com.sequraapi.disbursement.service.mapper;

import com.sequraapi.disbursement.service.controller.request.OrderRequest;
import com.sequraapi.disbursement.service.entity.OrderEntity;
import com.sequraapi.disbursement.service.enums.StatusEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

@Mapper
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "createdAt", expression = "java(localDateNow())")
    @Mapping(target = "status", expression = "java(setPendingStatus())")
    OrderEntity map(OrderRequest orderRequest);


    default LocalDate localDateNow(){
       return LocalDate.now();
    }

    default StatusEnum setPendingStatus(){
        return StatusEnum.PENDING;
    }


}
