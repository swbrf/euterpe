package euterpe.tts.xfyun;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ryan.wang
 */
public class XfYunWebTTS {

    private static final String apiKey = "83f3b6e3ff16b83699378e4181926db8";

    private static final String apiSecret = "b724498959d142f2fe2eaf679db9fe49";

    private static final String XFYUN_TTS_API_HOST = "tts-api.xfyun.cn";

    private static final String XFYUN_TTS_API_PATH_V2 = "/v2/tts";

    public static final String HMACSHA256 = "hmacsha256";

    public static final String VERIFY_HEADERS = "host date request-line";

    public static final DateTimeFormatter VERIFY_DATE_FORMAT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

    public static final String textContent = "高山上气候寒冷，若雪层表面没有融化或者融化很微弱，雪层中就缺少液态水，雪颗粒之间就像沙子一样疏松。当雪层越来越厚，坡度较大时雪就会流动。如果有一处雪层流动，周围的雪层也会被带着一起流动，进而发生雪层坍塌，向下坡方向快速奔流，这就是我们所说的雪崩。可以说，发生雪崩的条件是坡度、疏松雪层和诱发力。\n" +
            "\n" +
            "坡度越大、雪层越疏松，就越容易发生雪崩。雪层厚度不断增加使得疏松雪层上面承受的压力增加，这是一种诱发力。但是，当疏松雪层所承受的压力达到即将触发雪崩而又不再增加时，外界的微小力量就可诱发雪崩暴发，如人畜禽兽的踩踏、岩石崩落、声响震动等。在雪崩临界情况下，人大声说话或咳嗽都会诱发灾难。由此可以知道，易发生大雪崩的地方往往是山坡比较陡、雪层比较厚的地方，但太陡的山坡因为积存不住雪，经常是基岩裸露。\n" +
            "\n" +
            "雪崩是高寒山区常见的一种冰雪灾害，会对在雪崩区活动的人和动物带来伤害。但是，由于雪崩与地形关系密切，一般某个山区有无雪崩发生，可以根据地形条件和降雪情况大致判断出来，以便采取预防和应对措施，避免损失。"

            ;

    public void test() {
        String dateStr = getVerifyDateStr();
        String signatureOrigin = getSignatureOrigin(XFYUN_TTS_API_HOST, XFYUN_TTS_API_PATH_V2, dateStr);
        String signature = getSignature(apiSecret, signatureOrigin, StandardCharsets.UTF_8);
        String authorization = getAuthorization(apiKey, signature, StandardCharsets.UTF_8);
        String authUrl = getV2AuthUrl(XFYUN_TTS_API_HOST, XFYUN_TTS_API_PATH_V2, dateStr, authorization);
        Request request = new Request.Builder().url(authUrl).build();
        System.out.println("11111111");
        List<String> contentList = getContentList();
        contentList.forEach(t -> {
            generate(request, t);
        });
    }

    public String getVerifyDateStr() {
        return ZonedDateTime.now(ZoneId.of("GMT")).format(VERIFY_DATE_FORMAT);
    }

    public String getSignatureOrigin(String host, String requestPath, String dateStr) {
        return new StringBuilder("host: ").append(host).append("\n").
                append("date: ").append(dateStr).append("\n").
                append("GET ").append(requestPath).append(" HTTP/1.1").toString();
    }

    public String getSignature(String apiSecret, String signatureOrigin, Charset charset) {
        try {
            Mac mac = Mac.getInstance(HMACSHA256);
            SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(charset), HMACSHA256);
            mac.init(spec);
            byte[] signatureShaBytes = mac.doFinal(signatureOrigin.getBytes(charset));
            return Base64.getEncoder().encodeToString(signatureShaBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getAuthorization(String apiKey, String signature, Charset charset) {
        String origin = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"",
                apiKey, "hmac-sha256", VERIFY_HEADERS, signature);
        return Base64.getEncoder().encodeToString(origin.getBytes(charset));
    }

    public String getV2AuthUrl(String host, String path, String dateStr, String authorization) {
        return new StringBuilder("wss://").append(host).append(path).append("?authorization=").append(authorization)
                .append("&date=").append(dateStr).append("&host=").append(host).toString();
    }

    public List<String> getContentList() {
        List<String> textList = Arrays.asList(textContent.replaceAll(" ", "").split("\n"));
        return textList.stream().filter(t -> t.length() > 0).collect(Collectors.toList());
    }

    public void generate(Request request, String content) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        WebSocket webSocket = client.newWebSocket(request, new XfYunTtsSocketListener(content));
        System.out.println(content);
        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
