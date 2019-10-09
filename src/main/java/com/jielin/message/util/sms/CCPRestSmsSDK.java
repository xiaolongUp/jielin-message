package com.jielin.message.util.sms;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Slf4j
public class CCPRestSmsSDK {

    private static final int Request_Get = 0;


    private static final int Request_Post = 1;
    private static final String TemplateSMS = "SMS/TemplateSMS";
    private String SERVER_IP;
    private String SERVER_PORT;
    private String ACCOUNT_SID;
    private String ACCOUNT_TOKEN;
    public String App_ID;
    private BodyType BODY_TYPE = BodyType.Type_JSON;

    public enum BodyType {
        Type_XML, Type_JSON;
    }


    /**
     * 初始化服务地址和端口
     *
     * @param serverIP   必选参数 服务器地址
     * @param serverPort 必选参数 服务器端口
     */
    public void init(String serverIP, String serverPort) {
        if (isEmpty(serverIP) || isEmpty(serverPort)) {
            log.error("初始化异常:serverIP或serverPort为空");
            throw new IllegalArgumentException("必选参数:" + (isEmpty(serverIP) ? " 服务器地址 " : "") + (isEmpty(serverPort) ? " 服务器端口 " : "") + "为空");
        }
        SERVER_IP = serverIP;
        SERVER_PORT = serverPort;
    }

    /**
     * 初始化主帐号信息
     *
     * @param accountSid   必选参数 主帐号名称
     * @param accountToken 必选参数 主帐号令牌
     */
    public void setAccount(String accountSid, String accountToken) {
        if (isEmpty(accountSid) || isEmpty(accountToken)) {
            log.error("初始化异常:accountSid或accountToken为空");
            throw new IllegalArgumentException("必选参数:" + (isEmpty(accountSid) ? " 主帐号名称" : "") + (isEmpty(accountToken) ? " 主帐号令牌 " : "") + "为空");
        }
        ACCOUNT_SID = accountSid;
        ACCOUNT_TOKEN = accountToken;
    }


    /**
     * 初始化应用Id
     *
     * @param appId 必选参数 应用Id
     */
    public void setAppId(String appId) {
        if (isEmpty(appId)) {
            log.error("初始化异常:appId为空");
            throw new IllegalArgumentException("必选参数: 应用Id 为空");
        }
        App_ID = appId;
    }

    /**
     * 发送短信模板请求
     *
     * @param to         必选参数 短信接收端手机号码集合，用英文逗号分开，每批发送的手机号数量不得超过100个
     * @param templateId 必选参数 模板Id
     * @param datas      可选参数 内容数据，用于替换模板中{序号}
     * @return
     */
    public HashMap<String, Object> sendTemplateSMS(String to, String templateId, String[] datas) {
        HashMap<String, Object> validate = accountValidate();
        if (validate != null)
            return validate;
        if ((isEmpty(to)) || (isEmpty(App_ID)) || (isEmpty(templateId)))
            throw new IllegalArgumentException("必选参数:" + (isEmpty(to) ? " 手机号码 " : "") + (isEmpty(templateId) ? " 模板Id " : "") + "为空");
        CcopHttpClient chc = new CcopHttpClient();
        DefaultHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        try {
            httpclient = chc.registerSSL(SERVER_IP, "TLS", Integer.parseInt(SERVER_PORT), "https");
        } catch (Exception e1) {
            e1.printStackTrace();
            throw new RuntimeException("初始化httpclient异常" + e1.getMessage());
        }
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000 * 20);
        String result = "";
        try {
            HttpPost httppost = (HttpPost) getHttpRequestBase(1, TemplateSMS);
            httppost.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 1000 * 40);
            String requsetbody = "";
            if (BODY_TYPE == BodyType.Type_JSON) {
                JsonObject json = new JsonObject();
                json.addProperty("appId", App_ID);
                json.addProperty("to", to);
                json.addProperty("templateId", templateId);
                if (datas != null) {
                    StringBuilder sb = new StringBuilder("[");
                    for (String s : datas) {
                        sb.append("\"" + s + "\"" + ",");
                    }
                    sb.replace(sb.length() - 1, sb.length(), "]");
                    JsonParser parser = new JsonParser();
                    JsonArray Jarray = parser.parse(sb.toString()).getAsJsonArray();
                    json.add("datas", Jarray);
                }
                requsetbody = json.toString();
            } else {
                StringBuilder sb = new StringBuilder("<?xml version='1.0' encoding='utf-8'?><TemplateSMS>");
                sb.append("<appId>").append(App_ID).append("</appId>").append("<to>").append(to).append("</to>").append("<templateId>").append(templateId)
                        .append("</templateId>");
                if (datas != null) {
                    sb.append("<datas>");
                    for (String s : datas) {
                        sb.append("<data>").append(s).append("</data>");
                    }
                    sb.append("</datas>");
                }
                sb.append("</TemplateSMS>");
                requsetbody = sb.toString();
            }

