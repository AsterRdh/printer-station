package com.bcu.aster.printer.printerstation;

import com.google.gson.Gson;
import gnu.io.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/")
public class MyController {


    @GetMapping("/getAdList")
    public Message getAd() throws IOException {
       String s = "";
        HttpGet httpGet = new HttpGet("http://localhost:8080/PrintRoom/ad/adSrc?pos=0");
        // 执行请求
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response  = httpclient.execute(httpGet);

        if (response.getStatusLine().getStatusCode() == 200) {
            s = EntityUtils.toString(response.getEntity(), "UTF-8");
        }

        Gson gson=new Gson();
        String[] o = gson.fromJson(s, String[].class);
        for(int i=0;i<o.length;i++){
            o[i]="http://localhost:8080/PrintRoom/resource/"+o[i];
        }

//
        Message<Object> success = Message.success("200");
        success.setObj(o);
        return success;
    }

    @GetMapping("/openDoor")
    public Message openDoor(int doorId,String portName) {
        // 通过端口名识别端口
        Message message;
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            // 打开端口，并给端口名字和一个timeout（打开操作的超时时间）
            CommPort commPort = portIdentifier.open(portName, 2000);
            SerialPort serialPort = (SerialPort) commPort;
            serialPort.setSerialPortParams(doorId, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        }catch (NoSuchPortException noSuchPortException){
            message=Message.fail("没有此串口");
        }catch (PortInUseException exception){
            message=Message.fail("串口被占用");
        }catch (UnsupportedCommOperationException unsupportedCommOperationException){
            message=Message.fail("通信操作不支持");
        }
        message=Message.success("200");
        return message;
    }


}
