package com.langyastudio.springboot.controller;

import com.langyastudio.springboot.common.config.DiskLocalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * properties
 */
@RestController
@RequestMapping("/config")
public class ConfigController
{
    @Value("${langyastudio.disk.local.root}")
    String root;

    @Autowired
    DiskLocalConfig diskLocalConfig;

    @GetMapping("root")
    public String getRoot()
    {
        return root;
    }

    @GetMapping("config")
    public DiskLocalConfig getConfig()
    {
        return diskLocalConfig;
    }
}
