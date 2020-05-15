package com.song.docsign.service;

import com.alibaba.fastjson.JSON;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.api.TemplatesApi;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.*;
import com.song.docsign.util.DocSignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.*;

/**
 * Created by Song on 2020/05/15.
 */
@Component
@Slf4j
public class EnvelopeService {

    private static final int TIMEOUT = 5000;

    /**
     * 生成签署文件链接
     *
     * @return
     * @throws Exception
     */
    public String getDocSignUrl(String templateId) {
        String signerName = "wen";
        String signerEmail = "654644141@qq.com";
        String roleName = "customer";
        String return_url = "http://localhost:8080";
        String authenticationMethod = "None";
        String emailSubject = "请签署文件";

        Text nameText = new Text();
        nameText.setName("name");
        nameText.setTabLabel("name");
        nameText.setValue("songhaoran");
        nameText.setLocked(Boolean.TRUE.toString());
        Text ageText = new Text();
        ageText.setName("age");
        ageText.setTabLabel("必填");

        Tabs tabs = new Tabs();
        tabs.setTextTabs(Arrays.asList(nameText, ageText));

        TemplateRole templateRole = new TemplateRole();
        templateRole.setEmail(signerEmail);
        templateRole.setName(signerName);
        templateRole.setRoleName(roleName);
        templateRole.setTabs(tabs);

        EnvelopeDefinition envelopeDefinition = new EnvelopeDefinition();
        envelopeDefinition.setEmailSubject(emailSubject);
        envelopeDefinition.setTemplateId(templateId);
        envelopeDefinition.setStatus("sent");
        envelopeDefinition.setTemplateRoles(Arrays.asList(templateRole));

        EnvelopesApi envelopesApi = DocSignUtil.getEnvelopesApi();
        EnvelopeSummary envelopeSummary = null;
        try {
            envelopeSummary = envelopesApi.createEnvelope(DocSignUtil.accountId, envelopeDefinition);
        } catch (ApiException e) {
            log.error("", e);
            throw new RuntimeException(e.getMessage());
        }
        log.info("[]envelope->{}", JSON.toJSONString(envelopeSummary));
        String envelopeId = envelopeSummary.getEnvelopeId();
        log.info("[]envelope_id->{}", envelopeId);

        RecipientViewRequest viewRequest = new RecipientViewRequest();
        viewRequest.setReturnUrl(return_url);
        viewRequest.setAuthenticationMethod(authenticationMethod);
        viewRequest.setEmail(signerEmail);
        viewRequest.setUserName(signerName);
        ViewUrl viewUrl = null;
        try {
            viewUrl = envelopesApi.createRecipientView(DocSignUtil.accountId, envelopeId, viewRequest);
        } catch (ApiException e) {
            log.error("", e);
            throw new RuntimeException(e.getMessage());
        }
        log.info("[]view_url->{}", JSON.toJSONString(viewUrl));
        String signUrl = viewUrl.getUrl();
        log.info("[]sign_url->{}", signUrl);

        return signUrl;
    }

    /**
     * 获取envelope详情
     *
     * @param envelopeId
     * @return
     * @throws Exception
     */
    public Envelope getEnvelopeDetail(String envelopeId) throws Exception {
        EnvelopesApi envelopesApi = DocSignUtil.getEnvelopesApi();
        Envelope envelope = envelopesApi.getEnvelope(DocSignUtil.accountId, envelopeId);
        return envelope;
    }


    /**
     * 下载已签署文档
     *
     * @param envelopeId
     * @throws Exception
     */
    public void downloadSignedDoc(String envelopeId) {
        try {
            EnvelopesApi envelopesApi = DocSignUtil.getEnvelopesApi();
            EnvelopeDocumentsResult envelopeDocumentsResult = envelopesApi.listDocuments(DocSignUtil.accountId, envelopeId);
            List<EnvelopeDocument> envelopeDocumentList = envelopeDocumentsResult.getEnvelopeDocuments();
            envelopeDocumentList.forEach(doc -> {
                this.downloadByUri(doc.getUri(), doc.getName());
            });
        } catch (ApiException e) {
            log.error("[]", e);
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * 下载文档
     *
     * @param uri
     * @param localFileName
     */
    public void downloadByUri(String uri, String localFileName) {
        String downloadApi = "/v2/accounts/{ACCOUNT_ID}";

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Connection", "keep-alive");
        headers.put("Authorization", "Bearer " + DocSignUtil.accessToken);
        String url = DocSignUtil.basePath + (downloadApi.replace("{ACCOUNT_ID}", DocSignUtil.accountId)) + uri;

        HttpGet httpGet = new HttpGet(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpGet.addHeader(entry.getKey(), entry.getValue());
        }

        CloseableHttpClient httpClient = HttpClients.custom().build();
        RequestConfig requestConfig = RequestConfig.copy(RequestConfig.DEFAULT)
                .setSocketTimeout(TIMEOUT)
                .setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT)
                .build();

        httpGet.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            Header header = entity.getContentType();
            HeaderElement[] elements = header.getElements();
            List<String> contentTypes = new ArrayList<>();
            for (HeaderElement element : elements) {
                contentTypes.add(element.getName());
            }
            InputStream inputStream = entity.getContent();

            File targetFile = new File("/Users/songwenhao/Documents/" + localFileName + ".pdf");
            // TODO: 2020/5/15 这里直接传到COS比较好
            try (
                    OutputStream outStream = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            log.error("", e);
        } finally {
            httpGet.releaseConnection();
        }
    }

    /**
     * 下载模板原始文件
     *
     * @param templateId
     * @throws Exception
     */
    public void downloadTemplateDocs(String templateId) {
        try {
            TemplatesApi templatesApi = DocSignUtil.getTemplatesApi();
            TemplateDocumentsResult templateDocumentsResult = templatesApi.listDocuments(DocSignUtil.accountId, templateId);
            List<EnvelopeDocument> documentList = templateDocumentsResult.getTemplateDocuments();

            documentList.forEach(doc -> {
                this.downloadByUri(doc.getUri(), doc.getName());
            });
        } catch (ApiException e) {
            log.error("[]", e);
            throw new RuntimeException("下载模板文件失败");
        }
    }
}
