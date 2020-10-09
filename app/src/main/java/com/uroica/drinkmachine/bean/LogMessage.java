package com.uroica.drinkmachine.bean;
 
public class LogMessage {
    /**
     * 消息文本
     *
     * @return
     */
    String message;

    /**
     * 是否发送的消息
     * true 發送 false接受
     *
     * @return
     */
    boolean isToSend;
    /**
     * 时间
     */

    long currentTime;

    public LogMessage() {
    }

    public LogMessage(String message, boolean isToSend, long currentTime) {
        this.message = message;
        this.isToSend = isToSend;
        this.currentTime = currentTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isToSend() {
        return isToSend;
    }

    public void setToSend(boolean toSend) {
        isToSend = toSend;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }
}
