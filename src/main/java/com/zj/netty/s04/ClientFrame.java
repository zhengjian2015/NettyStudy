package com.zj.netty.s04;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 版本04 显示客户端返回的信息
 */
public class ClientFrame extends Frame {

    public static final ClientFrame INSTANCE = new ClientFrame();
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
                //ta.setText(ta.getText() + tf.getText());
                tf.setText("");
            }
        });
    }

    private void connectToServer() {
        c = new Client();
        c.connect();
    }

    public static void main(String[] args) {
        ClientFrame frame = ClientFrame.INSTANCE;
        frame.setVisible(true);
        frame.connectToServer();
    }

    public void updateText(String msgAccepted) {
        this.ta.setText(ta.getText() + System.getProperty("line.separator") + msgAccepted);
    }
}
