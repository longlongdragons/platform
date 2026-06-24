package org.example.platformspringboot.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * 敏感词过滤 - 基于Trie树 (DFA)
 * 默认从 classpath:sensitive-words.txt 加载词库，每行一个
 * 匹配时将敏感词替换为 *
 */
@Component
public class SensitiveWordFilter {

    private static final String REPLACEMENT = "***";
    private TrieNode root = new TrieNode();

    @PostConstruct
    void init() {
        try {
            ClassPathResource res = new ClassPathResource("sensitive-words.txt");
            Set<String> words = new HashSet<>();
            if (res.exists()) {
                String content = StreamUtils.copyToString(res.getInputStream(), StandardCharsets.UTF_8);
                for (String line : content.split("\\r?\\n")) {
                    line = line.trim();
                    if (!line.isEmpty()) words.add(line);
                }
            }
            // 兜底内置词
            words.add("傻逼"); words.add("色情"); words.add("暴力"); words.add("反动");
            words.add("法轮"); words.add("毒品"); words.add("枪支");
            for (String w : words) addWord(w);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addWord(String word) {
        TrieNode cur = root;
        for (char c : word.toCharArray()) {
            cur.children.computeIfAbsent(c, k -> new TrieNode());
            cur = cur.children.get(c);
        }
        cur.end = true;
    }

    /** 是否包含敏感词 */
    public boolean contains(String text) {
        if (text == null || text.isEmpty()) return false;
        TrieNode cur = root;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            TrieNode next = cur.children.get(c);
            if (next == null) {
                cur = root;
                if (cur.children.get(c) != null) {
                    cur = cur.children.get(c);
                    if (cur.end) return true;
                }
                continue;
            }
            cur = next;
            if (cur.end) return true;
        }
        return false;
    }

    /** 替换敏感词为星号 */
    public String replace(String text) {
        if (text == null || text.isEmpty()) return text;
        StringBuilder sb = new StringBuilder(text);
        for (int i = 0; i < sb.length(); i++) {
            int len = check(sb, i);
            if (len > 0) {
                for (int j = i; j < i + len; j++) sb.setCharAt(j, '*');
                i += len - 1;
            }
        }
        return sb.toString();
    }

    private int check(StringBuilder sb, int start) {
        TrieNode cur = root;
        int len = 0;
        for (int i = start; i < sb.length(); i++) {
            char c = sb.charAt(i);
            TrieNode next = cur.children.get(c);
            if (next == null) break;
            cur = next;
            len++;
            if (cur.end) return len;
        }
        return 0;
    }

    private static class TrieNode {
        boolean end = false;
        java.util.Map<Character, TrieNode> children = new java.util.HashMap<>();
    }
}
