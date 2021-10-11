package com.maven.common.handler;

import com.jfinal.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UrlHandler extends Handler {

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        String downloadPath = "/upload/download/";
        if (target.startsWith(downloadPath)) {
            String fileName = target.substring(downloadPath.length());

            if (fileName != null) {
                int iDot = fileName.lastIndexOf(".");

                if (iDot >= 0) {
                    String name = fileName.substring(0, iDot);
                    String ext = fileName.substring(iDot + 1);
                    target = downloadPath + name.replaceAll("/", "@__@") + "-" + ext;
                } else {
                    target = downloadPath + fileName.replaceAll("/", "@__@");
                }
            }
        }
        next.handle(target, request, response, isHandled);
    }
}