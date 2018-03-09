package com.konsung.fileselect;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.konsung.R;
import com.konsung.activity.MyApplication;
import com.konsung.fragment.SettingFragment;
import com.konsung.util.GlobalConstant;
import com.konsung.util.UiUitls;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DJH on 2016/6/3 0003.
 */
public class SelectHelpFile extends ListActivity {

    private List<String> items = null;
    private List<String> paths = null;
    private String rootPath = "/";
    private String curPath = "/";
    private TextView mPath;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.fileselect);
        mPath = (TextView) findViewById(R.id.mPath);
        Button buttonConfirm = (Button) findViewById(R.id.buttonConfirm);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent data = new Intent(SelectHelpFile.this,
                        SettingFragment.class);
                Bundle bundle = new Bundle();
                bundle.putString("file", curPath);
                data.putExtras(bundle);
                setResult(2, data);
                finish();

            }
        });
        Button buttonCancle = (Button) findViewById(R.id.buttonCancle);
        buttonCancle.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                finish();
            }
        });
        Environment environment = new Environment();
        File sdCardDir = Environment.getExternalStorageDirectory();
        rootPath = sdCardDir.getPath();

        List<String> sdPath = getSDCardPathEx();
        for (String path : sdPath) {
            Log.e("sdPath", path);
        }
        curPath = rootPath;
        getFileDir(rootPath, sdPath);
        PopupHelpDialog();
    }

    /**
     *  初始化dialog显示
     */
    private void PopupHelpDialog() {
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(UiUitls.getContent().getResources().getString(R
                        .string.hint_info))
                .setMessage(UiUitls.getContent().getResources().getString(R
                        .string.dialog_message_help_in))
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

    /**
     *  获取到文件目录
     * @param filePath
     * @param sdkPath
     */
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
                paths.add(rootPath);
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

    /**
     *     查看所有的sd路径
     */

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

            if (!file.getName().endsWith(".pdf")) {
                Toast.makeText(MyApplication.getGlobalContext(),
                        R.string.tip_choose_pdf, Toast.LENGTH_SHORT).show();
                return;
            }

            finish(); // 先消失

            // 打开
//            Intent intent = new Intent(this, HelpPdfActivity.class);
//            intent.putExtra("file",file);
//            startActivity(intent);
            File newFile = copyFile(file, GlobalConstant.KONSUNG_HELP_PDF_NAME);
            if (newFile != null && newFile.length() > 0) {
                Toast.makeText(MyApplication.getGlobalContext(),
                        R.string.tip_import_pdf_ok, Toast.LENGTH_SHORT).show();
            }


        }
    }

    /**
     * 复制pdf帮助文件到内部存储的根目录下
     *
     * @param file     旧的文件对象
     * @param filename 新的文件名
     * @return File   新的文件对象 （如果失败则返回null）
     */
    private File copyFile(File file, String filename) {
        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(file);
            File dirs = new File(GlobalConstant.HELP_PDF_PATH);
            if (!dirs.exists()) {
                dirs.mkdirs();
            }
            File srcFile = new File(dirs, filename);

            out = new FileOutputStream(srcFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;

            return srcFile;
        } catch (Exception e) {
            return null;
        }
    }

}
