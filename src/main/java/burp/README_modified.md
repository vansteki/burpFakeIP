package burp;import javax.swing.*;import java.awt.event.ActionEvent;impo - Pastebin.com
https://pastebin.com/V6ffxg7F

```
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) {
        if (messageIsRequest) {
            IRequestInfo requestInfo = helpers.analyzeRequest(message.getMessageInfo());
            List<String> headers = requestInfo.getHeaders();
//            headers.add("X-Forwarded-For: 1.1.1.1");
            headers.add(String.format("%s: %s", Config.AUTOXFF_KEY, Utils.getLocalRandomIp()));
 
            byte[] newRequest = helpers.buildHttpMessage(headers, null);
            message.getMessageInfo().setRequest(newRequest);
 
        }
    }
```