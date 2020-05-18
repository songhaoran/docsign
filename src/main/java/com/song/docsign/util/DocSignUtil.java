package com.song.docsign.util;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.api.TemplatesApi;
import com.docusign.esign.client.ApiClient;

/**
 * Created by Song on 2020/05/15.
 */
public class DocSignUtil {
    public static String accountId = "10520200";
    public static String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IjY4MTg1ZmYxLTRlNTEtNGNlOS1hZjFjLTY4OTgxMjIwMzMxNyJ9.eyJUb2tlblR5cGUiOjUsIklzc3VlSW5zdGFudCI6MTU4OTc5MDc2OSwiZXhwIjoxNTg5ODE5NTY5LCJVc2VySWQiOiI1ZTEyNDQwYy1iMjFhLTRiYjktODBhNC1iMzljYWYwZTFhMjUiLCJzaXRlaWQiOjEsInNjcCI6WyJzaWduYXR1cmUiLCJjbGljay5tYW5hZ2UiLCJvcmdhbml6YXRpb25fcmVhZCIsInJvb21fZm9ybXMiLCJncm91cF9yZWFkIiwicGVybWlzc2lvbl9yZWFkIiwidXNlcl9yZWFkIiwidXNlcl93cml0ZSIsImFjY291bnRfcmVhZCIsImRvbWFpbl9yZWFkIiwiaWRlbnRpdHlfcHJvdmlkZXJfcmVhZCIsImR0ci5yb29tcy5yZWFkIiwiZHRyLnJvb21zLndyaXRlIiwiZHRyLmRvY3VtZW50cy5yZWFkIiwiZHRyLmRvY3VtZW50cy53cml0ZSIsImR0ci5wcm9maWxlLnJlYWQiLCJkdHIucHJvZmlsZS53cml0ZSIsImR0ci5jb21wYW55LnJlYWQiLCJkdHIuY29tcGFueS53cml0ZSJdLCJhdWQiOiJmMGYyN2YwZS04NTdkLTRhNzEtYTRkYS0zMmNlY2FlM2E5NzgiLCJhenAiOiJmMGYyN2YwZS04NTdkLTRhNzEtYTRkYS0zMmNlY2FlM2E5NzgiLCJpc3MiOiJodHRwczovL2FjY291bnQtZC5kb2N1c2lnbi5jb20vIiwic3ViIjoiNWUxMjQ0MGMtYjIxYS00YmI5LTgwYTQtYjM5Y2FmMGUxYTI1IiwiYXV0aF90aW1lIjoxNTg5Nzg3ODEwLCJwd2lkIjoiOTYwYmFjMjAtNDMxNy00M2E0LWIxY2MtYWQzNTRiNWNjMTI4In0.VZE6Re9keWmKXUIR5MU5UdzzI0RiroRthMPDH5TT0uA89s_0RBkEcFH1uz7Z_ZDXcOSRXyoCJf0GftjYxM-w2L8AaVDJ62ADdwbKdnAMjawk-EA_hqDFbiZjsSy96gG8CLbSEniD4omsPDbcjxXe1FXl6j6Loo3A6gZRFfxL5nlQN6fKec5o4BqtGl9MDSQKj1Gf53jd6I_lhCOhyQYsEmaeXwACSdokWhTqh9dcR-_y0dJHpF6fEVVkQXrJo5o2VwxXcxi29FZvuXZ06pgDweNTd5QMdekcnbLER_L63XFe0j3s_nwhNQAOxp_RkUXvw5t-jKW2Ys1WNAdOClqnaQ";
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
