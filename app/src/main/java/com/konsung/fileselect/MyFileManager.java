package com.konsung.fileselect;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.fragment.SettingFragment;
import com.konsung.util.GlobalConstant;
import com.konsung.util.UiUitls;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MyFileManager extends ListActivity {
    private List<String> items = null;
    private List<String> paths = null;
    private String rootPath = "/";
    private String curPath = "/";
    private TextView mPath;

    private final static String TAG = "MyFileManager";

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.fileselect);
        mPath = (TextView) findViewById(R.id.mPath);
        Button buttonConfirm = (Button) findViewById(R.id.buttonConfirm);
        buttonConfirm.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent data = new Intent(MyFileManager.this, SettingFragment
                        .class);
                Bundle bundle = new Bundle();
                bundle.putString("file", curPath);
                data.putExtras(bundle);
                setResult(2, data);
                GlobalConstant.isCancel = true;
                finish();

            }
        });
        Button buttonCancle = (Button) findViewById(R.id.buttonCancle);
        buttonCancle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                GlobalConstant.isCancel = true;
                finish();
            }
        });
        Environment environment = new Environment();
        File sdCardDir = Environment.getExternalStorageDirectory();
        rootPath = sdCardDir.getPath();
        Log.d(TAG, "" + rootPath);
        List<String> sdPath = getSDCardPathEx();
        for (String path : sdPath) {
            Log.e("sdPath", path);
        }
        curPath = "/storage";
        getFileDir(rootPath, sdPath);

        PopupHelpDialog();
    }

    private void PopupHelpDialog() {
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(UiUitls.getContent().getResources().getString(R
                        .string.hint_info))
                .setMessage(UiUitls.getContent().getResources().getString(R
                        .string.dialog_message_out))
                .setPositiveButton(UiUitls.getContent().getResources()
                        .getString(R.string.confirm), new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void getFileDir(String filePath, List<String> sdkPath) {
        items = new ArrayList<String>();
        paths = new ArrayList<String>();
        if (sdkPath != null && sdkPath.size() > 0) {
            mPath.setText("/storage");
            for (String path : sdkPath) {
                File file = new File(path);
                items.add(file.getName());
                paths.add(file.getPath());
            }
        } else {
            if ("/storage".equals(filePath)) {
                getFileDir(filePath, getSDCardPathEx());
                return;
            }
            mPath.setText(filePath);
            File f = new File(filePath);
            File[] files = f.listFiles();

            if (!filePath.equals("/storage")) {
                items.add("b1");
                paths.add("/storage");
                items.add("b2");
                paths.add(f.getParent());
            }
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                items.add(file.getName());
                paths.add(file.getPath());
            }
        }
        setListAdapter(new MyAdapter(this, items, paths));
    }

    //查看所有的sd路径
    public List<String> getSDCardPathEx() {
        List<String> pathList = new ArrayList<>();
        String mount = new String();
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("storage") && !line.contains("tmpfs")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        pathList.add(columns[1]);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathList;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        File file = new File(paths.get(position));
        if (file.isDirectory()) {
            curPath = paths.get(position);
            getFileDir(paths.get(position), null);
        } else {
            //openFile(file);
        }
    }

    private void openFile(File f) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        String type = getMIMEType(f);
        intent.setDataAndType(Uri.fromFile(f), type);
        startActivity(intent);
    }

    private String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        String end = fName
                .substring(fName.lastIndexOf(".") + 1, fName.length())
                .toLowerCase();

        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                || end.equals("xmf") || end.equals("ogg") || end.equals
                ("wav")) {
            type = "audio";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            type = "image";
        } else {
            type = "*";
        }
        type += "/*";
        return type;
    }
}