            log.info("sendTemplateSMS Request body =  " + requsetbody);
            BasicHttpEntity requestBody = new BasicHttpEntity();
            requestBody.setContent(new ByteArrayInputStream(requsetbody.getBytes("UTF-8")));
            requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
            httppost.setEntity(requestBody);
            response = httpclient.execute(httppost);

            HttpEntity entity = response.getEntity();
            if (entity != null)
                result = EntityUtils.toString(entity, "UTF-8");

            EntityUtils.consume(entity);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return getMyError("172001", "网络错误");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return getMyError("172002", "无返回");
        } finally {
            if (httpclient != null)
                httpclient.getConnectionManager().shutdown();
            if (response != null)
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        log.info("sendTemplateSMS response body = " + result);

        try {
            if (BODY_TYPE == BodyType.Type_JSON) {
                return jsonToMap(result);
            } else {
                return xmlToMap(result);
            }
        } catch (Exception e) {

            return getMyError("172003", "返回包体错误");
        }
    }

    private HashMap<String, Object> jsonToMap(String result) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        JsonParser parser = new JsonParser();
        JsonObject asJsonObject = parser.parse(result).getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entrySet = asJsonObject.entrySet();
        HashMap<String, Object> hashMap2 = new HashMap<String, Object>();

        for (Map.Entry<String, JsonElement> m : entrySet) {
            if ("statusCode".equals(m.getKey()) || "statusMsg".equals(m.getKey()))
                hashMap.put(m.getKey(), m.getValue().getAsString());
            else {
                if ("SubAccount".equals(m.getKey()) || "totalCount".equals(m.getKey())
                        || "token".equals(m.getKey()) || "downUrl".equals(m.getKey())) {
                    if (!"SubAccount".equals(m.getKey()))
                        hashMap2.put(m.getKey(), m.getValue().getAsString());
                    else {
                        try {
                            if ((m.getValue().toString().trim().length() <= 2) && !m.getValue().toString().contains("[")) {
                                hashMap2.put(m.getKey(), m.getValue().getAsString());
                                hashMap.put("data", hashMap2);
                                break;
                            }
                            if (m.getValue().toString().contains("[]")) {
                                hashMap2.put(m.getKey(), new JsonArray());
                                hashMap.put("data", hashMap2);
                                continue;
                            }
                            JsonArray asJsonArray = parser.parse(m.getValue().toString()).getAsJsonArray();
                            ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
                            for (JsonElement j : asJsonArray) {
                                Set<Map.Entry<String, JsonElement>> entrySet2 = j.getAsJsonObject().entrySet();
                                HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
                                for (Map.Entry<String, JsonElement> m2 : entrySet2) {
                                    hashMap3.put(m2.getKey(), m2.getValue().getAsString());
                                }
                                arrayList.add(hashMap3);
                            }
                            hashMap2.put("SubAccount", arrayList);
                        } catch (Exception e) {
                            JsonObject asJsonObject2 = parser.parse(m.getValue().toString()).getAsJsonObject();
                            Set<Map.Entry<String, JsonElement>> entrySet2 = asJsonObject2.entrySet();
                            HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
                            for (Map.Entry<String, JsonElement> m2 : entrySet2) {
                                hashMap3.put(m2.getKey(), m2.getValue().getAsString());
                            }
                            hashMap2.put(m.getKey(), hashMap3);
                            hashMap.put("data", hashMap2);
                        }

                    }
                    hashMap.put("data", hashMap2);
                } else {

                    JsonObject asJsonObject2 = parser.parse(m.getValue().toString()).getAsJsonObject();
                    Set<Map.Entry<String, JsonElement>> entrySet2 = asJsonObject2.entrySet();
                    HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
                    for (Map.Entry<String, JsonElement> m2 : entrySet2) {
                        hashMap3.put(m2.getKey(), m2.getValue().getAsString());
                    }
                    if (hashMap3.size() != 0) {
                        hashMap2.put(m.getKey(), hashMap3);
                    } else {
                        hashMap2.put(m.getKey(), m.getValue().getAsString());
                    }
                    hashMap.put("data", hashMap2);
                }
            }
        }
        return hashMap;
    }

    /**
     * @param xml
     * @return Map
     * @description 将xml字符串转换成map
     */
    private HashMap<String, Object> xmlToMap(String xml) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            HashMap<String, Object> hashMap2 = new HashMap<String, Object>();
            for (Iterator i = rootElt.elementIterator(); i.hasNext(); ) {
                Element e = (Element) i.next();
                if ("statusCode".equals(e.getName()) || "statusMsg".equals(e.getName()))
                    map.put(e.getName(), e.getText());
                else {
                    if ("SubAccount".equals(e.getName()) || "totalCount".equals(e.getName())
                            || "token".equals(e.getName()) || "downUrl".equals(e.getName())) {
                        if (!"SubAccount".equals(e.getName())) {
                            hashMap2.put(e.getName(), e.getText());
                        } else {
                            ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
                            HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
                            for (Iterator i2 = e.elementIterator(); i2.hasNext(); ) {
                                Element e2 = (Element) i2.next();
                                hashMap3.put(e2.getName(), e2.getText());
                                arrayList.add(hashMap3);
                            }
                            hashMap2.put("SubAccount", arrayList);
                        }
                        map.put("data", hashMap2);
                    } else {

                        HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
                        for (Iterator i2 = e.elementIterator(); i2.hasNext(); ) {
                            Element e2 = (Element) i2.next();
                            // hashMap2.put(e2.getName(),e2.getText());
                            hashMap3.put(e2.getName(), e2.getText());
                        }
                        if (hashMap3.size() != 0) {
                            hashMap2.put(e.getName(), hashMap3);
                        } else {
                            hashMap2.put(e.getName(), e.getText());
                        }
                        map.put("data", hashMap2);
                    }
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return map;
    }


    private HttpRequestBase getHttpRequestBase(int get, String action) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String timestamp = DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_NO_SLASH);
        EncryptUtil eu = new EncryptUtil();
        String sig;
        String acountName;
        String acountType = "Accounts";

        acountName = ACCOUNT_SID;
        sig = ACCOUNT_SID + ACCOUNT_TOKEN + timestamp;
        String signature = eu.md5Digest(sig);

        String url = getBaseUrl().append("/" + acountType + "/").append(acountName).append("/" + action + "?sig=").append(signature).toString();
        log.info(getmethodName(action) + " url = " + url);
        HttpRequestBase mHttpRequestBase = null;
        if (get == Request_Get)
            mHttpRequestBase = new HttpGet(url);
        else if (get == Request_Post)
            mHttpRequestBase = new HttpPost(url);

        setHttpHeader(mHttpRequestBase);
        String src = acountName + ":" + timestamp;
        String auth = eu.base64Encoder(src);
        mHttpRequestBase.setHeader("Authorization", auth);
        return mHttpRequestBase;
    }


    private String getmethodName(String action) {
        if (action.equals(TemplateSMS)) {
            return "sendTemplateSMS";
        } else {
            return "";
        }
    }

    private void setHttpHeader(AbstractHttpMessage httpMessage) {
        if (BODY_TYPE == BodyType.Type_JSON) {
            httpMessage.setHeader("Accept", "application/json");
            httpMessage.setHeader("Content-Type", "application/json;charset=utf-8");
        } else {
            httpMessage.setHeader("Accept", "application/xml");
            httpMessage.setHeader("Content-Type", "application/xml;charset=utf-8");
        }
    }

    private StringBuffer getBaseUrl() {
        StringBuffer sb = new StringBuffer("https://");
        sb.append(SERVER_IP).append(":").append(SERVER_PORT);
        sb.append("/2013-12-26");
        return sb;
    }

    private boolean isEmpty(String str) {
        return (("".equals(str)) || (str == null));
    }

    private HashMap<String, Object> getMyError(String code, String msg) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("statusCode", code);
        hashMap.put("statusMsg", msg);
        return hashMap;
    }

    private HashMap<String, Object> accountValidate() {
        if ((isEmpty(SERVER_IP))) {
            return getMyError("172004", "IP为空");
        }
        if ((isEmpty(SERVER_PORT))) {
            return getMyError("172005", "端口错误");
        }
        if ((isEmpty(ACCOUNT_SID))) {
            return getMyError("172006", "主帐号为空");
        }
        if ((isEmpty(ACCOUNT_TOKEN))) {
            return getMyError("172007", "主帐号令牌为空");
        }
        if ((isEmpty(App_ID))) {
            return getMyError("172012", "应用ID为空");
        }
        return null;
    }
}
