package ca.wisecode.lucene.common.util;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/4/2024 10:35 AM
 * @Version: 1.0
 * @description:
 */

public final class Constants {
    // 定义常量
    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";

    public static final String MY_NAME = "CAIXIAN WANG";
    public static final String _ID_ = "_ID_";
    public static final String _PRJID_ = "_PRJID_";
    public static final int MAX_ROWS = 100;
    /**
     * 内存中暂存的最大索引记录数
     */
    public static final int MEMORY_MAX_INDEX = 1;

    /**
     * 分发时随机向节点插入的数据量
     */
    public static final int INSERT_ROWS = 2;


    private Constants() {
        throw new AssertionError("不能实例化 Constants 类");
    }
}
