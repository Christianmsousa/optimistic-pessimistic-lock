package concurrency.lock.repository;

import concurrency.lock.entity.ProductsOptimistic;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsOptimisticRepository extends JpaRepository<ProductsOptimistic, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE ProductsOptimistic pdo SET pdo.stock = :stock, pdo.version = pdo.version + 1" +
            "WHERE pdo.id = :id AND pdo.version = :version")
    int updateStockByIdAndVersion(@Param("id") Long id, @Param("stock") Long stock, @Param("version") Long version);
}
