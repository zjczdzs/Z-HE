package com.controller;

import Utils.SafeTextFileWriter;
import com.Dao.Flag;
import com.service.InfoService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class Infocontroller {
    private InfoService infoService;

//    @RequestMapping("test")
//    public Flag Testmapping() {
//        Flag flag = new Flag();
//        flag.setInformation("goodgood");
//        return flag;
//    }

    //post请求
    //@RequestBody 表示接收请求是JSON格式的数据
    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Flag SearchFlag(@RequestParam String flag, HttpServletRequest request){
        Flag writertolog = new Flag();
//        System.out.println(flag);
        // 获取系统时间
        LocalDateTime datetime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDatetime = datetime.format(formatter);
        // 获取客户端ip地址
        String clientIp = request.getRemoteAddr();

        writertolog.setDate(formattedDatetime);
        writertolog.setIp(clientIp);
        writertolog.setInformation(flag);

        try {
            SafeTextFileWriter writer = new SafeTextFileWriter("D:\\javahomework\\Z-HE\\src\\main\\java\\com\\log\\searchlog");
            writer.write(writertolog +"\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("发生异常：" + e.getMessage());
        }

        return writertolog;
    }

}
