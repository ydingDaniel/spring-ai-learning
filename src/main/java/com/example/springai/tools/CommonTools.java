package com.example.springai.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CommonTools {

    @Tool(description = "获取当前日期和时间")
    public String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Tool(description = "计算两个整数的加法，返回结果")
    public int add(int a, int b) {
        return a + b;
    }

    @Tool(description = "计算两个整数的乘法，返回结果")
    public int multiply(int a, int b) {
        return a * b;
    }

    @Tool(description = "将人民币（CNY）金额换算成美元（USD），汇率固定为 7.2，返回换算结果")
    public String convertToUSD(double cny) {
        double usd = cny / 7.2;
        return String.format("%.2f 元人民币 = %.2f 美元（汇率 7.2）", cny, usd);
    }

    /**
     * 模拟天气查询，实际项目中这里调用真实天气 API
     */
    @Tool(description = "查询指定城市的天气，返回天气描述")
    public String getWeather(String city) {
        return switch (city) {
            case "北京" -> "北京：晴，25°C，东南风3级";
            case "上海" -> "上海：多云，28°C，东风2级";
            case "杭州" -> "杭州：小雨，22°C，北风4级";
            default -> city + "：天气数据暂不可用";
        };
    }
}
