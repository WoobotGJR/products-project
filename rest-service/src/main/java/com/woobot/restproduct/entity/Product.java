package com.woobot.restproduct.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity // with this we show, that object is persistence
@Table(schema = "catalogue", name = "t_product")
@NamedQueries(
        @NamedQuery(name = "Product.findAllByTitleLikeIgnoringCase",
                query = "select p from Product p where p.title ilike :filter") // name of query depends on developer
)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "c_title")
    @NotNull
    @Size(min = 3, max = 50)
    private String title;

    @Column(name = "c_details")
    @Size(max = 1000)
    private String details;

//    private String purchasesCount -> purchases_count in db
}
