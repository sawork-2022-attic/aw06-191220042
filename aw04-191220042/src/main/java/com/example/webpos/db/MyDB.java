package com.example.webpos.db;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.batch.mapper.ProductMapper;
import com.example.webpos.model.NewProduct;
import com.example.webpos.model.NewProductMapper;
import com.example.webpos.model.Product;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class MyDB implements PosDB{

    @Resource
    private NewProductMapper newProductMapper;

    private List<Product> products = null;

    @Override
    public List<Product> getProducts() {
        if (products == null) {
            List<NewProduct> newProducts = newProductMapper.selectList(new QueryWrapper<>());


            products = newProducts.stream()
                    .filter(newProduct -> !newProduct.getPrice().isBlank() && !(newProduct.getImageURLHighRes().isEmpty()))
                    .filter(newProduct -> newProduct.getPrice().length() > 1 && newProduct.getPrice().charAt(0) == '$')
                    .map(newProduct -> new Product(newProduct.getId().toString(), newProduct.getTitle(), Double.parseDouble(newProduct.getPrice().substring(1)), newProduct.getImageURLHighRes().get(0)))
                    .collect(Collectors.toList());
        }


        return products;
    }

    @Override
    public Product getProduct(String productId) {
        if (products == null) {
            getProducts();
        }
        return products.stream().filter(product -> Objects.equals(product.getId(), productId)).findFirst().orElse(null);
    }
}
