package com.konsung.fragment;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsung.R;
import com.konsung.bean.Response.VideoResponse;
import com.konsung.bean.VideoInfo;
import com.konsung.defineview.CustomVideoView;
import com.konsung.util.GlobalConstant;
import com.konsung.util.JsonUtils;
import com.konsung.util.RequestUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.Utility;
import com.konsung.util.global.GlobalNumber;
import com.konsung.util.global.VideoInfoSaveModule;
import com.konusng.adapter.OnlineVideoAdapter;
import com.konusng.adapter.VideoAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author xiangshicheng
 *         视频培训页面
 */
public class VideoHelpFragment extends BaseFragment implements GridView.OnItemClickListener,
        View.OnClickListener {

    @InjectView(R.id.video_gridview)
    GridView videoGridView;
    @InjectView(R.id.video_play_layout)
    RelativeLayout rlVideoPlayLayout;
    @InjectView(R.id.video_view)
    CustomVideoView videoView;
    @InjectView(R.id.tv_current_video_name)
    TextView videoName;
    @InjectView(R.id.close_video)
    ImageView ivCloseVideo;
    @InjectView(R.id.ll_video_info)
    RelativeLayout rlVideoInfo;
    @InjectView(R.id.tv_local_video)
    TextView tvLocalVideo;
    @InjectView(R.id.pb_video)
    ProgressBar progressBar;
    @InjectView(R.id.tv_video_msg)
    TextView tvmsg;
    @InjectView(R.id.tv_online_video)
    TextView tvOnlineVideo;
    @InjectView(R.id.lv_video_online)
    ListView lvVideoOnline;
    @InjectView(R.id.rl_online_video)
    RelativeLayout rlOnlineVideo;
    @InjectView(R.id.wv_online)
    WebView wvOnline;
    @InjectView(R.id.iv_close_online)
    ImageView ivCloseOnline;
    @InjectView(R.id.ll_video_container)
    LinearLayout llContainer;
    private List<VideoInfo> listVideo;
    private VideoAdapter videoAdapter;
    //视频信息显示默认时间
    private int videoShowTime = GlobalNumber.FIVE_THOUSAND_NUMBER;
    private boolean isPause = false;
    private static final int ALPHA = 50;
    private List<VideoResponse.Video> onlineVideoList;
    private Context context = null;
    //标识本地视频是否导入成功
    private boolean isHadLoadSuccess = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_help, container, false);
        ButterKnife.inject(this, view);
        initListener();
        initData();
        initView();
        return view;
    }

    /**
     * 初始化页面
     */
    private void initView() {
        rlVideoInfo.getBackground().setAlpha(ALPHA);
        changeBackground(true);
        initWebView();
    }

    /**
     * 初始化配置WebView
     */
    private void initWebView() {
        WebSettings settings = wvOnline.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowFileAccess(true);
        settings.setLoadWithOverviewMode(false);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        wvOnline.setWebChromeClient(new WebChromeClient());
        wvOnline.setWebViewClient(new WebViewClient());
    }

    /**
     * 初始化数据
     */
    private void initData() {
        context = getActivity();
        listVideo = new ArrayList<VideoInfo>();
        videoAdapter = new VideoAdapter(getActivity(), listVideo, handler);
        videoGridView.setAdapter(videoAdapter);
//        List<VideoInfo> videoInfoList = VideoInfoSaveModule.getInstance().videoInfos;
//        if (videoInfoList != null && videoInfoList.size() > 0) {
//            listVideo.clear();
//            listVideo.addAll(videoInfoList);
//            videoAdapter.notifyDataSetChanged();
//        }
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        videoGridView.setOnItemClickListener(this);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Message message = handler.obtainMessage(1);
                handler.sendMessageDelayed(message, videoShowTime);
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                rlVideoPlayLayout.setVisibility(View.GONE);
                videoGridView.setVisibility(View.VISIBLE);
            }
        });
        videoView.setOnClickListener(this);
        ivCloseVideo.setOnClickListener(this);
        tvLocalVideo.setOnClickListener(this);
        tvOnlineVideo.setOnClickListener(this);
        ivCloseOnline.setOnClickListener(this);
        lvVideoOnline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoResponse.Video video = onlineVideoList.get(position);
                lvVideoOnline.setVisibility(View.GONE);
                rlOnlineVideo.setVisibility(View.VISIBLE);
                wvOnline.loadUrl(video.getVideoPath());
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        videoGridView.setVisibility(View.GONE);
        rlVideoPlayLayout.setVisibility(View.VISIBLE);
        VideoInfo videoInfo = listVideo.get(position);
        play(videoInfo.getPath());
        videoName.setText(UiUitls.separatorVideoName(videoInfo.getName()));
    }

    /**
     * 查找本地视频文件
     */
    private void findData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<VideoInfo> allFiles = Utility.getVideosFromSD(getActivity());
                if (allFiles != null && allFiles.size() > 0) {
                    listVideo.clear();
                    listVideo.addAll(allFiles);
                }
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listVideo.size() > 0) {
                            VideoInfoSaveModule.getInstance().videoInfos = listVideo;
                            videoAdapter.notifyDataSetChanged();
                            UiUitls.toast(getActivity(), UiUitls.getString(R.string
                                    .import_success));
                        } else {
                            UiUitls.toast(getActivity()
                                    , UiUitls.getString(R.string.native_not_find_video));
                        }
                        isHadLoadSuccess = true;
                        UiUitls.hideProgress();
                    }
                });
            }
        }).start();
    }

    /**
     * 播放视频方法
     * @param path 视频路径
     */
    private void play(String path) {
        videoView.setMediaController(new MediaController(getActivity()));
        videoView.setVideoPath(path);
        videoView.start();
        videoView.requestFocus();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                videoShowTime--;
                if (videoShowTime > 0) {
                    Message message = handler.obtainMessage(1);
                    handler.sendMessage(message);
                } else {
//                    rlVideoInfo.setVisibility(View.GONE);
                }
            } else if (msg.what == 2) {
                //暂停视频
                if (videoView.isPlaying()) {
                    videoView.pause();
                }
            } else if (msg.what == GlobalNumber.THREE_NUMBER) {
                //重新播放视频
                videoView.start();
                videoView.requestFocus();
            } else if (msg.what == GlobalNumber.FOUR_NUMBER) {
                int position = (int) msg.obj;
                videoGridView.setVisibility(View.GONE);
                rlVideoPlayLayout.setVisibility(View.VISIBLE);
                VideoInfo videoInfo = listVideo.get(position);
                play(videoInfo.getPath());
                videoName.setText(UiUitls.separatorVideoName(videoInfo.getName()));
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_view:
                if (!isPause) {
                    //暂停视频
                    isPause = true;
                    rlVideoInfo.setVisibility(View.VISIBLE);
                    Message message = handler.obtainMessage(2);
                    handler.sendMessage(message);
                } else {
                    //重新播放视频
                    isPause = false;
                    rlVideoInfo.setVisibility(View.GONE);
                    Message message = handler.obtainMessage(GlobalNumber.THREE_NUMBER);
                    handler.sendMessage(message);
                }
                break;
            case R.id.close_video:
                videoView.pause();
                MediaController mediaController = new MediaController(getActivity());
                mediaController.setVisibility(View.INVISIBLE);
                videoView.setMediaController(null);
                //videoView.stopPlayback();
                rlVideoPlayLayout.setVisibility(View.GONE);
                videoGridView.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_local_video:
                changeBackground(true);
                break;
            case R.id.tv_online_video:
                changeBackground(false);
                break;
            case R.id.iv_close_online:
                wvOnline.onPause(); //停止播放
                wvOnline.stopLoading();
                wvOnline.reload();
                rlOnlineVideo.setVisibility(View.GONE);
                lvVideoOnline.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * 加载在线视频
     */
    private void loadOnlineVideo() {

        final String url = GlobalConstant.HTTP + SpUtils.getSp(UiUitls.getContent(), GlobalConstant
                .APP_CONFIG, GlobalConstant.SERVICE_IP, GlobalConstant.IP_DEFAULT) + ":" + SpUtils
                .getSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG, GlobalConstant.IP_PROT,
                GlobalConstant.PORT_DEFAULT) + GlobalConstant.ONLINE_VIDEO_URL;
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestUtils.clientGet(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        String result = new String(bytes);
                        final VideoResponse videoResponse = JsonUtils.toEntity(result,
                                VideoResponse.class);
                        UiUitls.post(new Runnable() {
                            @Override
                            public void run() {
                                if (RequestUtils.SUCCESS_CODE.equals(videoResponse.getCode())) {
                                    onlineVideoList = videoResponse.getVideoData();
                                    OnlineVideoAdapter adapter = new OnlineVideoAdapter(
                                            getActivity(), onlineVideoList);
                                    lvVideoOnline.setAdapter(adapter);
                                } else {
                                    Toast.makeText(getActivity(), R.string.loading_fail, Toast
                                            .LENGTH_SHORT).show();
                                }
                                UiUitls.hideProgress();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable
                            throwable) {
                        UiUitls.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), R.string.loading_fail, Toast
                                        .LENGTH_SHORT).show();
                                UiUitls.hideProgress();
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 改变背景
     * @param select 是否选中本地视频
     */
    private void changeBackground(boolean select) {
        if (select) {
            if (!isHadLoadSuccess) {
                UiUitls.showProgress(context, UiUitls.getString(R.string.loading_local_data));
                findData();
            } else {
                //当前页面内加载成功后的切换不重新触发加载
                listVideo = VideoInfoSaveModule.getInstance().videoInfos;
                videoAdapter.notifyDataSetChanged();
            }
            tvLocalVideo.setTextColor(Color.WHITE);
            tvLocalVideo.setBackgroundResource(R.drawable.bg_local_video_sel);
            tvOnlineVideo.setTextColor(getResources().getColor(R.color.actionbar_bg_color));
            tvOnlineVideo.setBackgroundResource(R.drawable.bg_net_video_nor);
            videoGridView.setVisibility(View.VISIBLE);
            lvVideoOnline.setVisibility(View.GONE);
        } else {
            UiUitls.showProgress(context, UiUitls.getString(R.string.loading_online_video));
            tvLocalVideo.setTextColor(getResources().getColor(R.color.actionbar_bg_color));
            tvLocalVideo.setBackgroundResource(R.drawable.bg_local_video_nor);
            tvOnlineVideo.setTextColor(Color.WHITE);
            tvOnlineVideo.setBackgroundResource(R.drawable.bg_net_video_sel);
            videoGridView.setVisibility(View.GONE);
            lvVideoOnline.setVisibility(View.VISIBLE);
            loadOnlineVideo();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            videoView.stopPlayback();
            rlVideoPlayLayout.setVisibility(View.GONE);
            videoGridView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        wvOnline.destroy();
    }
}
