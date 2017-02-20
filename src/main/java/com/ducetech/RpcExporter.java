package com.ducetech;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * RPC服务发布者
 * 1. 作为服务端，监听客户端TCP连接，接收到新的客户端连接后，将其封装成Task,由线程池执行
 * 2. 将客户端发送的码流反序列化成对象，反射调用服务实现者，获取执行结果
 * 3. 将执行结果对象反序列化，通过Socket发送给客户端
 * 4. 远程服务调用完成后，释放Socket等连接资源，防止句柄泄露
 */
public class RpcExporter {

    static Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static void exporter(String hostName, int port) throws IOException {
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(hostName, port));
        try {
            while (true) {
                executor.execute(new ExportTask(server.accept()));
            }
        } finally {
            server.close();
        }
    }

    private static class ExportTask implements Runnable {

        Socket client = null;
        public ExportTask(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            ObjectInputStream input = null;
            ObjectOutputStream output = null;
            try {
                input = new ObjectInputStream(client.getInputStream());
                String interfaceName = input.readUTF();
                /*String[] strSplit = interfaceName.split("\\.");
                int len = strSplit.length;
                String serviceName = strSplit[len - 1] + "Impl";
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < len -1; i++) {
                    buffer.append(strSplit[i] + ".");
                }
                buffer.append("impl" + "." + serviceName);*/
                Class<?> service = Class.forName(interfaceName);
                String methodName = input.readUTF();
                Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
                Object[] arguments = (Object[]) input.readObject();
                Method method = service.getMethod(methodName, parameterTypes);
                Object result = method.invoke(service.newInstance(), arguments);
                output = new ObjectOutputStream(client.getOutputStream());
                output.writeObject(result);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (null != input) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (null != client) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
