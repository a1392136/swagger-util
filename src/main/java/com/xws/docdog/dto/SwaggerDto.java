package com.xws.docdog.dto;

import io.swagger.models.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author xuwangshen
 * @date 2018年10月29日
 * @company ZiYun
 **/
@NoArgsConstructor
@Getter
@Setter
public class SwaggerDto {

    protected List<Tag> tags;
    protected Map<String, PathDto> paths;

}
