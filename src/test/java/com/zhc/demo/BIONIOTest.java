package com.zhc.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * BIO、NIO、AIO使用案例
 * @author zhouhengchao
 * @since 2023-08-29 09:35:00
 */
@Slf4j
public class BIONIOTest {

    /**
     * BIO使用案例：启动一个服务端，监听8080端口，
     * 获取socket客户端发送的数据
     */
    @Test
    void testBIO01(){
        Socket socket = null;
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(8080));
            while(true){
                log.info("testBIO01：等待连接");
                socket = serverSocket.accept();
                log.info("testBIO01：连接成功");
                Socket finalSocket = socket;
//                new Thread(() -> {
                    byte[] bytes=new byte[1024];
                    try {
                        log.info("testBIO01：等待读取数据");
                        // 阻塞等待读取数据
                        int length= finalSocket.getInputStream().read(bytes);
                        log.info(new String(bytes,0,length));
                        log.info("testBIO01：数据读取成功");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                });
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * BIO使用案例，建立socket客户端，并发送请求往端口8080
     */
    @Test
    void testBIO02(){
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1",8080);
            socket.getOutputStream().write("一条数据".getBytes());
            socket.close();
            log.info("数据发送结束");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * NIO服务端使用案例
     */
    @Test
    void testNIOServer(){
        try {
            //创建一个socket通道，并且设置为非阻塞的方式
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(9000));
            //创建一个selector选择器，把channel注册到selector选择器上
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true){
                System.out.println("等待事件发生");
                selector.select();
                System.out.println("有事件发生了");
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    handle(key);
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    private void handle(SelectionKey key) throws IOException {
        if (key.isAcceptable()){
            System.out.println("连接事件发生");
            ServerSocketChannel serverSocketChannel= (ServerSocketChannel) key.channel();
            //创建客户端一侧的channel，并注册到selector上
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(key.selector(),SelectionKey.OP_READ);
        }else if (key.isReadable()){
            System.out.println("数据可读事件发生");
            SocketChannel socketChannel= (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int len = socketChannel.read(buffer);
            if (len!=-1){
                System.out.println("读取到客户端发送的数据："+new String(buffer.array(),0,len));
            }
            //给客户端发送信息
            ByteBuffer wrap = ByteBuffer.wrap("hello world".getBytes());
            socketChannel.write(wrap);
            key.interestOps(SelectionKey.OP_READ|SelectionKey.OP_WRITE);
            socketChannel.close();
        }
    }

    @Test
    void testNIOClient(){
        try {
            //配置基本的连接参数
            SocketChannel channel=SocketChannel.open();
            channel.configureBlocking(false);
            Selector selector = Selector.open();
            channel.connect(new InetSocketAddress("127.0.0.1",9000));
            channel.register(selector, SelectionKey.OP_CONNECT);
            //轮询访问selector
            while(true){
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    //连接事件发生
                    if (key.isConnectable()){
                        SocketChannel socketChannel= (SocketChannel) key.channel();
                        //如果正在连接，则完成连接
                        if (socketChannel.isConnectionPending()){
                            socketChannel.finishConnect();
                        }
                        socketChannel.configureBlocking(false);
                        ByteBuffer buffer = ByteBuffer.wrap("客户端发送的数据".getBytes());
                        socketChannel.write(buffer);
                        socketChannel.register(selector,SelectionKey.OP_READ);
                    }else if (key.isReadable()){
                        //读取服务端发送过来的消息
                        read(key);
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private static void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel= (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(512);
        int len=socketChannel.read(buffer);
        if (len!=-1){
            System.out.println("客户端收到信息："+new String(buffer.array(),0,len));
        }
    }

    @Test
    void testAIOServer(){
        try {
            //创建异步通道
            AsynchronousServerSocketChannel serverSocketChannel=AsynchronousServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(8080));
            System.out.println("等待连接中");
            //在AIO中，accept有两个参数，
            // 第一个参数是一个泛型，可以用来控制想传递的对象
            // 第二个参数CompletionHandler，用来处理监听成功和失败的逻辑
            //  如此设置监听的原因是因为这里的监听是一个类似于递归的操作，每次监听成功后要开启下一个监听
            serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                //请求成功处理逻辑
                @Override
                public void completed(AsynchronousSocketChannel result, Object attachment) {
                    System.out.println("连接成功，处理数据中");
                    //开启新的监听
                    serverSocketChannel.accept(null,this);
                    handledata(result);
                }
                @Override
                public void failed(Throwable exc, Object attachment) {
                    System.out.println("失败");
                }
            });
            try {
                TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handledata(AsynchronousSocketChannel result) {
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        //通道的read方法也带有三个参数
        //1.目的地：处理客户端传递数据的中转缓存，可以不使用
        //2.处理客户端传递数据的对象
        //3.处理逻辑，也有成功和不成功的两个写法
        result.read(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (result>0){
                    attachment.flip();
                    byte[] array = attachment.array();
                    System.out.println(new String(array));
                }
            }
            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                System.out.println("失败");
            }
        });
    }

    @Test
    void testAIOClient(){
        try {
            AsynchronousSocketChannel socketChannel=AsynchronousSocketChannel.open();
            socketChannel.connect(new InetSocketAddress("127.0.0.1",8080));
            Scanner scanner=new Scanner(System.in);
            String next = scanner.next();
            ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
            byteBuffer.put(next.getBytes());
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
