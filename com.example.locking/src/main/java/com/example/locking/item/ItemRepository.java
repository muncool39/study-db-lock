package com.example.locking.item;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    /*
    비관적 락 적용
    PESSIMISTIC_WRITE: 쓰기 락을 설정 - 다른 트랜잭션이 해당 데이터를 읽거나 쓰지 못함
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.id = :id")
    Item findByIdWithLock(Long id);
}
