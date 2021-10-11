package com.maven.common.upload;

import com.jfinal.aop.Clear;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import com.maven.common.bean.UploadFileRetBean;
import com.maven.common.controller.AppBaseController;
import com.maven.common.interceptor.MyInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Clear(MyInterceptor.class)
public class UploadController extends AppBaseController {
    public void fileupload() {
        //分时间存放文件
        String prefix = DateTime.now().toString("yyyyMM");
        List<UploadFile> files = this.getFiles(prefix);
        List<UploadFileRetBean> list = new ArrayList<>();
        files.forEach(file -> {
            String fileName = file.getFileName();
            String path = prefix.concat("/").concat(fileName);

            list.add(new UploadFileRetBean(path, fileName));
            //返回路径给客户端
        });
        this.renderJson(Ret.ok("msg", "上传成功!").set("value", list));
    }

    public void download() {
        String name = this.getPara(0);
        String ext = this.getPara(1);
        if (StringUtils.isBlank(ext)) {
            this.renderError(404);
            return;
        }
        name = name.replaceAll("@__@", "/");

        String basePath = JFinal.me().getConstants().getBaseUploadPath();
        String path = StringUtils.isBlank(ext) ? name : name + "." + ext;
        String uploadPath = basePath + "/" + path;
        File file = new File(uploadPath);

        if (file.isDirectory()) {
            this.renderError(404);
            return;
        }
        if (!file.exists()) {
            this.renderError(404);
            return;
        }
        this.renderFile(path);
    }
}