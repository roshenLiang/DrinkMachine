package com.pudutech.mqtt.component.client.config;

import com.pudutech.mqtt.component.client.callback.LoginStateCallback;
import com.pudutech.mqtt.component.client.callback.MessageReceiverListener;

import java.util.Vector;

/**
 * @author FreddyChen
 * @name
 * @date 2020/05/29 11:28
 * @email chenshichao@outlook.com
 * @github https://github.com/FreddyChen
 * @desc
 */
public class MqttParamOptions {

    private boolean hasUserName;
    private boolean hasPassword;
    private String userName;
    private String password;
    private Vector<String> addrs;
    private LoginStateCallback loginStateCallback;
    private MessageReceiverListener messageReceiverListener;

    private MqttParamOptions(Builder builder) {
        if(builder != null) {
            this.hasUserName = builder.hasUserName;
            this.hasPassword = builder.hasPassword;
            this.userName = builder.userName;
            this.password = builder.password;
            this.addrs = builder.addrs;
            this.loginStateCallback = builder.loginStateCallback;
            this.messageReceiverListener = builder.messageReceiverListener;
        }
    }

    public boolean isHasUserName() {
        return hasUserName;
    }

    public boolean isHasPassword() {
        return hasPassword;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Vector<String> getAddrs() {
        return addrs;
    }

    public LoginStateCallback getLoginStateCallback() {
        return loginStateCallback;
    }

    public MessageReceiverListener getMessageReceiverListener() {
        return messageReceiverListener;
    }

    @Override
    public String toString() {
        return "MqttParamOptions{" +
                "hasUserName=" + hasUserName +
                ", hasPassword=" + hasPassword +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", addrs=" + addrs +
                ", loginStateCallback=" + loginStateCallback +
                ", messageReceiverListener=" + messageReceiverListener +
                '}';
    }

    public static class Builder {

        private boolean hasUserName;
        private boolean hasPassword;
        private String userName;
        private String password;
        private MqttParamOptions options;
        private Vector<String> addrs;
        private LoginStateCallback loginStateCallback;
        private MessageReceiverListener messageReceiverListener;

        public Builder() {

        }

        public Builder hasUserName(boolean hasUserName) {
            this.hasUserName = hasUserName;
            return this;
        }

        public Builder hasPassword(boolean hasPassword) {
            this.hasPassword = hasPassword;
            return this;
        }

        public Builder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setAddrs(Vector<String> addrs) {
            this.addrs = addrs;
            return this;
        }

        public Builder setLoginStateCallback(LoginStateCallback loginStateCallback) {
            this.loginStateCallback = loginStateCallback;
            return this;
        }

        public Builder setMessageReceiverListener(MessageReceiverListener messageReceiverListener) {
            this.messageReceiverListener = messageReceiverListener;
            return this;
        }

        public MqttParamOptions build() {
            return new MqttParamOptions(this);
        }
    }
}
