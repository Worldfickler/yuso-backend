package com.yupi.yuso.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yuso.model.dto.post.PostQueryRequest;
import com.yupi.yuso.model.entity.Picture;
import com.yupi.yuso.model.entity.Post;
import com.yupi.yuso.model.vo.PostVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 图片服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public interface PictureService {

    Page<Picture> searchPicture(String searchText, long pageNum, long pageSize);

}
