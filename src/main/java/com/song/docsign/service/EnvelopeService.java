package com.song.docsign.service;

import com.alibaba.fastjson.JSON;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.api.TemplatesApi;
import com.docusign.esign.api.UsersApi;
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

import javax.annotation.Resource;
import java.io.*;
import java.util.List;
import java.util.*;


/**
 * Created by Song on 2020/05/15.
 */
@Component
@Slf4j
public class EnvelopeService {

    private static final int TIMEOUT = 5000;

    @Resource
    private DocSignUtil docSignUtil;

    /**
     * 生成签署文件链接
     *
     * @return
     * @throws Exception
     */
    public String getDocSignUrl(String templateId) throws Exception {
        // TODO: 2020/5/21 如果实际应用,应先将订单之前的信封作废

        String signerName = "wen";
//        String signerEmail = "30888649@qq.com";
        String signerEmail = "654644141@qq.com";
        String signerClientUserId = "";
        String roleName = "investor";
        String return_url = "http://localhost:8080?envelope_id=";
        String authenticationMethod = "None";
        String emailSubject = "请签署文件";


        // 1. 创建用户
        UsersApi usersApi = docSignUtil.getUsersApi();
        UserInformation userInformation = new UserInformation();
        userInformation.setEmail(signerEmail);
        userInformation.setFirstName("wenhao");
        userInformation.setLastName("song");
        userInformation.setUserName(userInformation.getFirstName() + " " + userInformation.getLastName());
        NewUsersDefinition newUsersDefinition = new NewUsersDefinition();
        newUsersDefinition.addNewUsersItem(userInformation);
        List<NewUser> newUsers = null;
        try {
            NewUsersSummary newUsersSummary = usersApi.create(docSignUtil.getAccountId(), newUsersDefinition);
            newUsers = newUsersSummary.getNewUsers();
            if (newUsers.size() == 0) {
                return "";
            }
        } catch (ApiException e) {
            e.printStackTrace();
            return "";
        }
        log.info("[]new user->{}", JSON.toJSONString(newUsers));
        signerClientUserId = newUsers.get(0).getUserId();


        // 2. 创建envelope
        Text nameText = new Text();
        nameText.setTabLabel("${cmn_float_percent}");
        nameText.setValue("songhaoran");
        nameText.setLocked(Boolean.TRUE.toString());
        Text ageText = new Text();
        ageText.setTabLabel("name");
        ageText.setValue("名字");

        Tabs tabs = new Tabs();
        tabs.setTextTabs(Arrays.asList(nameText, ageText));

        TemplateRole templateRole = new TemplateRole();
        templateRole.setEmail(signerEmail);
        templateRole.setName(signerName);
        templateRole.setClientUserId(signerClientUserId);
        templateRole.setRoleName(roleName);
        templateRole.setTabs(tabs);
        templateRole.setAccessCode("12345s");

        EnvelopeDefinition envelopeDefinition = new EnvelopeDefinition();
        envelopeDefinition.setEmailSubject(emailSubject);
        envelopeDefinition.setTemplateId(templateId);
        envelopeDefinition.setStatus("sent");
        envelopeDefinition.setTemplateRoles(Arrays.asList(templateRole));

        EnvelopesApi envelopesApi = docSignUtil.getEnvelopesApi();
        EnvelopeSummary envelopeSummary;
        try {
            envelopeSummary = envelopesApi.createEnvelope(docSignUtil.getAccountId(), envelopeDefinition);
        } catch (ApiException e) {
            log.error("", e);
            throw new RuntimeException(e.getMessage());
        }
        log.info("[]envelope->{}", JSON.toJSONString(envelopeSummary));
        String envelopeId = envelopeSummary.getEnvelopeId();
        log.info("[]envelope_id->{}", envelopeId);


        // 3. 获取签字链接
        RecipientViewRequest viewRequest = new RecipientViewRequest();
        viewRequest.setReturnUrl(return_url + envelopeId);
        viewRequest.setAuthenticationMethod(authenticationMethod);
        viewRequest.setEmail(signerEmail);
        viewRequest.setUserName(signerName);
        viewRequest.setClientUserId(signerClientUserId);
        ViewUrl viewUrl;
        try {
            viewUrl = envelopesApi.createRecipientView(docSignUtil.getAccountId(), envelopeId, viewRequest);
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
     * 下载已签署文档
     *
     * @param envelopeId
     * @throws Exception
     */
    public void downloadSignedDoc(String envelopeId) throws Exception {
        try {
            EnvelopesApi envelopesApi = docSignUtil.getEnvelopesApi();
            EnvelopeDocumentsResult envelopeDocumentsResult = envelopesApi.listDocuments(docSignUtil.getAccountId(), envelopeId);
            List<EnvelopeDocument> envelopeDocumentList = envelopeDocumentsResult.getEnvelopeDocuments();
            envelopeDocumentList.forEach(doc -> {
                try {
                    this.downloadByUri(doc.getUri(), doc.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
    public void downloadByUri(String uri, String localFileName) throws Exception {
        String downloadApi = "/v2/accounts/{ACCOUNT_ID}";

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Connection", "keep-alive");
        headers.put("Authorization", docSignUtil.getAuthorization());
        String url = DocSignUtil.sand_box_rest_api_base_url + (downloadApi.replace("{ACCOUNT_ID}", docSignUtil.getAccountId())) + uri;

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
    public void downloadTemplateDocs(String templateId) throws Exception {
        try {
            TemplatesApi templatesApi = docSignUtil.getTemplatesApi();
            TemplateDocumentsResult templateDocumentsResult = templatesApi.listDocuments(docSignUtil.getAccountId(), templateId);
            List<EnvelopeDocument> documentList = templateDocumentsResult.getTemplateDocuments();

            documentList.forEach(doc -> {
                try {
                    this.downloadByUri(doc.getUri(), doc.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (ApiException e) {
            log.error("[]", e);
            throw new RuntimeException("下载模板文件失败");
        }
    }


    /**
     * 创建模板
     *
     * @return
     * @throws Exception
     */
    public String createTemplate() throws Exception {
        try {
            InputStream in = null;
            File file;
            String base64;
            try {
                file = new File("/Users/songwenhao/Documents/文档/公募基金订单模板1.pdf");
                in = new FileInputStream(file);
                byte[] bytes = new byte[(int) file.length()];
                in.read(bytes);
                base64 = Base64.getEncoder().encodeToString(bytes);
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            } finally {
                if (in != null) {
                    in.close();
                }
            }


            Signer signer = new Signer();
            // TODO: 2020/5/18 这里需要确认下怎么查
            signer.setRecipientId("15764476");
            signer.setRoleName("investor");

            Recipients recipients = new Recipients();
            recipients.setSigners(Arrays.asList(signer));

            Document document = new Document();
            document.setDocumentBase64(base64);
            document.setName(file.getName());
            document.setDocumentId("1");
            document.setOrder("1");
            document.setTransformPdfFields(Boolean.TRUE.toString());

            EnvelopeTemplate envelopeTemplate = new EnvelopeTemplate();
            envelopeTemplate.setRecipients(recipients);
            envelopeTemplate.setDocuments(Arrays.asList(document));
            envelopeTemplate.setFolderName("Templates");
            envelopeTemplate.setName("测试api上传模板");
            envelopeTemplate.setDescription("测试desc");
            envelopeTemplate.setShared(Boolean.TRUE.toString());

            TemplatesApi templatesApi = docSignUtil.getTemplatesApi();
            TemplateSummary templateSummary = templatesApi.createTemplate(docSignUtil.getAccountId(), envelopeTemplate);
            String templateId = templateSummary.getTemplateId();
            return templateId;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (ApiException e) {
            e.printStackTrace();
            return "";
        }
    }


}
