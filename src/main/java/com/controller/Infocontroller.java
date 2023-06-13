package com.controller;

import Utils.EsUtil;
import Utils.HBaseUtil;
import Utils.HdfsUtil;
import com.Dao.Flag;
import com.Dao.Info;
import com.Dao.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
public class Infocontroller {
    static final Logger logger = LoggerFactory.getLogger(Infocontroller.class);

    //post请求
    //@RequestBody 表示接收请求是JSON格式的数据
    @PostMapping(value = "/ESsearch", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Info[] SearchFlag(@RequestParam String flag, HttpServletRequest request){
        //先写用户搜索日志
        writetolog("Search"+flag,request);
        Info[] map = new Info[0];
        //int count;
        try {
            map = EsUtil.search(flag,"article");
            // count = map.length;
        } catch (Exception e) {
            logger.error("查询索引错误!{}",e);
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 查看文章详细信息
     * @return
     */
    @RequestMapping(value = "/HBsearch", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Result detailArticleById(@RequestParam String id,HttpServletRequest request){
        Result result = new Result();
        try{
            Map<String, String> map = HBaseUtil.getFromHBase("article", id);
            result.setTitle(map.get("title"));
            result.setAuthor(map.get("author"));
            result.setContent(map.get("content"));
//            result.setTitle("test");
//            result.setAuthor("Zhai");
//            result.setContent("不久前，一名中年男子和一名年轻女子身着粉色“情侣装”，在成都太古里牵手逛街的视频在网络上疯传。最开始，这只是驻扎在太古里的摄影师常年“创作”的街拍作品之一，后来，网友扒出照片上两位主人公均供职于中石油，为上下级，并且二人还是婚外情关系。");
            //写入日志
            writetolog("Search"+map.get("title"),request);
        }catch (Exception e){
            logger.error("HBase数据查询异常："+e.getMessage());
        }
        System.out.println(result);
        return result;
    }

    public void writetolog(String action, HttpServletRequest request) {
        //先写用户搜索日志
        Flag writertolog = new Flag();
        // 获取系统时间
        LocalDateTime datetime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDatetime = datetime.format(formatter);
        // 获取客户端ip地址
        String clientIp = request.getRemoteAddr();
        writertolog.setDate(formattedDatetime);
        writertolog.setIp(clientIp);
        writertolog.setInformation(action);
        try {
            HdfsUtil hdfsUtil = new HdfsUtil();
            hdfsUtil.init();
            System.out.println(writertolog);
            hdfsUtil.writeFile("/WebLog/searchlog.txt",writertolog.toString());
        } catch (IOException e) {
            System.out.println("日志写入hdfs发生异常：" + e.getMessage());
        }
    }
}
