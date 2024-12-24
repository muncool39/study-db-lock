package com.example.locking;

import com.example.locking.item.Item;
import com.example.locking.item.ItemRepository;
import com.example.locking.item.ItemService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ItemServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ItemServiceTest.class);

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    /**
     * 비관적 락:
     * 다른 트랜잭션이 동시에 동일한 데이터에 접근하거나 수정하지 못하도록 한다
     */
    @Test
    public void testPessimisticLocking() throws InterruptedException {
        // 초기 데이터 설정: 아이템 생성
        logger.info("초기 아이템 데이터를 설정합니다.");
        Item item = new Item();
        item.setName("Item 1");
        item.setQuantity(10);
        itemRepository.save(item);

        // 첫 번째 트랜잭션: 아이템 수량을 20으로 업데이트
        Thread thread1 = new Thread(() -> {
            logger.info("스레드 1: 아이템 수량 업데이트를 시도합니다.");
            itemService.updateItemQuantity(item.getId(), 20);
            logger.info("스레드 1: 아이템 수량 업데이트 완료.");
        });

        // 두 번째 트랜잭션: 아이템 수량을 30으로 업데이트
        Thread thread2 = new Thread(() -> {
            logger.info("스레드 2: 아이템 수량 업데이트를 시도합니다.");
            itemService.updateItemQuantity(item.getId(), 30);
            logger.info("스레드 2: 아이템 수량 업데이트 완료.");
        });

        // 두 스레드를 동시에 실행
        thread2.start();
        thread1.start();

        // 두 스레드가 종료될 때까지 대기
        thread1.join();
        thread2.join();

        /*
        최종 결과를 확인 (업데이트 후 아이템 수량)
        락이 걸린 것을 확인할 수 있다.
         */
        Item updatedItem = itemService.findItemById(item.getId());
        logger.info("최종 아이템 수량: {}", updatedItem.getQuantity());
        /*
        최종적으로 마지막 요청으로 반영됨 -> 중간의 업데이트 과정을 놓치게 된다
        해당 기록을 확인할 수 있도록 이벤트 소싱을 적용하는 방법도 있다
         */
    }
}
