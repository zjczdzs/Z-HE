package Utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

public class HdfsUtil {
    FileSystem fs = null;

    @Before
    // 初始化，构建FileSystem对象
    public void init() throws IOException {
        Configuration conf = new Configuration();
        // 配置HDFS URL
        conf.set("fs.defaultFS", "hdfs://master:9000");
        // 设置副本的数量
        conf.set("dfs.replication", "1");
        // 构建FileSystem对象
        fs = FileSystem.get(conf);
    }

    public void createFile(String path) throws IOException {

        // 使用create方法创建文件
        fs.create(new Path(path));


    }


    // 删除文件
    public void deleteFile(String path) throws IOException {
        // recursive 是否递归的删除
        // 如果需要删除一个目录，则置为true
        fs.delete(new Path(path), true);
    }

    // 读取文件
    public void readFile(String path) throws IOException {
        FSDataInputStream fsDataInputStream = fs.open(new Path(path));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fsDataInputStream));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
        bufferedReader.close();
        fsDataInputStream.close();
    }


    public void writeFile(String path,String log) throws IOException {
        Path test1 = new Path(path);
        FSDataOutputStream fsDataOutputStream = null;
        if (!fs.exists(test1)) {
            fsDataOutputStream = fs.create(test1);
        } else {
            fsDataOutputStream = fs.append(test1);
        }

        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fsDataOutputStream));
        bufferedWriter.write(log);
        bufferedWriter.newLine();
        bufferedWriter.close();
        fsDataOutputStream.close();

    }

    // 递归创建目录
    public void mkdirs(String path) throws IOException {
        fs.mkdirs(new Path(path));
    }

    // ls
    public void listStatus(String path) throws IOException {
        FileStatus[] fileStatuses = fs.listStatus(new Path(path));
        for (FileStatus fileStatus : fileStatuses) {
            // 判断是否为目录 是则继续遍历
            if (fileStatus.isDirectory()) {
                for (FileStatus fileStatus2 : fs.listStatus(fileStatus.getPath())) {
                    System.out.println(fileStatus2.getPath());

                }
            } else if (fileStatus.isFile()) {// 文件直接打印
                System.out.println(fileStatus.getPath());

            }
        }
    }


    // 显示文件存储位置
    public void getFileBlockLocations(String path) throws IOException {
        BlockLocation[] fileBlockLocations = fs.getFileBlockLocations(new Path(path), 0, 100);
        for (BlockLocation location : fileBlockLocations) {
            for (String host : location.getHosts()) {
                System.out.println(host);

            }
            for (String name : location.getNames()) {
                System.out.println(name);
            }
        }
    }

    @After
    public void closeConn() throws IOException {
        fs.close();
    }
}
