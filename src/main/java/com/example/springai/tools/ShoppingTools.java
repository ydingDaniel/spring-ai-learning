package com.example.springai.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ShoppingTools {

    private static final Map<String, Integer> PRICES = Map.of(
            "iPhone 15", 5999,
            "MacBook Pro", 14999,
            "AirPods Pro", 1799
    );

    private static final Map<String, Integer> STOCK = Map.of(
            "iPhone 15", 0,
            "MacBook Pro", 5,
            "AirPods Pro", 23
    );

    private static final Map<String, Double> DISCOUNTS = Map.of(
            "iPhone 15", 0.95,
            "MacBook Pro", 0.88,
            "AirPods Pro", 0.90
    );

    @Tool(description = "查询商品的原价（单位：元），参数 productName 为商品名称")
    public String getPrice(String productName) {
        Integer price = PRICES.get(productName);
        if (price == null) return "商品不存在：" + productName;
        return productName + " 的原价为 " + price + " 元";
    }

    @Tool(description = "查询商品的库存数量，参数 productName 为商品名称")
    public String getStock(String productName) {
        Integer stock = STOCK.get(productName);
        if (stock == null) return "商品不存在：" + productName;
        return stock == 0
                ? productName + " 库存不足，暂时缺货"
                : productName + " 库存充足，剩余 " + stock + " 件";
    }

    @Tool(description = "查询商品的折扣率，并计算折后价。参数 productName 为商品名称，originalPrice 为原价")
    public String calculateDiscountPrice(String productName, int originalPrice) {
        Double discount = DISCOUNTS.get(productName);
        if (discount == null) return "该商品暂无折扣活动";
        int finalPrice = (int) (originalPrice * discount);
        return String.format("%s 当前折扣率 %.0f%%，折后价为 %d 元（优惠 %d 元）",
                productName, discount * 100, finalPrice, originalPrice - finalPrice);
    }
}
