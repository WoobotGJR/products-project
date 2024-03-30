package com.woobot.restproduct.repository;

import com.woobot.restproduct.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;

@Controller
public interface ProductRepository extends CrudRepository<Product, Integer> {
    // filtration by title with repository method name
    //Iterable<Product> findAllByTitleLikeIgnoreCase(String filter); // select all from catalogue.t_product where c_title ilike: filter
    /*
    Hibernate: select p1_0.id,p1_0.c_details,p1_0.c_title from catalogue.t_product p1_0 // simple findAll
    Hibernate: select p1_0.id,p1_0.c_details,p1_0.c_title from catalogue.t_product p1_0 where upper(p1_0.c_title) like upper(?) escape '\' // this method query
    */

    // filtration by JPQL query
    // Here we are working with class Product and his props!!! (table=Product, c_title=title)
//    @Query(value = "select p from Product p where p.title ilike :filter")
//    Iterable<Product> findAllByTitleLikeIgnoreCase(@Param("filter") String filter);
//    Hibernate: select p1_0.id,p1_0.c_details,p1_0.c_title from catalogue.t_product p1_0 where p1_0.c_title ilike ? escape ''

    // With SQL query
//    @Query(value = "select * from catalogue.t_product where c_title ilike :filter", nativeQuery = true)
//    Iterable<Product> findAllByTitleLikeIgnoreCase(@Param("filter") String filter);
//    Hibernate: select * from catalogue.t_product where c_title ilike ?

    // With named queries (check Product Entity annotations)
    @Query(name = "Product.findAllByTitleLikeIgnoringCase")
    Iterable<Product> findAllByTitleLikeIgnoreCase(@Param("filter") String filter);
}
