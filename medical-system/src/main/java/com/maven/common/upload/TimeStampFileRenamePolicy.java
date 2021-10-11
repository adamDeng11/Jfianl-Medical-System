package com.maven.common.upload;

import com.jfinal.kit.StrKit;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import org.joda.time.DateTime;

import java.io.File;

public class TimeStampFileRenamePolicy extends DefaultFileRenamePolicy {

    @Override
    public File rename(File f) {
        //使用时间戳重命名
        String fileName = DateTime.now().toString("yyyyMMdd_HHmmss_SSS");
        String extName = f.getName().substring(f.getName().lastIndexOf("."));
        f = new File(f.getParentFile(), fileName + (StrKit.isBlank(extName) ? "" : extName));
        return super.rename(f);
    }
}