package pl.cookbook.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.cookbook.database.entities.Product;

@Dao
public interface ProductsDao {
    @Query("SELECT * FROM Product")
    List<Product> getAll();

    @Query("SELECT * FROM Product WHERE idProduct=:idProduct")
    Product getByIdProduct(long idProduct);

    @Insert
    long insert(Product product);

    @Update
    int update(Product product);

    @Query("DELETE FROM Product WHERE idProduct=:idProduct")
    void deleteByIdProduct(long idProduct);
}
