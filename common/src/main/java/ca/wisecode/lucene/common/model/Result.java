package ca.wisecode.lucene.common.model;


/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/6/2024 11:25 AM
 * @Version: 1.0
 * @description:
 */

public class Result {
    private boolean handled;
    private String msg;

    public Result(boolean handled, String msg) {
        this.handled = handled;
        this.msg = msg;
    }

    public boolean isHandled() {
        return handled;
    }

    public String getMsg() {
        return msg;
    }
}
