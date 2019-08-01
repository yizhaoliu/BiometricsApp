package testc2.seventc.com.testc2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import java.util.List;

public class PictureSelectAdapter extends BaseAdapter {
    private List<String> mPicPaths;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private void showLog(String msg){
        Log.d("PicSelectAdapter-->",msg);
    }

    public PictureSelectAdapter(Context context, List<String> picPaths){
        mContext = context;
        mPicPaths = picPaths;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mPicPaths.size();
    }
    @Override
    public Object getItem(int position) {
        return mPicPaths.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String path = mPicPaths.get(position);

        final ImageView imageView;
        if (convertView == null){
            imageView = (ImageView) mLayoutInflater.inflate(R.layout.item_picture,null,false);
        }
        else{
            imageView = (ImageView) convertView;
        }
        Glide.with(mContext)
                .load(Fileutil.PATH_HEAD+path)
                .into(imageView);

        return imageView;
    }

}