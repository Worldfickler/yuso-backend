package com.yupi.yuso.model.dto.picture;

import com.yupi.yuso.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author Fickler
 * @date 2023/11/17 23:03
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class PictureQueryRequest  extends PageRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;

    private static final long serialVersionUID = 1L;

}
