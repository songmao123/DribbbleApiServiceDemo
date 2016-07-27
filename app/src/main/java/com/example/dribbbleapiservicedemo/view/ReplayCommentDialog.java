package com.example.dribbbleapiservicedemo.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dribbbleapiservicedemo.R;

/**
 * Created by 青松 on 2016/7/19.
 */
public class ReplayCommentDialog extends Dialog {

    private OnCommentReplayListener listener;

    public ReplayCommentDialog(Context context, OnCommentReplayListener l) {
        super(context, R.style.dialog_password);
        this.listener = l;
    }

    public interface OnCommentReplayListener {
        /** 发布评论*/
        void onReplay(View view, String replayBody);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window dialogWindow = this.getWindow();
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogWindow.setGravity(Gravity.BOTTOM);
        View contentView = getLayoutInflater().inflate(R.layout.layout_edit_coment, null);
        setContentView(contentView);
        setCanceledOnTouchOutside(true);
        dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        ImageView replay_iv = (ImageView) contentView.findViewById(R.id.replay_iv);
        final EditText replay_et = (EditText) contentView.findViewById(R.id.replay_et);
        replay_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String replayBody = replay_et.getText().toString().trim();
                if (TextUtils.isEmpty(replayBody)) {
                    Toast.makeText(getContext(), "Please Input A Comment!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (listener != null) {
                    listener.onReplay(v, replayBody);
                }
            }
        });
    }
}
