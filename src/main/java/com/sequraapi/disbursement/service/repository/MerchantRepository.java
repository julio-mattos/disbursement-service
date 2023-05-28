package com.sequraapi.disbursement.service.repository;

import com.sequraapi.disbursement.service.entity.MerchantEntity;
import com.sequraapi.disbursement.service.enums.DisbursementFrequencyEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantRepository extends MongoRepository<MerchantEntity, String> {

    List<MerchantEntity> findByDisbursementFrequency(DisbursementFrequencyEnum disbursementFrequency);

}
