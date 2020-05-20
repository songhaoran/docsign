package com.song.docsign.util;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.api.TemplatesApi;
import com.docusign.esign.api.UsersApi;
import com.docusign.esign.client.ApiClient;

/**
 * Created by Song on 2020/05/15.
 */
public class DocSignUtil {
    public static String accountId = "10520200";
    public static String accessToken = "eyJ0eXAiOiJNVCIsImFsZyI6IlJTMjU2Iiwia2lkIjoiNjgxODVmZjEtNGU1MS00Y2U5LWFmMWMtNjg5ODEyMjAzMzE3In0.AQsAAAABAAUABwAAWXerYPzXSAgAAJmauaP810gCAAxEEl4asrlLgKSznK8OGiUVAAEAAAAYAAEAAAAFAAAADQAkAAAAN2NmZTM1M2QtYzkxYy00MzJlLTlhOTAtNmVmMDAxZjdiYTdhIgAkAAAAN2NmZTM1M2QtYzkxYy00MzJlLTlhOTAtNmVmMDAxZjdiYTdhEgABAAAACwAAAGludGVyYWN0aXZlMACAwt6qYPzXSDcAIKwLlhdDpEOxzK01S1zBKA.gS2C0zzeqfG3qUIEUmIZQyRyBm4pmudqY8K9Jk5ReOEoURkI3cKaWMig5ReZLwAxUGXb7uHdCRJn4x_atoggf8LrPpiRahtrh2Ezk2gEChADnsVaDhS_8wguXx6e9pxe_M3BPXFq6C95EYjm2Qko9bVeF67tdzHMd7sPjUipOvezrxUbr4rwWy5ADY6OEAGCS2PLUKWVeXITSsif3lBjH7ooVS29EGT9TayYv9eMp5TSecbLbDeJaUEq-uctRzTot8ErXRw8v8rG9XMOSpB-wYXb_Kiie2vH5zER1jbR47Q29awPhmOOO6CJkV5mKFGf7KFbXrh6tdUJq9HMdWPYnw";
    public static String basePath = "https://demo.docusign.net/restapi";
    private static Long tokenExpirationSeconds = 8 * 60 * 60L;

    public static EnvelopesApi getEnvelopesApi() {
        ApiClient apiClient = new ApiClient(basePath);
        apiClient.setAccessToken(accessToken, tokenExpirationSeconds);
        return new EnvelopesApi(apiClient);
    }

    public static TemplatesApi getTemplatesApi() {
        ApiClient apiClient = new ApiClient(basePath);
        apiClient.setAccessToken(accessToken, tokenExpirationSeconds);
        return new TemplatesApi(apiClient);
    }

    public static UsersApi getUsersApi() {
        ApiClient apiClient = new ApiClient(basePath);
        apiClient.setAccessToken(accessToken, tokenExpirationSeconds);
        return new UsersApi(apiClient);
    }
}
