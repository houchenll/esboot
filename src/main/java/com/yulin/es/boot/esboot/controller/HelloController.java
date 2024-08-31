package com.yulin.es.boot.esboot.controller;

import com.yulin.es.boot.esboot.es.Poem;
import com.yulin.es.boot.esboot.service.PoemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("hello")
public class HelloController {

    @GetMapping("time")
    public String getTime() {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式化当前时间
        String formattedNow = now.format(formatter);
        return "当前时间: " + formattedNow;
    }

    @Autowired
    private PoemService poemService;

    @GetMapping("/search/all")
    public Iterable<Poem> searchAll() {
        return poemService.searchAll();
    }

    @GetMapping("/search")
    public List<Poem> search(@RequestParam(name = "query") String query) {
        System.out.println("query " + query);
        return poemService.searchPosts(query);
    }

}
