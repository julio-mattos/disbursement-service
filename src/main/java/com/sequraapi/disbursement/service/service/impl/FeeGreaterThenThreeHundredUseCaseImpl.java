package com.sequraapi.disbursement.service.service.impl;

import com.sequraapi.disbursement.service.entity.OrderEntity;
import com.sequraapi.disbursement.service.enums.StatusEnum;
import com.sequraapi.disbursement.service.service.FeeUseCase;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FeeGreaterThenThreeHundredUseCaseImpl implements FeeUseCase {

    private String paymentReference;

    @Override
    public void updateOrder(OrderEntity disbursement) {

        BigDecimal feeAmount = disbursement.getAmount().multiply(BigDecimal.valueOf(0.0085));
        disbursement.setFeeAmount(feeAmount.setScale(2, RoundingMode.HALF_EVEN));
        disbursement.setAmount(disbursement.getAmount().subtract(disbursement.getFeeAmount())
                .setScale(2, RoundingMode.HALF_EVEN));
        disbursement.setPaymentReference(paymentReference);
        disbursement.setStatus(StatusEnum.PROCESSED);
    }

    @Override
    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }
}
