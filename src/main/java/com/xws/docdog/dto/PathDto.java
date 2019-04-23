package com.xws.docdog.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xuwangshen
 * @date 2018年10月29日
 * @company ZiYun
 **/
@Getter
public class PathDto {

    private OperationDto get;
    private OperationDto put;
    private OperationDto post;
    private OperationDto head;
    private OperationDto delete;

    private List<OperationDto> operations = new ArrayList<>();

    public void setGet(OperationDto get) {
        this.get = get;
        if (this.get != null)
            operations.add(get);
    }

    public void setPut(OperationDto put) {
        this.put = put;
        if (this.put != null)
            operations.add(put);
    }

    public void setPost(OperationDto post) {
        this.post = post;
        if (this.post != null)
            operations.add(post);
    }

    public void setHead(OperationDto head) {
        this.head = head;
        if (this.head != null)
            operations.add(head);
    }

    public void setDelete(OperationDto delete) {
        this.delete = delete;
        if (this.delete != null)
            operations.add(delete);
    }

    public void setOperations(List<OperationDto> operations) {
        this.operations = operations;
    }
}
