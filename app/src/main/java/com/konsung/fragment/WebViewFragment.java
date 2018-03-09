package com.konsung.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.konsung.R;
import com.konsung.util.UiUitls;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by JustRush on 2015/7/22.
 */
public class WebViewFragment extends BaseFragment {
    @InjectView(R.id.web)
    WebView webVi;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web, null);
        ButterKnife.inject(this, view);
        webVi.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        webVi.setBackgroundColor(Color.parseColor("#ededed"));
        webVi.setBackground(getResources().getDrawable(R.drawable.background));
        WebSettings settings = webVi.getSettings();
        settings.setJavaScriptEnabled(true);
        webVi.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        Bundle bundle = getArguments();
        String url = bundle.getString("url");
        String whichFragment = bundle.getString("fragment");
        if (whichFragment.equals("health")) {
            changeTiTle(getRecString(R.string
                    .image_description_public_administration));
        } else if (whichFragment.equals("admin")) {
            changeTiTle(getRecString(R.string
                    .image_description_public_administration));
        } else if (whichFragment.equals("help")) {

        } else if (whichFragment.equals("person")) {
            changeTiTle(UiUitls.getString(R.string.patient_detail));
        } else if (whichFragment.equals("check")) {
            changeTiTle(UiUitls.getString(R.string.measure_detail));
        }
        try {
            getActivity().getResources().getAssets().open("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        webVi.loadUrl(url);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
