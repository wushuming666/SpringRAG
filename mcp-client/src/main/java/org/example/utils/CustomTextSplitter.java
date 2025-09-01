package org.example.utils;

import org.springframework.ai.transformer.splitter.TextSplitter;
import java.util.ArrayList;
import java.util.List;

public class CustomTextSplitter extends TextSplitter {

    @Override
    protected List<String> splitText(String text) {
        // 使用更复杂的正则表达式来分割FAQ条目
        // 匹配问题编号模式进行分割
        String[] faqSections = text.split("(?=\\*\\*\\d+、\\*\\*[^\\n]*\\*{2,}\\s*\\n-*)");

        List<String> result = new ArrayList<>();

        for (String section : faqSections) {
            String trimmedSection = section.trim();
            if (!trimmedSection.isEmpty()) {
                result.add(trimmedSection);
            }
        }

        // 如果分割结果不合理，使用默认方式
        if (result.size() <= 1) {
            String[] paragraphs = text.split("\\n\\n");
            result.clear();
            for (String paragraph : paragraphs) {
                String trimmedParagraph = paragraph.trim();
                if (!trimmedParagraph.isEmpty()) {
                    result.add(trimmedParagraph);
                }
            }
        }

        return result;
    }
}
