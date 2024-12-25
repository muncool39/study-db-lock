package com.example.locking.product;

import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 낙관적 락 실습
     * @param productId 상품 id
     * @param newPrice 업데이트할 가격
     */
    @Transactional
    public void updateProductPrice(Long productId, Double newPrice) {
        try {
            // 기존 데이터를 읽어온다
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // 가격을 수정한다
            product.setPrice(newPrice);

            // 저장 시 버전이 충돌하면 예외가 발생한다.
            productRepository.save(product);


        } catch (ObjectOptimisticLockingFailureException e) {
            // 낙관적 락 예외 처리
            System.err.println("낙관적 락 충돌이 발생했습니다. 다른 트랜잭션이 먼저 데이터를 수정했습니다.");
            throw e;
        }
    }
}

