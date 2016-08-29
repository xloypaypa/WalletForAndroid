package com.example.xsu.walletforandroid.handler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class MessageHandler extends Handler {

    private MessageHandler() {
    }

    private MessageHandler(Callback callback) {
        super(callback);
    }

    private MessageHandler(Looper looper) {
        super(looper);
    }

    private MessageHandler(Looper looper, Callback callback) {
        super(looper, callback);
    }

    public interface CommandSolver {
        boolean solveCommand(Activity activity,byte[] body) throws JSONException;
    }

    public static class Builder {

        private Map<String, CommandSolver> commandSolverMap = new HashMap<>();
        private Activity activity;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder addCommandSolver(String command, CommandSolver commandSolver) {
            this.commandSolverMap.put(command, commandSolver);
            return this;
        }

        public MessageHandler create() {
            return new MessageHandler(new Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    Bundle data = message.getData();
                    String command = data.getString("command");
                    byte[] body = data.getByteArray("body");

                    try {
                        return commandSolverMap.containsKey(command) && commandSolverMap.get(command).solveCommand(activity, body);
                    } catch (JSONException e) {
                        return false;
                    }
                }
            });
        }

    }

}
