package com.xws.docdog.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author xuwangshen
 * @date 2018年10月29日
 * @company ZiYun
 **/
@Getter
@Setter
public class OperationDto {

    private List<String> tags;

    private String summary;

}
