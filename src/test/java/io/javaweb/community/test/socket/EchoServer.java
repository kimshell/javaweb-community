package io.javaweb.community.test.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Kevin on 2018/2/21 12:03.
 */
public class EchoServer {

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    public EchoServer(int port)throws Exception{
        this.selector = Selector.open();
        this.serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress("0.0.0.0",port));
    }

    public void service()throws IOException{
        this.serverSocketChannel.register(this.selector,SelectionKey.OP_ACCEPT);
        //轮询 selector 是否有就绪事件
        while (this.selector.select() > 0){
            //获取就绪事件 Set
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            //获取 Set 迭代器
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            //迭代就绪事件
            while (iterator.hasNext()){
                SelectionKey selectionKey = null;
                try {
                    selectionKey = iterator.next();
                    iterator.remove();
                    /**  连接就绪 **/
                    if(selectionKey.isAcceptable()){
                        //获取服务端Channel
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                        //获取客户端Channel
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        //设置客户端为非阻塞模式
                        socketChannel.configureBlocking(false);
                        //创建 Buffer 缓冲区
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        //注册客户端事件
                        socketChannel.register(this.selector,SelectionKey.OP_READ | SelectionKey.OP_WRITE,byteBuffer);
                    }
                    /**  读就绪 **/
                    if(selectionKey.isReadable()){
                        //获取客户端 channel
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        //获取附件 Buffer
                        ByteBuffer byteBuffer = (ByteBuffer) selectionKey.attachment();

                        //创建读取数据的 Buffer
                        ByteBuffer readBuffer = ByteBuffer.allocate(32);
                        //把客户端可读数据写入 readB
                        socketChannel.read(readBuffer);
                        //复位,进入写模式
                        readBuffer.flip();

                        byteBuffer.limit(readBuffer.capacity());
                        byteBuffer.put(readBuffer);
                    }
                    /**  写就绪 **/
                    if(selectionKey.isWritable()){
                        ByteBuffer byteBuffer = (ByteBuffer) selectionKey.attachment();
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        byteBuffer.flip();

                        String data = StandardCharsets.UTF_8.decode(byteBuffer).toString();
                        if(data.indexOf("\r\n") == -1){
                            return;
                        }
                        String outputData = data.substring(0,data.indexOf("\n") + 1);
                        ByteBuffer outputBuffer = StandardCharsets.UTF_8.encode(outputData);
                        while (outputBuffer.hasRemaining()){
                            socketChannel.write(outputBuffer);
                        }
                        ByteBuffer temp = StandardCharsets.UTF_8.encode(outputData);
                        byteBuffer.position(temp.limit());
                        byteBuffer.compact();
                        if(outputData.equals("bye\r\n")){
                            selectionKey.cancel();
                            socketChannel.close();
                        }
                    }
                }catch (Exception exception){
                    exception.printStackTrace();
                    try {
                        if(selectionKey != null){
                            selectionKey.cancel();
                            selectionKey.channel().close();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
