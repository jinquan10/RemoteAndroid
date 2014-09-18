package com.miw.remoid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HttpController {
    
    @RequestMapping(value = "/echo", method = RequestMethod.GET, produces = "application/json")
    public Object echo(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "jz");
        
        return new ResponseEntity<Map<String, String>>(map, HttpStatus.OK);
    }
}
