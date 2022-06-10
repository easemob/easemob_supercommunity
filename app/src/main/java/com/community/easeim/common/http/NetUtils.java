package com.community.easeim.common.http;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * 请求头工具类
 *
 * @author easeMob
 * @date 2020/6/8
 */
public class NetUtils {

//    public static Map<String,String> getHeaders(){
//
////        String myUUID = CMCommonUtil.getMyUUID2();
//        String nonce = getRandomNum(16);
//        String times = getTimes();
//        String sha256 = getSHA256(nonce +"."+ times +"."+ Constant.APP_SECRET);
//
//        Map<String,String> map = new HashMap<>();
//        map.put("nonce",nonce);
//        map.put("times",times);
//        map.put("sign",sha256);
//        LoginBean loginUserInfo = PreferenceManager.getInstance().getLoginUserInfo();
//        if (loginUserInfo == null) {
//            LogUtils.d("NetUtils", "getHeaders nonce:"+nonce+"; times:"+times+"; sha256 :"+sha256);
//            map.put("token","");
//        } else {
//            String token = loginUserInfo.getToken();
//            if (!CMCommonUtil.isNullOrNil(token)){
//                map.put("token",token);
//            } else {
//                map.put("token","");
//            }
//            LogUtils.d("NetUtils", "getHeaders nonce:"+nonce+"; times:"+times+"; sha256 :"+sha256+"; token:"+token);
//        }
//        return map;
//    }


    private static String getRandomNum(int bit) {
        String strRand = "";
        for (int i = 0; i < bit; i++) {
            strRand += String.valueOf((int) (Math.random() * 10));
        }
        return strRand;
    }

    public static String getTimes(){
        return String.valueOf(System.currentTimeMillis());
    }

    private static String getSHA256(String str){

        MessageDigest messageDigest;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodestr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodestr;
    }

    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }


}
