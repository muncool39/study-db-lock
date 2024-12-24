package com.example.locking.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
}
