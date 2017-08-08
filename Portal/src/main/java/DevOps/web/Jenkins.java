package DevOps.web;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


/**
 * Created by Administrator on 2017/8/8.
 */
@RestController
@RequestMapping("/jenkins")
public class Jenkins {

    private HttpHeaders getHeader(){
        String cred = "admin:admin";
        String base64Cred = Base64Utils.encodeToString(cred.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Cred);

        return headers;
    }

    @RequestMapping(value = "/jobList", method = RequestMethod.GET)
    @ApiOperation(value="Job列表", notes="获取Jenkins中Job列表")
    public @ResponseBody String jobList(){

        RestTemplate rest = new RestTemplate();

        HttpEntity<String> request = new HttpEntity<String>(getHeader());
        ResponseEntity<String> res =  rest.exchange("http://localhost:8081/job/demo/3/wfapi/describe",
                HttpMethod.GET, request, String.class);
        return res.getBody().toString();

    }

    @RequestMapping(value = "/jobName", method = RequestMethod.POST)
    @ApiOperation(value = "执行job", notes = "执行指定名称的job")
    public @ResponseBody String runJob(String jobName){

//        RestTemplate rest = new RestTemplate();
//
//        HttpEntity<String> request = new HttpEntity<String>(getHeader());
//        ResponseEntity<String> res =  rest.exchange("http://localhost:8081/job/demo/",
//                HttpMethod.POST, request, String.class);
//        return res.getBody().toString();

        return jobName;
    }
}
