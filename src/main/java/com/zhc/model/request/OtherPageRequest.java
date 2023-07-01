package com.zhc.model.request;

import lombok.Data;

@Data
public class OtherPageRequest {

    private Integer pageNu = 1;

    private Integer index;

    private Integer pageSiz = 10;
}
