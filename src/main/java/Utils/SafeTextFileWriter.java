package Utils;

import java.io.*;
import java.nio.channels.*;

public class SafeTextFileWriter {
    private RandomAccessFile file;
    private FileChannel channel;
    private FileLock lock;

    public SafeTextFileWriter(String filename) throws IOException {
        // 打开文件并创建 FileChannel 对象
        this.file = new RandomAccessFile(filename, "rw");
        this.channel = file.getChannel();

        // 获取文件锁
        this.lock = channel.lock();
    }

    public void write(String text) throws IOException {
        // 向文件中写入数据
        file.writeBytes(text);
    }

    public void close() throws IOException {
        // 释放文件锁并关闭文件
        lock.release();
        channel.close();
        file.close();
    }
}