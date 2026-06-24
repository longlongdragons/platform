package org.example.platformspringboot.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class FileUtil {

    @Value("${file.upload-dir}")
    private String uploadDir;
    @Value("${file.domain}")
    private String domain;
    @Value("${file.nginx-prefix}")
    private String nginxPrefix;

    /** 拼接可直接访问的URL */
    public String toUrl(String relativePath) {
        return domain + nginxPrefix + relativePath;
    }

    /** 相对路径(去除uploadDir前缀) */
    public String toRelative(String absolutePath) {
        if (absolutePath == null) return null;
        return absolutePath.substring(uploadDir.length()).replace('\\', '/').replaceFirst("^/", "");
    }

    /** 上传单文件，返回相对路径 */
    public String save(MultipartFile file, String subDir) throws IOException {
        String date = LocalDate.now().toString();
        String dir = uploadDir + "/" + subDir + "/" + date;
        new File(dir).mkdirs();
        String ext = "";
        String oname = file.getOriginalFilename();
        if (oname != null && oname.contains(".")) {
            ext = oname.substring(oname.lastIndexOf('.'));
        }
        String name = UUID.randomUUID().toString().replace("-", "") + ext;
        File dest = new File(dir, name);
        file.transferTo(dest);
        return subDir + "/" + date + "/" + name;
    }

    /** 保存分片到 uploadDir/chunk/{uploadId}/{index} */
    public File saveChunk(String uploadId, int index, MultipartFile file) throws IOException {
        File dir = new File(uploadDir + "/chunk/" + uploadId);
        dir.mkdirs();
        File dest = new File(dir, String.valueOf(index));
        file.transferTo(dest);
        return dest;
    }

    /** 合并分片到正式目录，返回相对路径 */
    public String mergeChunks(String uploadId, int total, String filename, String subDir) throws IOException {
        String date = LocalDate.now().toString();
        File targetDir = new File(uploadDir + "/" + subDir + "/" + date);
        targetDir.mkdirs();
        String ext = "";
        if (filename != null && filename.contains(".")) {
            ext = filename.substring(filename.lastIndexOf('.'));
        }
        String name = UUID.randomUUID().toString().replace("-", "") + ext;
        File target = new File(targetDir, name);

        try (FileOutputStream fos = new FileOutputStream(target);
             FileChannel outCh = fos.getChannel()) {
            for (int i = 0; i < total; i++) {
                File chunk = new File(uploadDir + "/chunk/" + uploadId + "/" + i);
                if (!chunk.exists()) throw new IOException("分片缺失: " + i);
                try (FileInputStream fis = new FileInputStream(chunk);
                     FileChannel inCh = fis.getChannel()) {
                    inCh.transferTo(0, inCh.size(), outCh);
                }
            }
        }
        // 清理分片
        File chunkDir = new File(uploadDir + "/chunk/" + uploadId);
        deleteDir(chunkDir);
        return subDir + "/" + date + "/" + name;
    }

    public void deleteDir(File dir) {
        if (dir == null || !dir.exists()) return;
        File[] files = dir.listFiles();
        if (files != null) for (File f : files) {
            if (f.isDirectory()) deleteDir(f);
            else f.delete();
        }
        dir.delete();
    }

    /** 计算文件SHA-256(用于秒传) */
    public String sha256(File file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (InputStream is = new FileInputStream(file);
             FileChannel ch = ((FileInputStream) is).getChannel()) {
            // 简单分块读取
            byte[] buf = new byte[8192];
            int n;
            while ((n = is.read(buf)) > 0) md.update(buf, 0, n);
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : md.digest()) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
