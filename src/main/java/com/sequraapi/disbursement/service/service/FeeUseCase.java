package com.sequraapi.disbursement.service.service;

import com.sequraapi.disbursement.service.entity.OrderEntity;

public interface FeeUseCase {

    void updateDisbursement(OrderEntity disbursement);

    void paymentReference(String paymentReference);
}
