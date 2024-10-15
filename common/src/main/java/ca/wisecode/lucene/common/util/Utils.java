package ca.wisecode.lucene.common.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/24/2024 1:09 PM
 * @Version: 1.0
 * @description:
 */

public class Utils {
    /**
     * "2023-09-22",
     */
    private static final Pattern DATE_YYYY_MM_DD = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    /**
     * "2023-11-08 19:52:19",
     */
    private static final Pattern DATE_YYYY_MM_DD_HH_MM_SS = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$");

    /**
     * "09/22/2023",
     */
    private static final Pattern DATE_MM_DD_YYYY = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");
    /**
     * "2023-09-22T14:00:00",
     */
    private static final Pattern DATETIME_YYYY_MM_DD_T_HH_MM_SS = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$");
    /**
     * "2023-09-22T14:00:00.000+0200",
     */
    private static final Pattern DATETIME_YYYY_MM_DD_T_HH_MM_SS_SSSZ = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}[+-]\\d{4}$");
    /**
     * "2023/09/22 14:00:00",
     */
    private static final Pattern DATE_YYYY_MM_DD_HH_MM_SS2 = Pattern.compile("^\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}$");
    /**
     * "Fri, 22 Sep 2023 14:00:00 GMT"  // RFC 1123
     */
    private static final Pattern RFC_1123_DATE_TIME = Pattern.compile("^\\w{3}, \\d{2} \\w{3} \\d{4} \\d{2}:\\d{2}:\\d{2} \\w{3}$");

    // 正则表达式匹配数值（整数、小数、科学计数法）
    private static final Pattern NUMERIC_PATTERN = Pattern.compile(
            "^[-+]?\\d*(\\.\\d+)?([eE][-+]?\\d+)?$"
    );
    // 判断是否符合任何日期格式
    public static boolean isValidDate(String dateStr) {
        return DATE_YYYY_MM_DD.matcher(dateStr).matches() ||
                DATE_YYYY_MM_DD_HH_MM_SS.matcher(dateStr).matches() ||
                DATE_MM_DD_YYYY.matcher(dateStr).matches() ||
                DATETIME_YYYY_MM_DD_T_HH_MM_SS.matcher(dateStr).matches() ||
                DATETIME_YYYY_MM_DD_T_HH_MM_SS_SSSZ.matcher(dateStr).matches() ||
                DATE_YYYY_MM_DD_HH_MM_SS2.matcher(dateStr).matches() ||
                RFC_1123_DATE_TIME.matcher(dateStr).matches();
    }

    // 判断并解析日期字符串，返回时间戳（毫秒）
    public static Long parseDateToMillis(String dateStr) {
        if (DATE_YYYY_MM_DD.matcher(dateStr).matches()) {
            // yyyy-MM-dd 格式
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            return date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        } else if (DATE_YYYY_MM_DD_HH_MM_SS.matcher(dateStr).matches()) {
            // yyyy-MM-dd HH:mm:ss 格式
            LocalDateTime dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        } else if (DATE_MM_DD_YYYY.matcher(dateStr).matches()) {
            // MM/dd/yyyy 格式
            System.out.println("-----------" + dateStr);
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            return date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        } else if (DATETIME_YYYY_MM_DD_T_HH_MM_SS.matcher(dateStr).matches()) {
            // yyyy-MM-dd'T'HH:mm:ss 格式
            LocalDateTime dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        } else if (DATETIME_YYYY_MM_DD_T_HH_MM_SS_SSSZ.matcher(dateStr).matches()) {
            // yyyy-MM-dd'T'HH:mm:ss.SSSZ 格式
            LocalDateTime dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
            return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        } else if (DATE_YYYY_MM_DD_HH_MM_SS2.matcher(dateStr).matches()) {
            // yyyy/MM/dd HH:mm:ss 格式
            LocalDateTime dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
            return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        } else if (RFC_1123_DATE_TIME.matcher(dateStr).matches()) {
            // RFC 1123 格式
            LocalDateTime dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.RFC_1123_DATE_TIME);
            return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        }
        return null; // 未匹配到任何格式
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;  // 空字符串不算作数值
        }
        return NUMERIC_PATTERN.matcher(str).matches();
    }
    public static Number convertToNumber(String str) {
        if (!isNumeric(str)) {
            return null; // 字符串不是数值
        }

        // 判断是否包含小数点或科学计数法符号
        if (str.contains(".") || str.toLowerCase().contains("e")) {
            // 转换为 BigDecimal（适用于小数和科学计数法）
            return new BigDecimal(str);
        } else {
            // 如果没有小数点或科学计数法符号，说明是整数，先判断是否为 Long
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException e) {
                // 如果超过 Long 的范围，则直接返回 BigDecimal
                return new BigDecimal(str);
            }
        }
    }


    public static void main(String[] args) {
        // 测试示例
        String[] testDates = {
                "2023-09-22",
                "2023-11-08 19:52:19",
                "09/22/2023",
                "2023-09-22T14:00:00",
                "2023-09-22T14:00:00.000+0200",
                "2023/09/22 14:00:00",
                "Fri, 22 Sep 2023 14:00:00 GMT"  // RFC 1123
        };

        for (String testDate : testDates) {
            Long millis = parseDateToMillis(testDate);
            if (millis != null) {
                System.out.println(millis);
            }
            System.out.println(testDate + " 是有效日期格式吗？" + isValidDate(testDate));
        }
    }
}
