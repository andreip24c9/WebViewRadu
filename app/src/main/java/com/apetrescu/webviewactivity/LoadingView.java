package com.apetrescu.webviewactivity;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by apetrescu on 10/5/2017.
 */

public class LoadingView extends LinearLayout implements View.OnClickListener {


    public interface OnRetryClickListener {

        void onRetry();
    }

    private ProgressBar mProgressBar;
    private LinearLayout mErrorLayout;

    private boolean mIsLoading;
    private boolean mIsShowingError;

    private OnRetryClickListener mOnRetryClickListener;
    private TextView mErrorMessageTv;
    private Button mRetryBtn;


    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context) {
        this(context, null, 0);
    }


    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.loading_layout, this, true);

        mProgressBar = findViewById(R.id.progress_layout);
        mErrorLayout = findViewById(R.id.error_layout);
        mErrorMessageTv = findViewById(R.id.err_message_lb);
        mRetryBtn = findViewById(R.id.err_retry_btn);

        mProgressBar.setVisibility(VISIBLE);
        mRetryBtn.setOnClickListener(this);
    }

    public void isLoading(boolean loading) {
        mIsLoading = loading;
        if (mIsShowingError && loading) {
            isShowingError(false);
        }
        setVisibility(mIsLoading || mIsShowingError ? VISIBLE : GONE);
        mProgressBar.setVisibility(loading ? VISIBLE : GONE);
    }

    private void isShowingError(boolean isShowingError) {
        mIsShowingError = isShowingError;
        if (mIsLoading && isShowingError) {
            isLoading(false);
        }
        setVisibility(mIsLoading || mIsShowingError ? VISIBLE : GONE);
        mErrorLayout.setVisibility(isShowingError ? VISIBLE : GONE);
    }

    public void hideError() {
        isShowingError(false);
    }

    public void showError(String error) {
        if (!TextUtils.isEmpty(error) && !mIsShowingError) {
            isShowingError(true);
            mErrorMessageTv.setText(error);
        }
    }

    public void setOnRetryClickListener(OnRetryClickListener listener) {
        this.mOnRetryClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnRetryClickListener != null) {
            mOnRetryClickListener.onRetry();
        }
    }
}
