package com.ifi.consumer.consumer;

import com.ifi.consumer.domain.Coupon;
import com.ifi.consumer.domain.FailedEvent;
import com.ifi.consumer.repository.CouponRepository;
import com.ifi.consumer.repository.FailedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CouponCreateConsumer {

    private final Logger log = LoggerFactory.getLogger(CouponCreateConsumer.class);

    private final CouponRepository couponRepository;
    private final FailedEventRepository failedEventRepository;

    public CouponCreateConsumer(CouponRepository couponRepository, FailedEventRepository failedEventRepository) {
        this.couponRepository = couponRepository;
        this.failedEventRepository = failedEventRepository;
    }

    @KafkaListener(topics = "coupon_create", groupId = "group_1")
    public void listener(Long userId) {
        try {
            couponRepository.save(new Coupon(userId));
        } catch (Exception e) {
            log.error("failed to create coupon: " + userId);
            // 추후 배치로 FailedEvent 사용자들에게 쿠폰 발급
            failedEventRepository.save(new FailedEvent(userId));
        }
    }

}
