package com.song.docsign.util;

import com.alibaba.fastjson.JSON;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.api.TemplatesApi;
import com.docusign.esign.api.UsersApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.auth.OAuth;
import com.song.docsign.bean.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by Song on 2020/05/15.
 */
@Component
@Slf4j
public class DocSignUtil {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    private static String access_token_key = "doc_sign_access_token";
    private static String user_info_key = "doc_sign_user_info";
    private static Long tokenExpirationSeconds = 8 * 60 * 60L;
    private static String integration_key = "46480fbe-bfd0-4c04-8b4b-7902dc616430";
    private static String secret_key = "f7368c72-eff1-459e-b045-41ca76f221fe";
    private static String authorization_code_grant_url = "https://account-d.docusign.com/oauth/token";

    public static String sand_box_auth_user_info_base_url = "account-d.docusign.com";
    public static String sand_box_rest_api_base_url = "https://demo.docusign.net/restapi";
    public static String redirect_url = "https://test-api-gateway.meixincn.com/doc_sign/callback";

    public EnvelopesApi getEnvelopesApi() throws Exception {
        ApiClient apiClient = new ApiClient(sand_box_rest_api_base_url);
        apiClient.setAccessToken(this.getAccessToken(), tokenExpirationSeconds);
        return new EnvelopesApi(apiClient);
    }

    public TemplatesApi getTemplatesApi() throws Exception {
        ApiClient apiClient = new ApiClient(sand_box_rest_api_base_url);
        apiClient.setAccessToken(this.getAccessToken(), tokenExpirationSeconds);
        return new TemplatesApi(apiClient);
    }

    public UsersApi getUsersApi() throws Exception {
        ApiClient apiClient = new ApiClient(sand_box_rest_api_base_url);
        apiClient.setAccessToken(this.getAccessToken(), tokenExpirationSeconds);
        return new UsersApi(apiClient);
    }

    public String getAccountId() throws Exception {
        return this.getAccountUserInfo().getAccounts().get(0).getAccountId();
    }

    public String getBaseUri() throws Exception {
        return this.getAccountUserInfo().getAccounts().get(0).getBaseUri();
    }

    public String getAuthorization() throws Exception {
        return "Bearer " + this.getAccessToken();
    }

    private OAuth.UserInfo getAccountUserInfo() throws Exception {
        String value = stringRedisTemplate.opsForValue().get(user_info_key);
        OAuth.UserInfo userInfo = null;
        if (StringUtils.isNotBlank(value)) {
            userInfo = JSON.parseObject(value, OAuth.UserInfo.class);
        }

        if (userInfo == null) {
            ApiClient apiClient = new ApiClient();
            // TODO: 2020/5/21 注意沙箱和正式的请求路径不一样
            apiClient.setOAuthBasePath(sand_box_auth_user_info_base_url);
            userInfo = apiClient.getUserInfo(this.getAccessToken());
            if (userInfo == null) {
                throw new RuntimeException("");
            }
            stringRedisTemplate.opsForValue().set(user_info_key, JSON.toJSONString(userInfo));
        }

        return userInfo;
    }


    /**
     * 获取token
     *
     * @return
     * @throws Exception
     */
    public String getAccessToken() throws Exception {
        String value = stringRedisTemplate.opsForValue().get(access_token_key);
        AccessToken accessToken = null;
        if (StringUtils.isNotBlank(value)) {
            accessToken = JSON.parseObject(value, AccessToken.class);
            if (accessToken != null) {
                Date expireAt = accessToken.getExpireAt();
                if (expireAt == null || DateTime.now().isAfter(new DateTime(expireAt))) {
                    accessToken = this.getAccessTokenFromDocSign();
                    this.setAccessToken(accessToken);
                } else if (DateTime.now().isBefore(new DateTime(expireAt))
                        && DateTime.now().plusMinutes(10).isAfter(new DateTime(expireAt))) {
                    accessToken = this.refreshToken(accessToken);
                    this.setAccessToken(accessToken);
                }
            } else {
                accessToken = this.getAccessTokenFromDocSign();
                this.setAccessToken(accessToken);
            }
        } else {
            accessToken = this.getAccessTokenFromDocSign();
            this.setAccessToken(accessToken);
        }
        return accessToken.getAccessToken();
    }


