package com.controller;

import com.Dao.Flag;
import com.service.InfoService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class Infocontroller {
    private InfoService infoService;

    @RequestMapping("test")
    public Flag Testmapping() {
        Flag flag = new Flag();
        flag.setInformation("goodgood");
        return flag;
    }

    //post请求
    //@RequestBody 表示接收请求是JSON格式的数据
    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Flag SearchFlag(@RequestParam String flag){
//        String flag = formData.getFirst("flag");
        System.out.println(flag);
        Flag flag1 = new Flag();
        flag1.setInformation(flag);
        return flag1;
    }

}
