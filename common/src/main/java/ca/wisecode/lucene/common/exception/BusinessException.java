package ca.wisecode.lucene.common.exception;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/27/2024 10:18 AM
 * @Version: 1.0
 * @description:
 */

public class BusinessException extends RuntimeException {

    private int errorCode;  // 错误代码，可以用于标识特定错误类型
    private String errorMessage;  // 错误消息，详细描述错误内容

    // 构造方法
    public BusinessException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BusinessException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    // 可以增加其他的构造方法用于不同场景
    public BusinessException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorMessage = errorMessage;
    }
    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(int errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    // Getter 和 Setter 方法
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}

