package testc2.seventc.com.testc2;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Fileutil {
    public static final String PATH_HEAD = "file:///android_asset/";   // 在glide中引用assets资源需要加上完整路径
    public static List<String> getAssetPicPath(Context context){
        AssetManager am = context.getAssets();
        String[] path = null;
        try {
            path = am.list("");  // ""获取所有,填入目录获取该目录下所有资源
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> pciPaths = new ArrayList<>();
        for(int i = 0; i < path.length; i++){
            if (path[i].startsWith("0") ){  // 根据图片特征找出图片&& path[i].startsWith("0")
                pciPaths.add(path[i]);
            }
        }
        return pciPaths;
    }
    /** 根据路径获取Bitmap图片
     * @param context
     * @param path
     * @return
     */
    public static Bitmap getAssetsBitmap(Context context, String path){
        AssetManager am = context.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = am.open(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }

}
