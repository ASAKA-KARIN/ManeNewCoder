package com.newcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词过滤器
 *
 * @author Yoshino
 */
@Component
public class SensitiveFilter {

    private final static Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    private final static String REPLACE_WORD = "****";
    private TrieNode root;
    SensitiveFilter(){
        root = new TrieNode();
    }

    /**
     * 初始化方法
     * @PostConstrut保证了该方法在Spring完成Autowired后就被执行
     * 执行顺序 construct -> autowired -> postConstruct
     */
    @PostConstruct
     void init() {
        try (
                //获取当前类加载器，并拿到类路径下sensitive_word.txt对应的输入流
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sensitive_word.txt");
                //包装成缓冲流，方便更高效的读取文件
                BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            String readLine = bf.readLine();
            while (!StringUtils.isBlank(readLine)) {
                //初始化前缀树,将sensitive_word.txt中的所有违禁词存入到树中。
                addTrieNode(readLine);
                readLine = bf.readLine();
            }
        } catch (IOException ioException) {
            logger.error("初始化敏感词过滤器失败！" + ioException.getMessage());
            throw new RuntimeException();
        }
    }

    /**
     * 过滤敏感词
     * @param text
     * @return 过滤后的敏感词
     *
     */
    public String filter(String text)
    {
        //保存过滤后的文章
        StringBuilder sb = new StringBuilder();
        //指针一:用来遍历前缀树
        TrieNode temp = root;
        //指针二:用来指向被遍历词语的开头
        int begin = 0;
        //指针三:用来指向被遍历词语的结尾
        int end = 0;
        //从定义可知，end的位置一定大于等于begin的位置
        //为了避免数组越界已经提高过滤效率
        // 当end指向文章末尾时即可结束过滤
        while (end < text.length())
        {
            char c = text.charAt(end);
            /**
             * 判断是否为特殊字符
             * 主要是防止用户在发送敏感词时用特殊字符来间隔以达到目的
             * 遇到特殊字符时有两种情况:
             * 1.若此时temp仍指在root节点，则说明该特殊字符没有用来间隔敏感词
             *   此时需要将该特殊字符存入结果中
             * 2.若此时temp没有指在root节点，则说明该特殊字符有可能被用来间隔敏感词
             *   此时需要跳过该字符，进行下一次过滤。
             */
            if (isSymbol(c))
            {
                //对应情况1
                if (temp == root)
                {
                    sb.append(c);
                    begin++;
                }
                //情况二
                end++;
                continue;
            }
            /**
             * 过滤特殊字符存在三种情况
             * 1.情况一:获取不到当前字符对应的节点
             *          说明正在过滤的该text的begin到end为健康词汇
             *          我们需要将begin对应的char放入到结果中
             *          并将begin的位置加一,再将end与begin对齐
             * 2.情况二:遍历到了叶子节点
             *          说明正在过滤的该text的begin到end为敏感词汇
             *          我们需要用REPLACE_WORD将这部分字符串代替
             *          并将end的位置+1，再将begin与end对齐
             * 3.情况三：能够在前缀树中找到end位置对应的char
             *          说明正在过滤的该text的begin到end有可能为敏感词汇，具体结果仍需向后遍历才能知道
             *          我们需要将end加一继续遍历，直到遇到情况一或情况三为止
             */
            temp = temp.getSubNode(c);
            //情况一
            if (temp == null)
            {
                //这里只添加begin的原因是我们目前只能确定从begin到end的字符串不是敏感词
                //无法确定从begin+1到end的字符串的情况
                sb.append(text.charAt(begin));
                temp = root;
                end = ++begin;
                //情况二
            }else if(temp.isEnd)
            {
                sb.append(REPLACE_WORD);
                begin = ++end;
                temp = root;
            //情况三
            }else
            {
                end++;
            }
        }
        //循环结束后，我们不能保证text已经完全被添加进了最终结果中
        //因为在遍历结束后end > begin
        //我们需要将begin后的字符串添加进最后的结果中
        sb.append(text.substring(begin));

        return sb.toString();
    }
    /**
     * 判断字符是否为特殊字符
     * 其中0x2E80到0x9FFF是东亚文字范围
     * @param ch
     * @return
     */
    private boolean isSymbol(char ch)
    {
        return !CharUtils.isAsciiAlphanumeric(ch)&&(ch < 0x2E80||ch > 0x9FFF);
    }

    /**
     * 向前缀树中添加节点
     * @param s
     */
    private void addTrieNode(String s) {
        //临时节点
        TrieNode temp = root;
        //遍历字符串中每一个字符
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            TrieNode subNode = temp.getSubNode(c);
            if (subNode == null)
            {
                subNode = new TrieNode();
                temp.addSubNode(c,subNode);
            }
            temp = subNode;
            //当遍历到最后时，将isEnd设置为true
            if (i == s.length()-1)
            {
                temp.setEnd(true);
            }
        }
    }

    private class TrieNode {
        //该节点是否为叶子节点
        boolean isEnd;
        //该节点的子节点
        Map<Character, TrieNode> subNode;

        TrieNode() {
            isEnd = false;
            subNode = new HashMap<>();
        }

        public boolean isEnd() {
            return isEnd;
        }

        public void setEnd(boolean end) {
            isEnd = end;
        }

        public TrieNode getSubNode(char c) {
            return subNode.get(c);
        }
        public void addSubNode(char s, TrieNode trieNode) {
            subNode.put(s, trieNode);
        }

    }


}
