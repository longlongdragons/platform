package org.example.platformspringboot.common;

public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public static <T> Result<T> success()        { return build(0, "ok", null); }
    public static <T> Result<T> success(T data)  { return build(0, "ok", data); }
    public static <T> Result<T> error(String msg){ return build(500, msg, null); }
    public static <T> Result<T> error(int code, String msg){ return build(code, msg, null); }

    private static <T> Result<T> build(int code, String msg, T data) {
        Result<T> r = new Result<>();
        r.code = code; r.message = msg; r.data = data;
        return r;
    }
}