package com.sequraapi.disbursement.service.repository;

import com.sequraapi.disbursement.service.entity.DisbursementEntity;
import com.sequraapi.disbursement.service.enums.StatusMinimumFeeEnum;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DisbursementRepository extends MongoRepository<DisbursementEntity, String> {

    Optional<DisbursementEntity> findByYearAndMonthAndMerchantId(int year, int month, String merchantId);

    List<DisbursementEntity> findByStatusMinimumFee(StatusMinimumFeeEnum statusMinimumFee);
}
