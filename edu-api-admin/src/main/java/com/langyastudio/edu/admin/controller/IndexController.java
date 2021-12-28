package com.langyastudio.edu.admin.controller;

import com.langyastudio.edu.common.data.ResultInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 根访问
 */
@RestController
@RequestMapping("/")
public class IndexController
{
    /**
     * 当前版本
     */
    @Value("${spring.application.version}")
    private String serviceVersion;

    /**
     * 打包时间
     */
    @Value("${spring.application.package-time}")
    private String serviceBuildDate;

    /**
     * 主页
     */
    @RequestMapping("/")
    public ResultInfo index()
    {
        return ResultInfo.data(Map.of("name", "admin api",
                                      "version", serviceVersion,
                                      "time", serviceBuildDate,
                                      "company", "郎涯工作室",
                                      "official", "https://langyastudio.blog.csdn.net/",
                                      "maintainer", "langyastudio  「15589933912」"));
    }
}
