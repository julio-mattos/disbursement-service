package com.sequraapi.disbursement.service.entity;

import com.sequraapi.disbursement.service.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "order")
public class OrderEntity {

    @Id
    private String id;
    private StatusEnum status;
    @Field("merchant_reference")
    private String merchantId;
    private BigDecimal amount;
    @Field("fee_amount")
    private BigDecimal feeAmount;
    @Field("created_at")
    private LocalDate createdAt;
    @Field("payment_reference")
    private String paymentReference;
}