    public void setAccessToken(AccessToken accessToken) {
        stringRedisTemplate.opsForValue().set(access_token_key, JSON.toJSONString(accessToken));
    }

    /**
     * Authorization Code Grant
     *
     * @throws Exception
     */
    public AccessToken getAccessTokenFromDocSign() throws Exception {
        // TODO: 2020/5/21 每次授权都不一样,待处理!!!!!!
        String code = "eyJ0eXAiOiJNVCIsImFsZyI6IlJTMjU2Iiwia2lkIjoiNjgxODVmZjEtNGU1MS00Y2U5LWFmMWMtNjg5ODEyMjAzMzE3In0.AQsAAAABAAYABwAAcSjON_3XSAgAAP2uFTj910gCAAxEEl4asrlLgKSznK8OGiUVAAEAAAAYAAEAAAAFAAAADQAkAAAANDY0ODBmYmUtYmZkMC00YzA0LThiNGItNzkwMmRjNjE2NDMwIgAkAAAANDY0ODBmYmUtYmZkMC00YzA0LThiNGItNzkwMmRjNjE2NDMwNwAgrAuWF0OkQ7HMrTVLXMEoMACAPflhNv3XSBIAAQAAAAMAAAB0c3Y.2b9f36fK9hm7wELiNZYWb9oZ1QHqYFqRSoJ6unAisIMiRDzmze8Fn5PVIyh74EqfbVPV9OcYhb7AkgkXlzilyoBvlkhLwOAnh0w_hXGep-AGqb5tHFISkvTNwBncAV4wjVecW60kObqjfJU1LT6wcDKWH9oAkJ2z_WY_10wkvxmm4x77UausaAnOU0wsvjIO1kwYpfz2w3fiK3JxM3djEzwZgJLgn3BDl9bcpGJBZ1I3WDElFeU_rWpuVstVi8EOFCnGEtuhxxb6S5KCHRfsDuBZKVXTaVn77suUg5M7LrXrlxa_yaSfQxOJy3ZIW1kvzQcC2QzC1tSID1zsfhAHmg";
        String redirect_uri = "www.baidu.com";

        String authorization = String.format(
                "Basic %s",
                new String(new org.apache.tomcat.util.codec.binary.Base64().encode(String.format("%s:%s", integration_key,
                        secret_key).getBytes("UTF-8")), "UTF-8"));

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", authorization);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("code", code));
        params.add(new BasicNameValuePair("redirect_uri", redirect_uri));

        HttpResp resp = HttpUtil.post(authorization_code_grant_url, params, headers);
        log.info("[]resp->{}", JSON.toJSONString(resp));

        String body = resp.getBody();
        AccessToken accessToken = JSON.parseObject(body, AccessToken.class);
        accessToken.setExpireAt(DateTime.now().plusSeconds(accessToken.getExpiresIn().intValue()).toDate());
        return accessToken;
    }

    /**
     * 刷新token
     */
    public AccessToken refreshToken(AccessToken accessToken) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + accessToken.getAccessToken());

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", "refresh_token"));
        params.add(new BasicNameValuePair("refresh_token", accessToken.getRefreshToken()));

        HttpResp httpResp = HttpUtil.post(authorization_code_grant_url, params, headers);
        log.info("resp->{}", JSON.toJSONString(httpResp));

        accessToken = JSON.parseObject(httpResp.getBody(), AccessToken.class);
        accessToken.setExpireAt(DateTime.now().plusSeconds(accessToken.getExpiresIn().intValue()).toDate());
        return accessToken;
    }
}
