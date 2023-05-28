package com.sequraapi.disbursement.service.entity;

import com.sequraapi.disbursement.service.enums.StatusMinimumFeeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@Getter
@Setter
@Document("disbursement")
@NoArgsConstructor
@AllArgsConstructor
public class DisbursementEntity {
    @Id
    private String id;
    private int year;
    private int month;
    @Field("merchant_reference")
    private String merchantId;
    @Field("total_disbursement")
    private int totalDisbursement;
    @Field("total_fee")
    private BigDecimal totalFee;
    @Field("total_amount")
    private BigDecimal totalAmount;
    @Field("status_minimum_fee")
    private StatusMinimumFeeEnum statusMinimumFee;
    @Field("is_minimum_fee_chargeable")
    private Boolean isMinimumFeeChargeable;
    @Field("amount_fee_chargeable")
    private BigDecimal amountFeeChargeable;
}
