package com.yupi.yuso.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yuso.common.BaseResponse;
import com.yupi.yuso.common.ErrorCode;
import com.yupi.yuso.common.ResultUtils;
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
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    private PictureService pictureService;

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

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
                Page<UserVO> userVOPage = userService.listPostVOByPage(userQueryRequest);
                return userVOPage;
            });

            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                PostQueryRequest postQueryRequest = new PostQueryRequest();
                postQueryRequest.setSearchText(searchText);
                Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
                return postVOPage;
            });

            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
                Page<Picture> picturePage = pictureService.searchPicture(searchText, current, pageSize);
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
            SearchVO searchVO = new SearchVO();
            switch (searchTypeEnum) {
                case POST:
                    PostQueryRequest postQueryRequest = new PostQueryRequest();
                    postQueryRequest.setSearchText(searchText);
                    Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
                    searchVO.setPostList(postVOPage.getRecords());
                    break;
                case USER:
                    UserQueryRequest userQueryRequest = new UserQueryRequest();
                    userQueryRequest.setUserName(searchText);
                    Page<UserVO> userVOPage = userService.listPostVOByPage(userQueryRequest);
                    searchVO.setUserList(userVOPage.getRecords());
                    break;
                case PICTURE:
                    Page<Picture> picturePage = pictureService.searchPicture(searchText, current, pageSize);
                    searchVO.setPictureList(picturePage.getRecords());
                    break;
                default:
            }
            return searchVO;
        }
    }

}
