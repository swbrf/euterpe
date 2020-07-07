package euterpe.tts.xfyun;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import okhttp3.*;
import okio.ByteString;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ryan.wang
 */
public class XfYunTtsSocketListener extends WebSocketListener {

    private static final String appid = "5ef22fad";

    private String textContent;

    public static final Gson json = new Gson();

    public XfYunTtsSocketListener(String textContent) {
        this.textContent = textContent;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        try {
            System.out.println(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(222222);
        webSocket.send(getReqContent(textContent));
    }

    public String getReqContent(String content) {
        //发送数据
        JsonObject frame = new JsonObject();
        JsonObject business = new JsonObject();
        JsonObject common = new JsonObject();
        JsonObject data = new JsonObject();
        // 填充common
        common.addProperty("app_id", appid);
        //填充business
        business.addProperty("aue", "lame");
        business.addProperty("sfl", 1);
        business.addProperty("tte", "UTF8");
        business.addProperty("vcn", "x2_yezi");
        business.addProperty("pitch", 50);
        //填充data
        data.addProperty("status", 2);
        data.addProperty("text", Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8)));
        data.addProperty("encoding", "");
        //填充frame
        frame.add("common", common);
        frame.add("business", business);
        frame.add("data", data);
        return frame.toString();
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        //处理返回数据
        System.out.println("receive=>" + text);
        ResponseData resp = null;
        try {
            resp = json.fromJson(text, ResponseData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 存放音频的文件
        File f = new File("audio.mp3");
        FileOutputStream os = null;
        if (!f.exists()) {
            try {
                f.createNewFile();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            os = new FileOutputStream(f, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (resp != null) {
            if (resp.getCode() != 0) {
                System.out.println("error=>" + resp.getMessage() + " sid=" + resp.getSid());
                return;
            }
            if (resp.getData() != null) {
                String result = resp.getData().audio;
                byte[] audio = Base64.getDecoder().decode(result);
                try {
                    os.write(audio);
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (resp.getData().status == 2) {
                    // todo  resp.data.status ==2 说明数据全部返回完毕，可以关闭连接，释放资源
                    System.out.println("session end ");
                    webSocket.close(1000, "");
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
        System.out.println("socket closing");
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        System.out.println("socket closed");
    }

    @SneakyThrows
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        t.printStackTrace();
        System.out.println(response.code());
        System.out.println(new String(response.body().bytes()));
        System.out.println("connection failed");
    }

    public static class ResponseData {
        private int code;
        private String message;
        private String sid;
        private Data data;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return this.message;
        }

        public String getSid() {
            return sid;
        }

        public Data getData() {
            return data;
        }
    }

    public static class Data {
        private int status;  //标志音频是否返回结束  status=1，表示后续还有音频返回，status=2表示所有的音频已经返回
        private String audio;  //返回的音频，base64 编码
        private String ced;  // 合成进度
        private String spell;  //音频的拼音标注
    }
}
