package com.zj.netty.s03;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 版本03  连接时初始化channel 封装send方法
 */
public class ClientFrame extends Frame {


    TextArea ta = new TextArea();
    TextField tf = new TextField();

    Client c = new Client();

    public ClientFrame(){
        this.setSize(1000,800);
        this.setLocation(100,20);
        ta.setFont(new Font("SansSerif", Font.BOLD, 44));
        tf.setFont(new Font("SansSerif", Font.BOLD, 40));
        this.add(ta,BorderLayout.CENTER);
        this.add(tf,BorderLayout.SOUTH);
        //回车的按键监听事件
        tf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c.send(tf.getText());
                //把字符串发送到服务器
                ta.setText(ta.getText() + tf.getText());
                tf.setText("");
            }
        });

        this.setVisible(true);
        //窗口显示完毕后调用 如果写在actionPerformed是敲一下就连一次 没有必要
        //只要连一次,保持连接状态就好
        connectToServer();
    }

    private void connectToServer() {
        c = new Client();
        c.connect();
    }

    public static void main(String[] args) {
        new ClientFrame();
    }
}
