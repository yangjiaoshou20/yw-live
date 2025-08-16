package com.yw.live.user.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserTagDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3848398522248671686L;

    private Long userId;
    private Long tagInfo01;
    private Long tagInfo02;
    private Long tagInfo03;
}
