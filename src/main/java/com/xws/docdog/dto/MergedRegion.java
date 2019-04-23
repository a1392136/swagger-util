package com.xws.docdog.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author xuwangshen
 * @date 2018年10月29日
 * @company ZiYun
 **/
@Getter
@Setter
public class MergedRegion {

    private int startRowIndex = 0; //合并开始行坐标
    private int endRowIndex = 0;   //合并结束行坐标
    private int startCellIndex = 0; //合并开始列坐标
    private int endCellIndex = 0;   //合并结束行坐标
}
