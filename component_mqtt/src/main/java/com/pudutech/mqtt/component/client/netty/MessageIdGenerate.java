package com.pudutech.mqtt.component.client.netty;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ChenS on 2020/5/14.
 * chenshichao@outlook.com
 */
public class MessageIdGenerate {

    private static volatile MessageIdGenerate instance;
    private AtomicInteger messageId;

    private MessageIdGenerate() {
        messageId = new AtomicInteger(0);
    }

    public static MessageIdGenerate getInstance() {
        if(instance == null) {
            synchronized (MessageIdGenerate.class) {
                if(instance == null) {
                    instance = new MessageIdGenerate();
                }
            }
        }

        return instance;
    }

    public int getMessageId() {
        if(messageId.get() > 65535 - 1) {
            messageId.set(0);
        }

        return messageId.incrementAndGet();
    }

    public static void main(String[] args) {
        for(int i = 0; i < 70000; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(MessageIdGenerate.getInstance().getMessageId());
                }
            }).start();
        }
    }
}
