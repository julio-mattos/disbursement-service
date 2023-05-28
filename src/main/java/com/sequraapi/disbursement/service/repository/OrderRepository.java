package com.sequraapi.disbursement.service.repository;

import com.sequraapi.disbursement.service.entity.OrderEntity;
import com.sequraapi.disbursement.service.enums.StatusEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<OrderEntity, String> {

    List<OrderEntity> findByMerchantIdAndStatus(String merchantId, StatusEnum status);

}
