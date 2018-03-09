package com.konsung.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.ScrollBar;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.konsung.R;
import com.konsung.activity.MyApplication;
import com.konsung.util.GlobalConstant;
import com.konsung.util.UiUitls;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by dlx on 2015/7/22.
 */
public class HelpPdfFragment extends BaseFragment {

    @InjectView(R.id.pv_help)
    PDFView pvHelp;
    @InjectView(R.id.sb_scrollBar)
    ScrollBar sbScrollbar;
    @InjectView(R.id.btn_pre)
    Button btnPre;
    @InjectView(R.id.btn_next)
    Button btnNext;
    @InjectView(R.id.tv_page_num)
    TextView tvPageNum;
    @InjectView(R.id.tv_empty)
    TextView tvEmpty;

    private int pageNumber = 1; // 记录当前页码

    private File file;
    private int pageSum;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_view, null);
        ButterKnife.inject(this, view);

        init();
        return view;
    }

    /**
     * 初始化pdf 文件对象
     */
    private void init() {

        file = new File(GlobalConstant.HELP_PDF_PATH,
                GlobalConstant.KONSUNG_HELP_PDF_NAME);

        if (!file.exists() || file.length() <= 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        } else {
            tvEmpty.setVisibility(View.GONE);
        }

        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UiUitls.isFastDoubleClick()) {
                    return;
                }
                sbScrollbar.setCurrentPage(pageNumber - 1);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UiUitls.isFastDoubleClick()) {
                    return;
                }
                sbScrollbar.setCurrentPage(pageNumber + 1);
            }
        });
        pvHelp.setScrollBar(sbScrollbar);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        String pageText = String.format("%s / %s", pageNumber, pageSum);
        tvPageNum.setText(pageText);

        // 判空处理
        if (file.length() <= 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        } else {
            tvEmpty.setVisibility(View.GONE);
            if (sbScrollbar != null) {
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                loadPdf();
                            }
                        }
                ).start();
                //回到之前查看那一页pdf文档
            }
        }
    }

    /**
     * 加载pdf
     */
    private void loadPdf() {
        pvHelp.fromFile(file)
                .defaultPage(pageNumber)
                .enableDoubletap(false)
                .swipeVertical(true)
                .enableSwipe(false)
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        pvHelp.zoomWithAnimation(2.5f);
                        tvPageNum.setText(String.format("%s / %s",
                                pageNumber, nbPages));
                        pageSum = nbPages;
                    }
                })
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        pvHelp.setMinZoom(1f);
                        pvHelp.zoomWithAnimation(2.5f);
                        pageNumber = page;
                        String pageText = String.format("%s / %s",
                                page, pageCount);
                        tvPageNum.setText(pageText);

                    }
                })
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(MyApplication.getGlobalContext(),
                                R.string.tip_pdf_file_error,
                                Toast.LENGTH_SHORT).show();
                        file.delete();
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                })
                .load();
        pvHelp.setScrollBar(sbScrollbar);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
