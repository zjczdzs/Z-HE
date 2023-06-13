package DateSet;

import Utils.HBaseUtil;
import Utils.RedisUtil;
import org.apache.log4j.BasicConfigurator;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 从本地导入文章数据，入库HBase和Redis（Rowkey）
 *
 * 注意：HBase建表语句 create 'article','info'
 * Created by xuwei
 */
public class DataImport {

    private final static Logger logger = LoggerFactory.getLogger(DataImport.class);

    public static void main(String[] args) throws FileNotFoundException {
        // 从本地文件读取文章数据
        String filePath = "D:\\Hadoop\\es\\总数据\\总数据.xlsx";
        try {
            Workbook workbook = WorkbookFactory.create(new File(filePath));
            Sheet sheet = workbook.getSheetAt(0);
//            System.out.println(sheet);

            // 解析表格数据
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                String id = getStringCellValue(row.getCell(0));
                String title = getStringCellValue(row.getCell(1));
                String author = getStringCellValue(row.getCell(2));
                String describe = getStringCellValue(row.getCell(3));
                String content = getStringCellValue(row.getCell(4));

                Jedis jedis = null;
                try {
                    // 将数据入库到HBase
                    String tableName = "article";
                    String cf = "info";
//                    HBaseUtil.put2HBaseCell(tableName, id, cf, "id", id);
                    HBaseUtil.put2HBaseCell(tableName, id, cf, "title", title);
                    HBaseUtil.put2HBaseCell(tableName, id, cf, "author", author);
                    HBaseUtil.put2HBaseCell(tableName, id, cf, "describe", describe);
                    HBaseUtil.put2HBaseCell(tableName, id, cf, "content", content);

                    // 将Rowkey保存到Redis中
                    jedis = RedisUtil.getJedis();
                    jedis.lpush("l_article_ids", id);
                } catch (Exception e) {
                    // 注意：由于hbase的put操作属于幂等操作，多次操作，对最终的结果没有影响，所以不需要额外处理
                    BasicConfigurator.configure();
                    logger.error("数据添加失败：" + e.getMessage());
                } finally {
                    // 向连接池返回连接
                    if (jedis != null) {
                        RedisUtil.returnResource(jedis);
                    }
                }
            }

            workbook.close();
        } catch (IOException e) {
            BasicConfigurator.configure();
            logger.error("读取文件失败：" + e.getMessage());
        }
    }

    private static String getStringCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
}
