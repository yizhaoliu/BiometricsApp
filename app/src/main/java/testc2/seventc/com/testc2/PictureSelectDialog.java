package testc2.seventc.com.testc2;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import org.xutils.common.util.FileUtil;

import java.util.List;

public class PictureSelectDialog extends Activity {

    private GridView gv_ProductPic;
    private PictureSelectAdapter mAdapter;
    private List<String> mPicturePaths;
    public final static String KEY_PATH = "path";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_picture_select_dialog);
        gv_ProductPic = (GridView) findViewById(R.id.gv_productPic);
        // 获取所有资源图片
        mPicturePaths = Fileutil.getAssetPicPath(this);
        mAdapter = new PictureSelectAdapter(this, mPicturePaths);
        gv_ProductPic.setAdapter(mAdapter);
        addListener();
    }
    private void addListener(){
        gv_ProductPic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = mPicturePaths.get(position);
                Intent data = new Intent();
                data.putExtra(KEY_PATH,path);
                setResult(RESULT_OK,data);
                finish();
            }
        });
    }

}
