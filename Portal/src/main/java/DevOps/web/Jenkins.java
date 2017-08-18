package DevOps.web;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import DevOps.domain.CrumbIssuer;

import java.io.*;
import java.net.URLEncoder;

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

        return JSON.parseObject(res.getBody(), CrumbIssuer.class);
    }

    @RequestMapping(value = "/jobList", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value="Job列表", notes="获取Jenkins中Job列表")
    public @ResponseBody String jobList(){

        RestTemplate rest = new RestTemplate();

        HttpEntity<String> request = new HttpEntity<String>(getHeader());
        ResponseEntity<String> res =  rest.exchange(base_url + "/api/json?tree=jobs[name]",
                HttpMethod.GET, request, String.class);
        return res.getBody();

    }

    @RequestMapping(value = "/job/{jobName}", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "获取job详细信息", notes = "获取job详细信息")
    public @ResponseBody String GetJob(@PathVariable("jobName") String jobName){

        RestTemplate rest = new RestTemplate();

        HttpEntity<String> request = new HttpEntity<String>(getHeader());
        ResponseEntity<String> res =  rest.exchange(base_url + "/job/" + jobName + "/api/json",
                HttpMethod.GET, request, String.class);
        return res.getBody();

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

    @RequestMapping(value = "/testReport/{jobName}/{path}", method = RequestMethod.GET)
    @ApiOperation(value = "获取基于RobotFrameWork框架的测试报告", notes = "获取测试报告")
    public @ResponseBody ResponseEntity<Resource> GetReport(@PathVariable("jobName") String jobName, @PathVariable("path") String path) throws UnsupportedEncodingException {
        String fileName = "report.html";
        RestTemplate rest = new RestTemplate();

        HttpEntity<String> request = new HttpEntity<String>(getHeader());
        ResponseEntity<String> res =  rest.exchange(base_url + "/job/" + jobName + "/ws/" + path +"/report.html/*view*/",
                HttpMethod.GET, request, String.class);

        HttpHeaders headers = new HttpHeaders();
        //HTTP1.1
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        fileName = URLEncoder.encode(fileName, "UTF-8");
        headers.add("Content-Disposition", "attachment;filename=" + fileName);

        Resource resource = new InputStreamResource(new ByteArrayInputStream(res.getBody().getBytes()));

        return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("application/download")).body(resource);
    }

    @RequestMapping(value = "/testLog/{jobName}/{path}", method = RequestMethod.GET)
    @ApiOperation(value = "获取基于RobotFrameWork框架的测试日志", notes = "获取测试日志")
    public @ResponseBody ResponseEntity<Resource> GetTestLog(@PathVariable("jobName") String jobName, @PathVariable("path") String path) throws UnsupportedEncodingException {

        RestTemplate rest = new RestTemplate();

        HttpEntity<String> request = new HttpEntity<String>(getHeader());
        ResponseEntity<String> res =  rest.exchange(base_url + "/job/" + jobName + "/ws/" + path +"/log.html/*view*/",
                HttpMethod.GET, request, String.class);

        String fileName = "log.html";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        fileName = URLEncoder.encode(fileName, "UTF-8");
        headers.add("Content-Disposition", "attachment;filename=" + fileName);

        Resource resource = new InputStreamResource(new ByteArrayInputStream(res.getBody().getBytes()));

        return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("application/download")).body(resource);

    }

    @RequestMapping(value = "/testResult/{jobName}/{buildNum}", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "获取基于RobotFrameWork的测试结果", notes = "获取测试结果")
    public @ResponseBody String GetTestResult(@PathVariable("jobName") String jobName, @PathVariable("buildNum") int buildNum){

        RestTemplate rest = new RestTemplate();

        HttpEntity<String> request = new HttpEntity<String>(getHeader());
        ResponseEntity<String> res =  rest.exchange(base_url + "/job/" + jobName + "/" + buildNum + "/robot/api/json?pretty=true",
                HttpMethod.GET, request, String.class);

        return res.getBody();
    }


    @RequestMapping(value = "/testPackage/{jobName}/{path}", method = RequestMethod.GET)
    @ApiOperation(value = "获取基于RobotFrameWork框架的测试日志", notes = "获取测试日志")
    public @ResponseBody ResponseEntity<Resource> GetTestPackage(@PathVariable("jobName") String jobName, @PathVariable("path") String path) throws IOException {

        String fileName = path + ".zip";
        RestTemplate rest = new RestTemplate();

        HttpEntity<byte[]> request = new HttpEntity<byte[]>(getHeader());

        ResponseEntity<byte[]> res =  rest.exchange(base_url + "/job/" + jobName + "/ws/" + path +"/*zip*/" + path + "/.zip",
                HttpMethod.GET, request, byte[].class);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        fileName = URLEncoder.encode(fileName, "UTF-8");
        headers.add("Content-Disposition", "attachment;filename=" + fileName);

        Resource resource = new InputStreamResource(new ByteArrayInputStream(res.getBody()));

        return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("application/zip")).body(resource);
    }
}
