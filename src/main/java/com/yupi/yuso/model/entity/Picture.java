package com.yupi.yuso.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 图片
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@TableName(value = "picture")
@Data
public class Picture implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * url
     */
    private String url;

    private static final long serialVersionUID = 1L;
}