package com.newcoder.community.Quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 * @author 86156
 * 定时任务，删除存在5分钟以上的分享截图
 */
public class DelShareJob implements Job {

    @Value("${community.path.wk.filePath}")
    String filePath;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        File fileList = new File(filePath);
        if (!fileList.exists()||fileList.isFile())
        {
            return;
        }
        File[] files = fileList.listFiles();
        for (File file:files)
        {
           Path path = Paths.get(filePath+'/'+file.getName());
            BasicFileAttributeView fileAttr = Files.getFileAttributeView(path,BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
            try {
                BasicFileAttributes basicFileAttributes = fileAttr.readAttributes();
                FileTime fileTime = basicFileAttributes.lastModifiedTime();
                long millis = fileTime.toMillis();
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis-millis >= 1000*60*5)
                {
                        file.delete();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
