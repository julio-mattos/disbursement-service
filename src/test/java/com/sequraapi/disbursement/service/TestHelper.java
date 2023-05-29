package com.sequraapi.disbursement.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sequraapi.disbursement.service.controller.reponse.DisbursementReportsResponse;
import com.sequraapi.disbursement.service.entity.DisbursementEntity;
import com.sequraapi.disbursement.service.entity.MerchantEntity;
import com.sequraapi.disbursement.service.entity.OrderEntity;
import com.sequraapi.disbursement.service.enums.DisbursementFrequencyEnum;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestHelper {

    public static List<MerchantEntity> getMerchantDaily(){
        return List.of(MerchantEntity.builder().id("wintheiser_bernhard")
                .email("info@wintheiser-bernhard.com")
                .liveOn(LocalDate.of(2022,12,1))
                .disbursementFrequency(DisbursementFrequencyEnum.DAILY)
                .minimumMonthlyFee(BigDecimal.valueOf(15))
                .build());
    }

    public static MerchantEntity getOneMerchantDaily(){
        return MerchantEntity.builder().id("wintheiser_bernhard")
                .email("info@wintheiser-bernhard.com")
                .liveOn(LocalDate.of(2022,12,1))
                .disbursementFrequency(DisbursementFrequencyEnum.DAILY)
                .minimumMonthlyFee(BigDecimal.valueOf(15))
                .build();
    }

    public static MerchantEntity getOneMerchantWeekly(){
        return MerchantEntity.builder().id("vandervort_kiehn")
                .email("info@vandervort-kiehn.com")
                .liveOn(LocalDate.of(2022,10,9))
                .disbursementFrequency(DisbursementFrequencyEnum.WEEKLY)
                .minimumMonthlyFee(BigDecimal.valueOf(15))
                .build();
    }

    public static List<MerchantEntity> getMerchantWeekly(){
        return List.of(MerchantEntity.builder().id("vandervort_kiehn")
                .email("info@vandervort-kiehn.com")
                .liveOn(LocalDate.of(2022,10,9))
                .disbursementFrequency(DisbursementFrequencyEnum.WEEKLY)
                .minimumMonthlyFee(BigDecimal.valueOf(15))
                .build());
    }

    public static List<OrderEntity>getOrdersDaily(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            Path filePath = Paths.get("src/test/java/com/sequraapi/disbursement/service/resource/orders_daily.json");
            byte[] jsonData = Files.readAllBytes(filePath);
            return objectMapper.readValue(jsonData, new TypeReference<List<OrderEntity>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static List<OrderEntity>getOrdersWeekly(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            Path filePath = Paths.get("src/test/java/com/sequraapi/disbursement/service/resource/orders_weekly.json");
            byte[] jsonData = Files.readAllBytes(filePath);
            return objectMapper.readValue(jsonData, new TypeReference<List<OrderEntity>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static List<DisbursementEntity>getDisbursementList(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            Path filePath = Paths.get("src/test/java/com/sequraapi/disbursement/service/resource/disbursement.json");
            byte[] jsonData = Files.readAllBytes(filePath);
            return objectMapper.readValue(jsonData, new TypeReference<List<DisbursementEntity>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static List<DisbursementReportsResponse> getListReports() {
        return List.of(DisbursementReportsResponse.builder().year(2022)
                        .numberOfDisbursements(170672)
                        .amountDisbursedToMerchants(BigDecimal.valueOf(12830601.68))
                        .amountOfOrdersFees(BigDecimal.valueOf(118836.58))
                        .monthlyFeesCharged(8)
                        .amountMonthlyFeeCharged(BigDecimal.valueOf(141.36)).build(),
                DisbursementReportsResponse.builder().year(2023)
                        .numberOfDisbursements(110016)
                        .amountDisbursedToMerchants(BigDecimal.valueOf(9972431.22))
                        .amountOfOrdersFees(BigDecimal.valueOf(92108.64))
                        .monthlyFeesCharged(9)
                        .amountMonthlyFeeCharged(BigDecimal.valueOf(150.68)).build());
    }

}
