package cn.itcast.order.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class Product implements Serializable{
    private Long id;
    private String productName;
    private Integer status;
    private BigDecimal price;
    private String product_desc;
    private String caption;
    private Integer inventory;
}
