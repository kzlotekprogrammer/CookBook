package pl.cookbook.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import pl.cookbook.database.entities.Product;

@Dao
public interface ProductsDao {
    @Query("SELECT * FROM Product")
    List<Product> getAll();

    @Insert
    void insertAll(Product... products);

    @Delete
    void delete(Product product);

    @Query("DELETE FROM Product")
    void deleteAll();
}
