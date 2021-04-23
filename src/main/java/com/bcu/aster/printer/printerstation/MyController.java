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
import java.io.OutputStream;
import java.util.*;

@RestController
@RequestMapping("/")
public class MyController {

    private CommPort commPort = null;

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

    @GetMapping("/connection")
    public Message openDoor(int baudrate,String portName) {
        // 通过端口名识别端口
        Message message;
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            // 打开端口，并给端口名字和一个timeout（打开操作的超时时间）
            commPort = portIdentifier.open(portName, 2000);
            SerialPort serialPort = (SerialPort) commPort;
            serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            message=Message.success("200");
        }catch (NoSuchPortException noSuchPortException){
            Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
            List<String> portNameList = new ArrayList<>();
            //将可用串口名添加到List并返回该List
            while (portList.hasMoreElements()) {
                String portNamea = portList.nextElement().getName();
                portNameList.add(portNamea);
            }
            message=Message.fail("没有此串口");
            Map<String,Object> m=new HashMap();
            m.put("COM_LIST",portNameList);
            m.put("error_Info",noSuchPortException);
            message.setObj(m);
        }catch (PortInUseException exception){
            message=Message.fail("串口被占用");
            message.setObj(exception);
        }catch (UnsupportedCommOperationException unsupportedCommOperationException){
            message=Message.fail("通信操作不支持");
            message.setObj(unsupportedCommOperationException);
        }
        return message;
    }

    @GetMapping("/send")
    public Message sendToPort(Integer data) {
        Message message = null;
        if(commPort!=null){
            OutputStream out =null;
            try {
                out = commPort.getOutputStream();
                out.write(data);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
                message=Message.fail("IO异常");
                message.setObj(e.getMessage());
            } finally {
                try {
                    if (out != null) {
                        out.close();
                        out = null;
                        message=Message.success("成功发送信息");
                        message.setObj(data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    e.printStackTrace();
                    message=Message.fail("IO异常2");
                    message.setObj(e.getMessage());
                }
            }
        }
        return message;
    }

    @GetMapping("/close")
    public Message closeCOM(){
        String name="";
        if(commPort!=null) {
            name = commPort.getName();
            commPort.close();
            commPort=null;
        }
        return Message.success("已关闭串口"+name);
    }



}
