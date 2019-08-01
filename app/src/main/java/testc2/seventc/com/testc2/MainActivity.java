package testc2.seventc.com.testc2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import static testc2.seventc.com.testc2.PicSelectDialog.imageUri;

public class MainActivity extends AppCompatActivity  {
    private String fileName;
    private Context mContext;
    private TextView paizhao;
    /**
     * 相机、相册需要的权限
     */
    public  String[]permissionsCAMERA={
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;
        paizhao=findViewById(R.id.paizhao);
        paizhao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lacksPermissions(mContext, permissionsCAMERA)) {
                    ActivityCompat.requestPermissions(MainActivity.this, permissionsCAMERA, 0);
                }else {
                    showPic();
                }
            }
        });
        //搜索
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null)
        {
            actionBar.hide();
        }
        TextView button1=(TextView) findViewById(R.id.button_1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "进入搜索", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Search.class);
                startActivity(intent);

            }
        });
        //示例
        if (actionBar!=null)
        {
            actionBar.hide();
        }
        TextView shili=(TextView) findViewById(R.id.shili);
        shili.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "进入示例", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Example.class);
                startActivity(intent);

            }
        });
    }

    private void showPic(){
        PicSelectDialog.show(this, new PicSelectDialog.GetFileName() {
            @Override
            public void name(String name) {
                fileName = name;
            }

            @Override
            public void click(Intent intent, int resultFlag) {
                startActivityForResult(intent, resultFlag);
            }


        });
    }

    private Uri uri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("TAG_Result","requestCode="+requestCode);
        switch (requestCode) {
            case PicSelectDialog.CAMERA_FLAG://相机
                uri=PicSelectDialog.compressCameraImg(mContext,fileName);
                    if (uri!=null){
                    PicSelectDialog.crop(mContext,uri, 5, 3, new PicSelectDialog.CropCallBack() {
                        @Override
                        public void crop(Intent intent, int flag) {
                            startActivityForResult(intent, PicSelectDialog.CROP_FLAG);
                        }
                    });
                }
                System.out.println("1   " + uri.toString());
                //这里传值屏幕宽高，得到的视图即全屏大小
                try {
                    PicSelectDialog.getBitmap(uri,10,400,10,400);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PicSelectDialog.PHONE_FLAG://相册
                uri=PicSelectDialog.compressPhoneImg(mContext,data);
                if (uri!=null){
                    PicSelectDialog.crop(mContext,uri, 5, 3, new PicSelectDialog.CropCallBack() {
                        @Override
                        public void crop(Intent intent, int flag) {
                            startActivityForResult(intent, PicSelectDialog.CROP_FLAG);
                        }
                    });
                }
                break;
            case PicSelectDialog.CROP_FLAG://截图返回
                Uri uri2=imageUri;
                if (uri2!=null){
                    ZipFolder();
                    Intent i = new Intent(MainActivity.this, Json.class);
                    startActivity(i);
                    System.out.println(imageUri);
                }
                break;
        }
    }
    /*
     压缩文件
    * @param srcFileString 要压缩的文件
    * @param zipFileString 压缩完成的Zip路径
    * @throws Exception
    */
    public static void ZipFolder() {
        //创建ZIP
        String srcFileString = "/storage/emulated/0/DCIM/Camera/temp.jpeg";
        String zipFileString = "/storage/emulated/0/pcs.zip";
        try {
            ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileString));
            //创建文件
            File file = new File(srcFileString);
            //压缩
            ZipFiles(file.getParent() + File.separator, file.getName(), outZip);
            System.out.printf("压缩成功");
            //完成和关闭
            outZip.finish();
            outZip.close();
        }catch (Exception e){
            System.out.printf("压缩失败");
        }
    }
    /*
     * 压缩文件
     *
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    private static void ZipFiles(String folderString, String fileString, ZipOutputStream zipOutputSteam) throws Exception {
        if (zipOutputSteam == null)
            return;
        File file = new File(folderString + fileString);
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(fileString);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
            zipOutputSteam.closeEntry();
        } else {
            //文件夹
            String fileList[] = file.list();
            //没有子文件和压缩
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }
            //子文件和递归
            for (int i = 0; i < fileList.length; i++) {
                ZipFiles(folderString+fileString+"/",  fileList[i], zipOutputSteam);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    /**
     * 判断权限集合
     * permissions 权限数组
     * return true-表示没有改权限  false-表示权限已开启
     */
    public boolean lacksPermissions(Context mContexts, String[] permissions) {
        for (String permission : permissions) {
            if (lacksPermission(mContexts,permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否缺少权限
     */
    private boolean lacksPermission(Context mContexts, String permission) {
        return ContextCompat.checkSelfPermission(mContexts, permission) ==
                PackageManager.PERMISSION_DENIED;
    }
}
