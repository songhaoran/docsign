package com.song.docsign.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * Created by Song on 2020/05/21.
 */
@Data
public class AccessToken {

    @JSONField(name = "access_token")
    private String accessToken;

    @JSONField(name = "token_type")
    private String tokenType;

    @JSONField(name = "token_type")
    private String refreshToken;

    @JSONField(name = "expires_in")
    private Long expiresIn;

    private Date expireAt;
}
