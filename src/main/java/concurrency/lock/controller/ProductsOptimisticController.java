package concurrency.lock.controller;


import concurrency.lock.repository.ProductsOptimisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/optimistic")
public class ProductsOptimisticController {

    private final ProductsOptimisticRepository repository;

    @PostMapping("/{id}/{value}")
    public ResponseEntity optimistic(@PathVariable Long value, @PathVariable Long id) {
        var product = repository.findById(id);
        if(product.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Product not found");
        }
        var productOptimistic = product.get();
        if(productOptimistic.getStock() <= 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Product indisponivel");
        }
        var newValue = productOptimistic.getStock() - value;
        var updatedCount = repository.updateStockByIdAndVersion(id, newValue, productOptimistic.getVersion());
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
