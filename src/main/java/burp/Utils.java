package burp;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Project: fakeIP
 * Date:2021/5/21 上午11:30
 *
 * @author CoolCat
 * @version 1.0.0
 * Github:https://github.com/TheKingOfDuck
 * When I wirting my code, only God and I know what it does. After a while, only God knows.
 */
public class Utils {

    public static void addfakeip(IContextMenuInvocation iContextMenuInvocation, String ip) {

        //获取原请求信息
        IHttpRequestResponse currentRequest = iContextMenuInvocation.getSelectedMessages()[0];
        IRequestInfo requestInfo = BurpExtender.helpers.analyzeRequest(currentRequest);
        List<String> headers = requestInfo.getHeaders();

        //去除header中本身已经有的字段
        List<String> templist = Config.HEADER_LIST;
        for (String header : headers) {
            String hkey = header.split(":")[0];
            templist = templist.stream().filter(key -> !key.equals(hkey)).collect(Collectors.toList());
        }
        for (String headerkey : templist) {
            headers.add(String.format("%s: %s", headerkey, ip));
        }

        //更新header
        byte[] newMessage = BurpExtender.helpers.buildHttpMessage(headers, getHttpRequestBody(currentRequest));
        currentRequest.setRequest(newMessage);

    }


    public static void addfakeip(IHttpRequestResponse iHttpRequestResponse, String ip) {

        //获取原请求信息
        IRequestInfo requestInfo = BurpExtender.helpers.analyzeRequest(iHttpRequestResponse);
        List<String> headers = requestInfo.getHeaders();

        //为每个请求添加一个Header
        headers = headers.stream().filter(key -> !key.equals(Config.AUTOXFF_KEY)).collect(Collectors.toList());
        headers.add(String.format("%s: %s", Config.AUTOXFF_KEY, ip));

        //更新header
        byte[] newMessage = BurpExtender.helpers.buildHttpMessage(headers, getHttpRequestBody(iHttpRequestResponse));
        iHttpRequestResponse.setRequest(newMessage);


    }

    private static byte[] getHttpRequestBody(IHttpRequestResponse httpRequestResponse) {
        byte[] request = httpRequestResponse.getRequest();
        IRequestInfo requestInfo = BurpExtender.helpers.analyzeRequest(request);

        int httpBodyOffset = requestInfo.getBodyOffset();
        int httpBodyLength = request.length - httpBodyOffset;
        byte[] httpBody = new byte[httpBodyLength];
        System.arraycopy(request,httpBodyOffset,httpBody,0,httpBodyLength);
        return httpBody;
    }

    public static String getRandomIp() {

        // ip范围 ref：https://blog.csdn.net/zhengxiongwei/article/details/78486146
        int[][] range = {
                {607649792, 608174079},
                {1038614528, 1039007743},
                {1783627776, 1784676351},
                {2035023872, 2035154943},
                {2078801920, 2079064063},
                {-1950089216, -1948778497},
                {-1425539072, -1425014785},
                {-1236271104, -1235419137},
                {-770113536, -768606209},
                {-569376768, -564133889},
        };

        SecureRandom random = new SecureRandom();
        int index = random.nextInt(10);
        String ip = num2ip(range[index][0] + new SecureRandom().nextInt(range[index][1] - range[index][0]));
        return ip;
    }

    public static String getLocalRandomIp() {
        int[] rangeStart = {127, 0, 0, 1};
        int[] rangeEnd = {127, 254, 254, 254};

        Random random = new Random();
        int[] ip = new int[4];
        for (int i = 0; i < 4; i++) {
            if (i == 3) {
                ip[i] = random.nextInt(rangeEnd[i] - rangeStart[i]) + rangeStart[i] + 1;
            } else {
                ip[i] = random.nextInt(rangeEnd[i] - rangeStart[i] + 1) + rangeStart[i];
            }
        }
        return String.format("%d.%d.%d.%d", ip[0], ip[1], ip[2], ip[3]);
    }

    public static String num2ip(int ip) {
        int[] b = new int[4];
        String ipStr = "";
        b[0] = (int) ((ip >> 24) & 0xff);
        b[1] = (int) ((ip >> 16) & 0xff);
        b[2] = (int) ((ip >> 8) & 0xff);
        b[3] = (int) (ip & 0xff);
        ipStr = Integer.toString(b[0]) + "." + Integer.toString(b[1]) + "." + Integer.toString(b[2]) + "." + Integer.toString(b[3]);
        return ipStr;
    }
}
