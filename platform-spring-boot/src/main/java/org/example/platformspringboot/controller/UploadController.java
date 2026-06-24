package org.example.platformspringboot.controller;

import org.example.platformspringboot.common.BizException;
import org.example.platformspringboot.common.RedisKey;
import org.example.platformspringboot.common.Result;
import org.example.platformspringboot.utils.FileUtil;
import org.example.platformspringboot.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 文件分片上传
 * 1) /init       -> 创建上传任务，返回uploadId
 * 2) /chunk      -> 接收分片
 * 3) /merge      -> 合并分片
 * 4) /simple     -> 小文件单次上传(非分片)
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired private FileUtil fileUtil;
    @Autowired private StringRedisTemplate redis;

    @PostMapping("/init")
    public Result<Map<String, Object>> init(@RequestParam String filename,
                                            @RequestParam long totalSize,
                                            @RequestParam int chunkCount,
                                            @RequestParam(required = false) String fileHash) {
        String uploadId = UUID.randomUUID().toString().replace("-", "");
        // 秒传：实际场景需对比hash; 这里仅记录
        Map<String, Object> meta = new HashMap<>();
        meta.put("filename", filename);
        meta.put("totalSize", totalSize);
        meta.put("chunkCount", chunkCount);
        meta.put("fileHash", fileHash);
        meta.put("userId", UserContext.getUserId());
        meta.put("uploaded", new java.util.HashSet<>());
        redis.opsForHash().putAll(RedisKey.uploadMetaKey(uploadId), meta);
        redis.expire(RedisKey.uploadMetaKey(uploadId), 1, TimeUnit.DAYS);
        return Result.success(Map.of("uploadId", uploadId));
    }

    @PostMapping("/chunk")
    public Result<Map<String, Object>> chunk(@RequestParam String uploadId,
                                             @RequestParam int index,
                                             @RequestParam MultipartFile file) throws Exception {
        Object userIdObj = redis.opsForHash().get(RedisKey.uploadMetaKey(uploadId), "userId");
        if (userIdObj == null) throw new BizException("上传任务不存在或已过期");
        long uid = Long.parseLong(userIdObj.toString());
        if (uid != UserContext.getUserId()) throw new BizException("无权操作此上传");
        File saved = fileUtil.saveChunk(uploadId, index, file);
        // 记录已上传分片
        redis.opsForHash().put(RedisKey.uploadMetaKey(uploadId),
                "chunk_" + index, saved.length());
        return Result.success(Map.of("index", index, "size", saved.length()));
    }

    @PostMapping("/merge")
    public Result<Map<String, Object>> merge(@RequestParam String uploadId,
                                             @RequestParam String filename,
                                             @RequestParam String subDir) throws Exception {
        Object chunkCountObj = redis.opsForHash().get(RedisKey.uploadMetaKey(uploadId), "chunkCount");
        if (chunkCountObj == null) throw new BizException("上传任务不存在");
        int total = Integer.parseInt(chunkCountObj.toString());
        String rel = fileUtil.mergeChunks(uploadId, total, filename, subDir);
        String url = fileUtil.toUrl(rel);
        redis.delete(RedisKey.uploadMetaKey(uploadId));
        return Result.success(Map.of("url", url, "relative", rel));
    }

    /** 小文件单次上传 */
    @PostMapping("/simple")
    public Result<Map<String, Object>> simple(@RequestParam MultipartFile file,
                                              @RequestParam String subDir) throws Exception {
        String rel = fileUtil.save(file, subDir);
        String url = fileUtil.toUrl(rel);
        return Result.success(Map.of("url", url, "relative", rel));
    }
}
