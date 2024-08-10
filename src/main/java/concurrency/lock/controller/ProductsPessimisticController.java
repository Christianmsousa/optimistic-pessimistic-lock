package concurrency.lock.controller;


import concurrency.lock.repository.ProductsPessimisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/pessimistic")
public class ProductsPessimisticController {

    private final ProductsPessimisticRepository repository;

    @Transactional
    @PostMapping("/{id}/{value}")
    public ResponseEntity optimistic(@PathVariable Long value, @PathVariable Long id) {
        var product = repository.findByIdWithLock(id);
        if(product.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Product not found");
        }
        var productPessimistic = product.get();
        if(productPessimistic.getStock() <= 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Product indisponivel");
        }
        var newValue = productPessimistic.getStock() - value;
        var updatedCount = repository.updateStockById(id, newValue);
        if (updatedCount == 0) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao comprar (Concorrência)");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Compra com sucesso");
    }
}
