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

    public static final String textContent = "【雾霾天】\n" +
            "雾霾（mái）天是指空气中因悬浮着大量微粒而形成的混浊形象。" +
            "【雾霾天危害】\n" +
            "雾霾天气易诱发疾病，雾霾天气极易引发上呼吸道感染及皮肤过敏等多种疾病。包括咳嗽、打喷嚏、脸部红肿等。\n" +
            "\n" +
            "【雾霾天出行应注意什么呢】\n" +
            "1、外出戴口罩　　\n" +
            "\n" +
            "文献记载医用口罩对0.3微米的颗粒能挡住95%。选择口罩要买正规合格的，同时要戴一下，买与自己脸型大小匹配的型号，要最大程度地贴紧皮肤，让污染颗粒不能进入。口罩不能洗，取下后，要等里面干燥后再对折收起来，以免呼吸的潮气让口罩滋生细菌。老年人和有心血管疾病的人要避免佩戴，因为其为专业抗病毒气溶胶口罩，密闭性好，戴上后容易呼吸困难，缺氧而感到头昏。　　\n" +
            "\n" +
            "2、外出尽量别骑车\n" +
            "\n" +
            "汽车尾气里有很多没有完全燃烧透的化学成分，会随着空气里面细小颗粒漂浮。当你骑单车或电动车时，身体需要供氧，肺就会吸入大量空气。雾霾可以暂时减少晨练，尽量选择在10—14时外出。同时，要多喝水，少吸烟并远离“二手烟”，减轻肺、肝等器官的负担。习惯骑单车、电动车上班或出门办事的人，尽量避开早晚交通拥挤的高峰时段，尽量改换搭乘公交车。　" +
            "\n" +
            "【雾霾天个人卫生】\n" +
            "1、洗脸\n" +
            "\n" +
            "洗脸最好用温水，可以将附着在皮肤上的阴霾颗粒有效清洁干净;\n" +
            "2、漱口\n" +
            "\n" +
            "漱口的目的是清除附着在口腔的脏东西;\n" +
            "\n" +
            "3、清理鼻腔\n" +
            "\n" +
            "清理鼻腔是最关键的。清理鼻腔时，一定要轻轻吸水，避免呛咳。家长在给儿童清理鼻腔时，可以用干净棉签蘸水，反复清洗。" +
            "\n" +
            "【雾霾天常规防护】\n" +
            "1、少出门\n" +
            "\n" +
            "这种天气，减少出门是自我保护最有效的办法，尤其是有心脑血管、呼吸系统疾病的人群，更要尽量少出门。　　根据该研究，在排除了年龄、性别、时间效应和气象因素等影响因素之后，当雾浓度每增加103微克/立方米时，居民全部死因的超额死亡风险会增加2.29%，滞后时间在1—2天。心脑血管疾病增加的超额死亡风险更高，为3.08%。\n" +
            "\n" +
            " 2、尽量不要开窗　　\n" +
            "\n" +
            "在大雾天气升级的情况下尽量不要开窗;确实需要开窗透气的话，开窗时应尽量避开早晚雾霾高峰时段，可以将窗户打开一条缝通风，不让风直接吹进来，通风时间每次以半小时至一小时为宜。家中以空调取暖的居民，尤其要注意开窗透气，确保室内氧气充足。\n" +
            "\n" +
            "雾霾天气是疾病的温床，在雾霾天时应尽量减少出行，并且做好个人卫生等防护工作。";

    public void test() {
        String dateStr = getVerifyDateStr();
        String signatureOrigin = getSignatureOrigin(XFYUN_TTS_API_HOST, XFYUN_TTS_API_PATH_V2, dateStr);
        String signature = getSignature(apiSecret, signatureOrigin, StandardCharsets.UTF_8);
        String authorization = getAuthorization(apiKey, signature, StandardCharsets.UTF_8);
        String authUrl = getV2AuthUrl(XFYUN_TTS_API_HOST, XFYUN_TTS_API_PATH_V2, dateStr, authorization);
        Request request = new Request.Builder().url(authUrl).build();
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
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
