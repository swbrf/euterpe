package euterpe.tts.xfyun;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class XfYunWebTTSTest {

    @Test
    public void getVerifyDateStr() {
        XfYunWebTTS webTTS = new XfYunWebTTS();
        System.out.println(webTTS.getVerifyDateStr());
    }

    @Test
    public void getSignatureOrigin() {
        XfYunWebTTS webTTS = new XfYunWebTTS();
        String dateStr = webTTS.getVerifyDateStr();
        String signatureOrigin = webTTS.getSignatureOrigin("tts-api.xfyun.cn", "/v2/tts", dateStr);
        System.out.println(signatureOrigin);
    }

    @Test
    public void getSignature() {
        XfYunWebTTS webTTS = new XfYunWebTTS();
        String dateStr = webTTS.getVerifyDateStr();
        String signatureOrigin = webTTS.getSignatureOrigin("tts-api.xfyun.cn", "/v2/tts", dateStr);
        String signature = webTTS.getSignature("b724498959d142f2fe2eaf679db9fe49", signatureOrigin, StandardCharsets.UTF_8);
        System.out.println(signature);
    }

    @Test
    public void getAuthorizationOrigin() {
        XfYunWebTTS webTTS = new XfYunWebTTS();
        String dateStr = webTTS.getVerifyDateStr();
        String signatureOrigin = webTTS.getSignatureOrigin("tts-api.xfyun.cn", "/v2/tts", dateStr);
        String signature = webTTS.getSignature("b724498959d142f2fe2eaf679db9fe49", signatureOrigin, StandardCharsets.UTF_8);
        String authorization = webTTS.getAuthorization("83f3b6e3ff16b83699378e4181926db8", signature, StandardCharsets.UTF_8);
        System.out.println(authorization);
    }

    @Test
    public void getV2AuthUrl() {
        XfYunWebTTS webTTS = new XfYunWebTTS();
        String dateStr = webTTS.getVerifyDateStr();
        String signatureOrigin = webTTS.getSignatureOrigin("tts-api.xfyun.cn", "/v2/tts", dateStr);
        String signature = webTTS.getSignature("b724498959d142f2fe2eaf679db9fe49", signatureOrigin, StandardCharsets.UTF_8);
        String authorization = webTTS.getAuthorization("83f3b6e3ff16b83699378e4181926db8", signature, StandardCharsets.UTF_8);
        String authUrl = webTTS.getV2AuthUrl("tts-api.xfyun.cn", "/v2/tts", dateStr, authorization);
        System.out.println(authUrl);
    }

    @Test
    public void test() throws InterruptedException {
        XfYunWebTTS webTTS = new XfYunWebTTS();
        webTTS.test();
        Thread.sleep(1000 * 60 * 60 * 24);
    }
}