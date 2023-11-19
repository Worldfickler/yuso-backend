package com.yupi.yuso.datasource;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yuso.common.ErrorCode;
import com.yupi.yuso.exception.BusinessException;
import com.yupi.yuso.model.entity.Picture;
import com.yupi.yuso.service.PictureService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 图片服务实现类
 * @author Fickler
 * @date 2023/11/17 23:13
 */

@Service
public class PictureDataSource implements DataSource<Picture> {

    @Resource
    private PictureService pictureService;

    @Override
    public Page<Picture> doSearch(String searchText, long pageNum, long pageSize) {
        Page<Picture> picturePage = pictureService.searchPicture(searchText, pageNum, pageSize);
        return picturePage;
    }
}
