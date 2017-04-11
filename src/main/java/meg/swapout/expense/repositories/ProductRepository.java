package meg.swapout.expense.repositories;

import meg.swapout.expense.domain.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Integer> {
}