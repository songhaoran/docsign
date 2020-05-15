package com.song.docsign.util;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.api.TemplatesApi;
import com.docusign.esign.client.ApiClient;

/**
 * Created by Song on 2020/05/15.
 */
public class DocSignUtil {
    public static String accountId = "10520200";
    public static String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IjY4MTg1ZmYxLTRlNTEtNGNlOS1hZjFjLTY4OTgxMjIwMzMxNyJ9.eyJUb2tlblR5cGUiOjUsIklzc3VlSW5zdGFudCI6MTU4OTUzNTk0MywiZXhwIjoxNTg5NTY0NzQzLCJVc2VySWQiOiI1ZTEyNDQwYy1iMjFhLTRiYjktODBhNC1iMzljYWYwZTFhMjUiLCJzaXRlaWQiOjEsInNjcCI6WyJzaWduYXR1cmUiLCJjbGljay5tYW5hZ2UiLCJvcmdhbml6YXRpb25fcmVhZCIsInJvb21fZm9ybXMiLCJncm91cF9yZWFkIiwicGVybWlzc2lvbl9yZWFkIiwidXNlcl9yZWFkIiwidXNlcl93cml0ZSIsImFjY291bnRfcmVhZCIsImRvbWFpbl9yZWFkIiwiaWRlbnRpdHlfcHJvdmlkZXJfcmVhZCIsImR0ci5yb29tcy5yZWFkIiwiZHRyLnJvb21zLndyaXRlIiwiZHRyLmRvY3VtZW50cy5yZWFkIiwiZHRyLmRvY3VtZW50cy53cml0ZSIsImR0ci5wcm9maWxlLnJlYWQiLCJkdHIucHJvZmlsZS53cml0ZSIsImR0ci5jb21wYW55LnJlYWQiLCJkdHIuY29tcGFueS53cml0ZSJdLCJhdWQiOiJmMGYyN2YwZS04NTdkLTRhNzEtYTRkYS0zMmNlY2FlM2E5NzgiLCJhenAiOiJmMGYyN2YwZS04NTdkLTRhNzEtYTRkYS0zMmNlY2FlM2E5NzgiLCJpc3MiOiJodHRwczovL2FjY291bnQtZC5kb2N1c2lnbi5jb20vIiwic3ViIjoiNWUxMjQ0MGMtYjIxYS00YmI5LTgwYTQtYjM5Y2FmMGUxYTI1IiwiYXV0aF90aW1lIjoxNTg5NTM0MjExLCJwd2lkIjoiOTYwYmFjMjAtNDMxNy00M2E0LWIxY2MtYWQzNTRiNWNjMTI4In0.Onqjily0L5wYpO2FYWIyPQgweopLkhYxH0qFjTdRWth0x06ZM0il-NlAeTpzq3_8xI2MQdxr9qpdr2Gi7qhqHH4ScTi8PIpDtXdB1vL9YufIRgcIm97eQMT-MD8wQ2qd9rpE-tHpMRdXfbB3hDb1tkIn9p5ZBXFM1Lg03o5brJv6q7GsaP3OrBxlOr8tA6bj3OQFvn3xRWAAft8bVEi1KKmIY6gom_18v-A09X-RPcVzYcQ_mxf9_E2IeolJ4d1DZU79a7BST-4gm9_EZKFMnsrXwsNh2SMYRuNnaghduW-rUmfBVZ8IjpNtR35g0_yBcoRS_dzEYL95djWmzm9IOw";
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
}
