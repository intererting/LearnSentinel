package com.example.learnsentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * author        yiliyang
 * date          2023-04-01
 * time          下午3:34
 * version       1.0
 * since         1.0
 * <p>
 * QPS 每秒请求数达到后限流  并发线程数  限定请求处理线程数量,多的请求排队
 * <p>
 * 直接  快速失败   超过限定直接报错
 * 关联  快速失败   当关联请求超过限定,本请求将会直接失败
 * 直接  warm up   开始只有qps/3的限定,在时长s秒后,达到qps,超过qps直接失败
 * 直接  排队等待   超过qps请求将会排队
 * <p>
 * <p>
 * <p>
 * 熔断
 * 平均时间超过Rt
 * 异常比例
 * 异常数目
 * <p>
 * 热点限流
 */
@RestController
public class NacosController {

    @GetMapping(value = "/testA")
    public String testA() {
        return "testA";
    }

    @GetMapping(value = "/testB")
    public String testB() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "testB";
    }

    /**
     * blockHandler处理限流异常
     */
    @GetMapping("/hot")
    @SentinelResource(value = "hotKey", blockHandler = "blockHandler", fallback = "fallback")
    public String testHotKey(@RequestParam("q1") String q1, @RequestParam String q2) {
        throw new RuntimeException("hello fallback");
        //        return q1 + " " + q2;
    }

    private String blockHandler(String q1, String q2, BlockException blockException) {
        return "block " + q1 + "  " + q2 + "  " + blockException.getMessage();
    }

    private String fallback(@RequestParam String q1, @RequestParam String q2, Throwable e) {
        return "fallback " + q1 + " " + q2 + e.getMessage();
    }
}
