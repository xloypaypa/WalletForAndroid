package com.example.xsu.walletforandroid.net;

import com.example.xsu.walletforandroid.tools.PasswordEncoder;
import com.example.xsu.walletforandroid.tools.RSA;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;

/**
 * Created by xlo on 16/2/23.
 * it's the protocol builder
 */
public class ProtocolBuilder {

    public static byte[] getSessionId() {
        return "/getSessionID#{}".getBytes();
    }

    public static byte[] key(PublicKey publicKey) {
        byte[] key = RSA.publicKey2Bytes(publicKey);
        byte[] url = "/key#".getBytes();
        byte[] all = new byte[url.length + key.length];
        System.arraycopy(url, 0, all, 0, url.length);
        System.arraycopy(key, 0, all, url.length, key.length);
        return all;
    }

    public static byte[] login(String username, String password, String sessionID) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        jsonObject.put("password", PasswordEncoder.encode(password + sessionID));
        String body = jsonObject.toString();
        return ("/login#" + body).getBytes();
    }

    public static byte[] register(String username, String password) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        jsonObject.put("password", password);
        String body = jsonObject.toString();
        return ("/register#" + body).getBytes();
    }

    public static byte[] useApp(String appName) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appName", appName);
        String body = jsonObject.toString();
        return ("/useApp#" + body).getBytes();
    }

    public static byte[] rollBack() {
        return "rollBack#{}".getBytes();
    }

    public static byte[] useMoney(String typename, String budgetType, double value) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("typename", typename);
        jsonObject.put("value", value);
        jsonObject.put("budgetType", budgetType);
        String body = jsonObject.toString();
        return ("useMoney#" + body).getBytes();
    }

    public static byte[] income(String typename, double value) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("typename", typename);
        jsonObject.put("value", value);
        String body = jsonObject.toString();
        return ("income#" + body).getBytes();
    }

    public static byte[] addMoney(String typename, double value) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("typename", typename);
        jsonObject.put("value", value);
        String body = jsonObject.toString();
        return ("addMoney#" + body).getBytes();
    }

    public static byte[] removeMoney(String typename) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("typename", typename);
        return ("removeMoney#" + jsonObject.toString()).getBytes();
    }

    public static byte[] getMoney() {
        return "getMoney#{}".getBytes();
    }

    public static byte[] transferMoney(String from, String to, double value) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from", from);
        jsonObject.put("to", to);
        jsonObject.put("value", value);
        return ("transferMoney#" + jsonObject.toString()).getBytes();
    }

    public static byte[] addBudget(String typename, double value) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("typename", typename);
        jsonObject.put("value", value);
        String body = jsonObject.toString();
        return ("addBudget#" + body).getBytes();
    }

    public static byte[] removeBudget(String typename) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("typename", typename);
        return ("removeBudget#" + jsonObject.toString()).getBytes();
    }

    public static byte[] transferBudget(String from, String to, double value) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from", from);
        jsonObject.put("to", to);
        jsonObject.put("value", value);
        return ("transferBudget#" + jsonObject.toString()).getBytes();
    }

    public static byte[] getBudget() {
        return "getBudget#{}".getBytes();
    }

    public static byte[] assignMoneyToBudget(String typename, double value) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("typename", typename);
        jsonObject.put("value", value);
        String body = jsonObject.toString();
        return ("assignMoneyToBudget#" + body).getBytes();
    }

}
