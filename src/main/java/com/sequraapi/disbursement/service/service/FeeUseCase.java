package com.sequraapi.disbursement.service.service;

import com.sequraapi.disbursement.service.entity.OrderEntity;

public interface FeeUseCase {

    void updateOrder(OrderEntity disbursement);

    void setPaymentReference(String paymentReference);
}
