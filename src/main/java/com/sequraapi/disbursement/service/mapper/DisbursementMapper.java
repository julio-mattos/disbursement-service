package com.sequraapi.disbursement.service.mapper;

import com.sequraapi.disbursement.service.controller.reponse.DisbursementReportsResponse;
import com.sequraapi.disbursement.service.entity.DisbursementEntity;
import com.sequraapi.disbursement.service.entity.MerchantEntity;
import com.sequraapi.disbursement.service.entity.OrderEntity;
import com.sequraapi.disbursement.service.enums.StatusMinimumFeeEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;

@Mapper(imports = {BigDecimal.class, StatusMinimumFeeEnum.class})
public interface DisbursementMapper {

    DisbursementMapper INSTANCE = Mappers.getMapper(DisbursementMapper.class);

    @Mapping(target = "totalDisbursement", expression = "java(mapTotalDisbursement(orderList, disbursement.getTotalDisbursement()))")
    @Mapping(target = "totalFee", expression = "java(mapTotalFee(orderList, disbursement.getTotalFee()))")
    @Mapping(target = "totalAmount", expression = "java(mapTotalAmount(orderList, disbursement.getTotalAmount()))")
    @Mapping(target = "id", ignore = true)
    void mapUpdatedDisbursement(@MappingTarget DisbursementEntity disbursement, List<OrderEntity> orderList, MerchantEntity merchant);

    @Mapping(target = "totalDisbursement", expression = "java(mapTotalDisbursement(orderList, 0))")
    @Mapping(target = "totalFee", expression = "java(mapTotalFee(orderList, BigDecimal.ZERO))")
    @Mapping(target = "totalAmount", expression = "java(mapTotalAmount(orderList, BigDecimal.ZERO))")
    @Mapping(target = "merchantId", source =" merchant.id")
    @Mapping(target = "year", source = "yearMonth.year")
    @Mapping(target = "month", source = "yearMonth.monthValue")
    @Mapping(target = "statusMinimumFee", expression = "java(StatusMinimumFeeEnum.PENDING)")
    @Mapping(target = "id", ignore = true)
    DisbursementEntity mapNewDisbursement(List<OrderEntity> orderList, MerchantEntity merchant, YearMonth yearMonth);


    @Mapping(target = "year", source = "year")
    @Mapping(target = "numberOfDisbursements", expression = "java(mapNumberOfDisbursement(disbursementList))")
    @Mapping(target = "amountDisbursedToMerchants", expression = "java(mapAmountDisbursedToMerchants(disbursementList))")
    @Mapping(target = "amountOfOrdersFees", expression = "java(mapAmountOfOrdersFees(disbursementList))")
    @Mapping(target = "monthlyFeesCharged", expression = "java(mapMonthlyFeesCharged(disbursementList))")
    @Mapping(target = "amountMonthlyFeeCharged", expression = "java(mapAmountMonthlyFeeCharged(disbursementList))")
    DisbursementReportsResponse map(List<DisbursementEntity> disbursementList, Integer year);

    default BigDecimal mapTotalFee(List<OrderEntity> orderList, BigDecimal initialValue){
        return orderList.stream()
                .map(OrderEntity::getFeeAmount)
                .reduce(initialValue, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    default Integer mapMonthlyFeesCharged(List<DisbursementEntity> disbursementList){
        return disbursementList.stream()
                .map(m -> m.getIsMinimumFeeChargeable() ? 1 : 0)
                .reduce(0, Integer::sum);
    }

    default Integer mapNumberOfDisbursement(List<DisbursementEntity> disbursementList){
        return disbursementList.stream()
                .map(DisbursementEntity::getTotalDisbursement)
                .reduce(0, Integer::sum);
    }

    default BigDecimal mapAmountDisbursedToMerchants(List<DisbursementEntity> disbursementList){
        return disbursementList.stream()
                .map(DisbursementEntity::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    default BigDecimal mapAmountOfOrdersFees(List<DisbursementEntity> disbursementList){
        return disbursementList.stream()
                .map(DisbursementEntity::getTotalFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    default BigDecimal mapAmountMonthlyFeeCharged(List<DisbursementEntity> disbursementList){
        return disbursementList.stream().map(DisbursementEntity::getAmountFeeChargeable).reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    default BigDecimal mapTotalAmount(List<OrderEntity> orderList, BigDecimal initialValue){
        return orderList.stream()
                .map(OrderEntity::getAmount)
                .reduce(initialValue, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    default Integer mapTotalDisbursement(List<OrderEntity> orderList, int initialValue){
        return orderList.stream().map(m-> 1).reduce(initialValue, Integer::sum);
    }
}
