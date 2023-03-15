package com.store.becoffeestore.entity;

import com.store.becoffeestore.dto.result.ProductDto;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;

@SqlResultSetMapping(
    name = "maxResultMapping",
    classes = {
        @ConstructorResult(
            targetClass = ProductDto.class,
            columns = {
                @ColumnResult(name = "id", type = Long.class),
                @ColumnResult(name = "amount", type = Integer.class)
            }
        )
    }
)
@NamedNativeQuery(name = "maxCoffeeQuery", query = "select s.product_id AS id, sum(s.amount) AS amount from sales s, product p where s.product_id = p.id and p.dtype ='Coffee' group by s.product_id order by sum(s.amount) desc limit 1", resultSetMapping = "maxResultMapping")
@NamedNativeQuery(name = "maxToppingQuery", query = "select s.product_id AS id, sum(s.amount) AS amount from sales s, product p where s.product_id = p.id and p.dtype ='Topping' group by s.product_id order by sum(s.amount) desc limit 1", resultSetMapping = "maxResultMapping")

@Entity
@Table(name = "sales")
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Product product;

    private Integer amount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
