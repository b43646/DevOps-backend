package DevOps.web;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.util.JSONPObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import DevOps.domain.CrumbIssuer;
/**
 * Created by Administrator on 2017/8/8.
 */
@RestController
@RequestMapping("/jenkins")
public class Jenkins {


    @Value("${jenkins.cred}")
    private String cred;

    @Value("${jenkins.base_url}")
    private String base_url;

    private HttpHeaders getHeader(){

        String base64Cred = Base64Utils.encodeToString(cred.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Cred);

        return headers;
    }

    private CrumbIssuer GetScrumb(){
        RestTemplate rest = new RestTemplate();

        HttpEntity<String> request = new HttpEntity<String>(getHeader());
        ResponseEntity<String> res =  rest.exchange(base_url + "/crumbIssuer/api/json",
                HttpMethod.GET, request, String.class);

        CrumbIssuer crumbIssuer = JSON.parseObject(res.getBody().toString(), CrumbIssuer.class);

        return crumbIssuer;

    }

    @RequestMapping(value = "/jobList", method = RequestMethod.GET)
    @ApiOperation(value="Job列表", notes="获取Jenkins中Job列表")
    public @ResponseBody String jobList(){

        RestTemplate rest = new RestTemplate();

        HttpEntity<String> request = new HttpEntity<String>(getHeader());
        ResponseEntity<String> res =  rest.exchange(base_url + "/api/json?tree=jobs[name]",
                HttpMethod.GET, request, String.class);
        return res.getBody().toString();

    }

    @RequestMapping(value = "/jobName", method = RequestMethod.GET)
    @ApiOperation(value = "获取job详细信息", notes = "获取job详细信息")
    public @ResponseBody String GetJob(String jobName){

        RestTemplate rest = new RestTemplate();

        HttpEntity<String> request = new HttpEntity<String>(getHeader());
        ResponseEntity<String> res =  rest.exchange(base_url + "/job/" + jobName + "/api/json",
                HttpMethod.GET, request, String.class);
        return res.getBody().toString();

    }

    @RequestMapping(value = "/jobName", method = RequestMethod.POST)
    @ApiOperation(value = "执行job", notes = "执行job")
    public @ResponseBody
    HttpStatus runJob(@RequestBody String jobName){

        RestTemplate rest = new RestTemplate();

        HttpHeaders httpHeaders = getHeader();
        CrumbIssuer crumbIssuer = GetScrumb();
        httpHeaders.add(crumbIssuer.getCrumbRequestField(), crumbIssuer.getCrumb());

        HttpEntity<String> request = new HttpEntity<String>(httpHeaders);
        ResponseEntity<String> res =  rest.exchange(base_url + "/job/" + jobName + "/build",
                HttpMethod.POST, request, String.class);

        return res.getStatusCode();
    }
}
