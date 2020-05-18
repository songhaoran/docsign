package com.song.docsign.controller;

import com.song.docsign.service.EnvelopeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by Song on 2020/05/15.
 */
@RestController
public class TestController {
    @Resource
    private EnvelopeService envelopeService;


    @GetMapping("/template/download_doc")
    @ApiOperation(value = "下载模板的所有文档")
    public void downloadTempDoc(@RequestParam(value = "template_id", required = false) String templateId) {
        templateId = "9e9c4180-a8b3-4cc5-9a95-16537abd729f";
        envelopeService.downloadTemplateDocs(templateId);
    }


    @GetMapping("/envelope/get_sign_url")
    @ApiOperation(value = "获取签字链接(测试重定向链接加参数)")
    public String getSignUrl(@RequestParam(value = "template_id", required = false) String templateId) {
        templateId = "9e9c4180-a8b3-4cc5-9a95-16537abd729f";
        String docSignUrl = envelopeService.getDocSignUrl(templateId);
        return docSignUrl;
    }


    @GetMapping("/envelope/download_signed_doc")
    @ApiOperation(value = "下载签署后的签署文件")
    public void downloadSignedDoc(@RequestParam(value = "envelope_id", required = false) String envelopeId) {
        envelopeId = "8743fe0d-8ae7-45cc-8f9c-5c43d04c259c";
        envelopeService.downloadSignedDoc(envelopeId);
    }


    @GetMapping("/template/create")
    @ApiOperation(value = "创建模板")
    public String createTemplate() {
        return envelopeService.createTemplate();
    }
}
