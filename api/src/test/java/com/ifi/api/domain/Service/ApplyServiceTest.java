package com.ifi.api.domain.Service;

import com.ifi.api.Service.ApplyService;
import com.ifi.api.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApplyServiceTest {


    @Autowired
    private ApplyService applyService;

    @Autowired
    private CouponRepository couponRepository;

    @DisplayName("한번만 응모")
    @Test
    public void tryOnce() {
        // given
        applyService.apply(1L);

        // when
        long count = couponRepository.count();

        // then
        assertThat(count).isEqualTo(1);
    }

    @DisplayName("여러명 응모")
    @Test
    public void tryMultiple() throws InterruptedException {
        // given
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // 다른 쓰레드 작업을 기다리도록 도와주는 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            long userId = i;
            executorService.submit(() -> {
                try {
                    applyService.apply(userId);
                }
                finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Thread.sleep(5000);

        long count = couponRepository.count();

        // then
        // 100개가 아닌 원인 : lace condition 발생
        assertThat(count).isEqualTo(100);
    }

    @DisplayName("한명당_한개의_쿠폰만_발급")
    @Test
    public void onlyOneCouponPerPerson() throws InterruptedException {
        // given
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // 다른 쓰레드 작업을 기다리도록 도와주는 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            long userId = i;
            executorService.submit(() -> {
                try {
                    applyService.apply(1L);
                }
                finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Thread.sleep(5000);

        long count = couponRepository.count();

        // then
        assertThat(count).isEqualTo(1);
    }

}