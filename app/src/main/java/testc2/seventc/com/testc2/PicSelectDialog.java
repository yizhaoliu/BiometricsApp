package testc2.seventc.com.testc2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;




public class PicSelectDialog extends Dialog {
    private TextView tv_photo,tv_camera,tv_dis;
    private static PicSelectDialog dialog;
    private static String fileName;
    public static final int PHONE_FLAG=2;
    public static final int CAMERA_FLAG=1;
    public static final int CROP_FLAG=3;
    private static String IMAGE_FILE_LOCATION = "file:///storage/emulated/0/DCIM/Camera/temp.jpeg";
    public static Uri imageUri;



    protected PicSelectDialog(@NonNull final Context context, int themeResId, final GetFileName getFileName) {
        super(context,themeResId);
        setContentView(R.layout.tm_shops_pic_dialog);
        tv_photo=  findViewById(R.id.tv_photo);
        tv_camera=  findViewById(R.id.tv_camera);
        tv_dis= findViewById(R.id.tv_dis);

        tv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog!=null){
                    dialog.dismiss();
                }
                UUID uuid = UUID.randomUUID();
                fileName = "temp" + ".jpeg";
                getFileName.name(fileName);

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");//相片类型
                getFileName.click(intent,PHONE_FLAG);
            }
        });
        tv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog!=null){
                    dialog.dismiss();
                }
                fileName="";
                UUID uuid = UUID.randomUUID();
                fileName = "temp" + ".jpeg";
                Log.i("TAG_Camera", "fileName="+fileName);
                getFileName.name(fileName);
                //将拍摄的照片保存在一个指定好的文件下
                File dir= new File("/storage/emulated/0/Pictures/tm/");
                if(!dir.exists()){
                    dir.mkdirs();
                }
                File f = new File(dir, fileName);
                String filePath = dir.getPath() + fileName;
                System.out.println(filePath);

                Uri u = Uri.fromFile(f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    u = FileProvider.getUriForFile(context, "testc2.seventc.com.testc2", f);//通过FileProvider创建一个content类型的Uri
                }
                //调用系统相机
                Intent intentCamera = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //添加这一句表示对目标应用临时授权该Uri所代表的文件
                    intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                //将拍照结果保存至photo_file的Uri中，不保留在相册中
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, u);
                getFileName.click(intentCamera,CAMERA_FLAG);

            }
        });
        tv_dis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog!=null){
                    dialog.dismiss();
                }
            }
        });

    }

    public static void show(Context context, GetFileName getFileName) {
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        if (dialog != null && dialog.isShowing()) {
            return;
        }

        dialog = new PicSelectDialog(context,R.style.dialog,getFileName);
        dialog.show();
    }

    public interface GetFileName{
        void name(String name);
        void click(Intent intent, int resultFlag);
    }


    /**
     * 相机
     * @param mContext
     * @return
     */
    public static Uri compressCameraImg(Context mContext,String fileName){
        Uri imgUrl=null;
        if (fileName==null){
            return null;
        }
        //返回原图
        try {
            File fData = new File("/storage/emulated/0/Pictures" + "/tm/" + fileName);
            imgUrl = Uri.parse(MediaStore.Images.Media.insertImage(mContext.getContentResolver(), fData.getAbsolutePath(), null, null));
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("TAG_Camera", "Exception="+e.toString());
        }

        return imgUrl;
    }

    /**
     * 相册
     * @param mContext
     * @param data
     * @return
     */
    public static Uri compressPhoneImg(Context mContext, Intent data){
        //拍照截图返回
        Uri myUri=null;
        try {
            if (data.getData() != null) {
                myUri = data.getData();
            }
        } catch (Exception e) {
            Log.i("TAG_Phone", "Exception="+e.toString());
        }
        return myUri;
    }
    public static void getBitmap(Uri uri, int x1, int x2, int y1, int y2) throws Exception {
        //String srcFileString = uri.toString();
        //File file = new File(srcFileString);
        FileInputStream fis = new FileInputStream("/storage/emulated/0/Pictures/tm/temp.jpeg");
        System.out.println("2   "+uri.getPath());
        /*View screenView1 = getWindow().getDecorView();
        screenview.setDrawingCacheEnabled(true);
        screenview.buildDrawingCache();*/

        //获取屏幕整张图片
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        String path =  "/storage/emulated/0/DCIM/Camera/preview.jpeg";

        try {
            //从屏幕整张图片中截取指定区域
            bitmap = Bitmap.createBitmap(bitmap, x1, y1, x2-x1, y2-y1);
            FileOutputStream fout = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            System.out.println("截取成功");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
        }
    }

    public static void crop(Context mContext,Uri uri,int w,int h,CropCallBack back){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String srcFileString = "/storage/emulated/0/DCIM/Camera/temp.jpeg";
            File file = new File(srcFileString);
            if (file.exists()){ //如果已经存在，则先删除
                file.delete();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("TAG_cropEx",e.toString());
            }
            imageUri = Uri.fromFile(file);
        }else {
            imageUri = Uri.parse(IMAGE_FILE_LOCATION);
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        System.out.println("手动截图地址："+uri.getPath());
        intent.setDataAndType(uri, "image/*");//图片资源uri

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }else {

        }

        intent.putExtra("crop", "true");

        // 设置x,y的比例，截图方框就按照这个比例来截 若设置为0,0，或者不设置 则自由比例截图
        intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);

        // 裁剪区的宽和高 其实就是裁剪后的显示区域 若裁剪的比例不是显示的比例，则自动压缩图片填满显示区域。若设置为0,0 就不显示。若不设置，则按原始大小显示
//        intent.putExtra("outputX", 300);
//        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//截图返回的uri
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        back.crop(intent,CROP_FLAG);
    }

    public interface CropCallBack{
        void crop(Intent intent, int flag);
    }
}
