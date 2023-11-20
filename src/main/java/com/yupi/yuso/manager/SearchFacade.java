package com.yupi.yuso.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yuso.common.BaseResponse;
import com.yupi.yuso.common.ErrorCode;
import com.yupi.yuso.common.ResultUtils;
import com.yupi.yuso.datasource.DataSource;
import com.yupi.yuso.datasource.PictureDataSource;
import com.yupi.yuso.datasource.PostDataSource;
import com.yupi.yuso.datasource.UserDataSource;
import com.yupi.yuso.exception.BusinessException;
import com.yupi.yuso.exception.ThrowUtils;
import com.yupi.yuso.model.dto.post.PostQueryRequest;
import com.yupi.yuso.model.dto.search.SearchRequest;
import com.yupi.yuso.model.dto.user.UserQueryRequest;
import com.yupi.yuso.model.entity.Picture;
import com.yupi.yuso.model.enums.SearchTypeEnum;
import com.yupi.yuso.model.vo.PostVO;
import com.yupi.yuso.model.vo.SearchVO;
import com.yupi.yuso.model.vo.UserVO;
import com.yupi.yuso.service.PictureService;
import com.yupi.yuso.service.PostService;
import com.yupi.yuso.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 查询门面
 * @author Fickler
 * @date 2023/11/18 20:02
 */

@Component
@Slf4j
public class SearchFacade {

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private UserDataSource userDataSource;

    public SearchVO searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {

        String type = searchRequest.getType();
        String searchText = searchRequest.getSearchText();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);
        long current = searchRequest.getCurrent();
        long pageSize = searchRequest.getPageSize();

        if (ObjectUtils.isEmpty(searchTypeEnum)) {
            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                UserQueryRequest userQueryRequest = new UserQueryRequest();
                userQueryRequest.setUserName(searchText);
                Page<UserVO> userVOPage = userDataSource.doSearch(searchText, current, pageSize);
                return userVOPage;
            });

            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                PostQueryRequest postQueryRequest = new PostQueryRequest();
                postQueryRequest.setSearchText(searchText);
                Page<PostVO> postVOPage = postDataSource.doSearch(searchText, current, pageSize);
                return postVOPage;
            });

            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
                Page<Picture> picturePage = pictureDataSource.doSearch(searchText, current, pageSize);
                return picturePage;
            });

            CompletableFuture.allOf(userTask, postTask, pictureTask).join();

            try {
                Page<UserVO> userVOPage = userTask.get();
                Page<PostVO> postVOPage = postTask.get();
                Page<Picture> picturePage = pictureTask.get();

                SearchVO searchVO = new SearchVO();
                searchVO.setPictureList(picturePage.getRecords());
                searchVO.setPostList(postVOPage.getRecords());
                searchVO.setUserList(userVOPage.getRecords());
                return searchVO;
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {
            Map<String, DataSource<T>> typeDataSourceMap = new HashMap() {{
               put(SearchTypeEnum.POST.getValue(), postDataSource);
               put(SearchTypeEnum.PICTURE.getValue(), pictureDataSource);
               put(SearchTypeEnum.USER.getValue(), userDataSource);
            }};
            SearchVO searchVO = new SearchVO();
            DataSource<?> searchDataSource = typeDataSourceMap.get(type);
            Page<?> page = searchDataSource.doSearch(searchText, current, pageSize);
            searchVO.setDataList(page);
            return searchVO;
        }
    }

}
