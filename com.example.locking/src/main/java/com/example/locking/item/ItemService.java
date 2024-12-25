package com.example.locking.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     *
     * @param itemId 아이템 id
     * @param newQuantity 업데이트할 수량
     */
    @Transactional
    public void updateItemQuantity(Long itemId, Integer newQuantity) {
        // 1. 작성한 비관적 락을 사용하여 데이터를 조회
        Item item = itemRepository.findByIdWithLock(itemId);

        // 2. 재고 수량을 수정
        item.setQuantity(newQuantity);

        // 3. 수정된 데이터를 저장
        itemRepository.save(item);
    }


    /**
     *
     * @param itemId 아이템 id
     * @return 비관적 락 적용 없이 데이터를 조회
     */
    @Transactional
    public Item findItemById(Long itemId) {
        return itemRepository.findById(itemId).orElse(null);
    }


    /**
     * 데드락이 발생하는지 확인하기위한 메서드
     * @param itemId1 접근할 아이템 1
     * @param itemId2 접근할 아이템 2
     * SERIALIZABLE 격리 수준 :
     *                트랜잭션 간의 완벽한 격리를 보장하기 위해 트랜잭션들이 마치 순차적으로 처리되는 것처럼 보이도록 만든다
     *                이 수준에서 한 트랜잭션이 데이터를 읽거나 수정하고 있는 동안 다른 트랜잭션은 해당 데이터에 접근할 수 없다
     * timeout = 1 :
     *                트랜잭션 시작 후 1초 안에 완료되지 않으면 타임아웃 발생시킴
     *                데드 락 상황에서 무한히 대기하지 않고 빠져나올 수 있도록 한다
     */
    @Transactional(timeout = 1, isolation = Isolation.SERIALIZABLE)
    public void updateItemsQuantity(Long itemId1, Long itemId2) {
        // 첫 번째 아이템에 락을 건 후 업데이트
        Item item1 = itemRepository.findByIdWithLock(itemId1);
        item1.setQuantity(item1.getQuantity() + 10);
        itemRepository.save(item1);

        // 데드 락을 발생시키기 위해 잠시 대기
        try { Thread.sleep(4000); } catch (InterruptedException e) { e.printStackTrace(); }

        // 두 번째 아이템에 락을 건 후 업데이트
        Item item2 = itemRepository.findByIdWithLock(itemId2);
        item2.setQuantity(item2.getQuantity() + 10);
        itemRepository.save(item2);
    }
}
