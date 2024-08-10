package concurrency.lock.repository;

import concurrency.lock.entity.ProductsPessimistic;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ProductsPessimisticRepository  extends JpaRepository<ProductsPessimistic, Long> {

    // SELECT * FROM products_pessimistic p WHERE p.id = 1 FOR UPDATE;
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductsPessimistic p WHERE p.id = :id")
    Optional<ProductsPessimistic> findByIdWithLock(@Param("id") Long id);


    @Modifying
    @Transactional
    @Query("UPDATE ProductsPessimistic p SET p.stock = :stock WHERE p.id = :id")
    int updateStockById(@Param("id") Long id, @Param("stock") Long stock);
}
