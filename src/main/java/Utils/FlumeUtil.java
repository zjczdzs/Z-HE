package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class FlumeUtil {
    private static Socket socket;
    private static OutputStream outputStream;

    // 初始化连接
    public static void init() throws IOException {
        String flumeHost = "master"; // 主机名
        int flumePort = 55555; // 端口号
        socket = new Socket(flumeHost, flumePort);
        outputStream = socket.getOutputStream();
    }


    // 关闭连接
    public static void close() throws IOException {
        if (outputStream != null) {
            outputStream.close();
        }
        if (socket != null) {
            socket.close();
        }
    }

    // 发送日志数据到Flume
    public static void sendLogToFlume(String log) throws IOException {
        outputStream.write(log.getBytes());
        outputStream.write("\n".getBytes());
        outputStream.flush();
    }

    // 示例方法，将控制台日志发送到Flume
    public static void sendConsoleLogToFlume() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = reader.readLine()) != null) {
            sendLogToFlume(line);
        }
    }
}
