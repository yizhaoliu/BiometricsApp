package testc2.seventc.com.testc2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Json extends AppCompatActivity implements View.OnClickListener {
    private ImageView img;
    public 	ListView lv;
    public ArrayList<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.json);
        img = findViewById(R.id.iv);

        TextView sendRequest = (TextView) findViewById(R.id.upload);
        sendRequest.setOnClickListener(this);
        final String filepath = "/storage/emulated/0/DCIM/Camera/temp.jpeg";
        Bitmap bmp= BitmapFactory.decodeFile(filepath);
        ImageView iv= (ImageView)findViewById(R.id.iv);
        iv.setImageBitmap(bmp);
    }
    //上传
    public void onClick(View v) {
        if (v.getId() == R.id.upload) {
            Toast.makeText(Json.this, "识别中...", Toast.LENGTH_SHORT).show();
            mtest();
            //readFromAssets();
        }
    }
   public void mtest(){
       list.clear();
       lv=(ListView) findViewById(R.id.lv);
        //构建URL的格式为:    http://IP地址:监听的端口号/Servlet的路径
        final String strUrl = "http://39.108.83.121:443/up_photo";
        //final String strUrl = "http://10.18.38.163:8080/up_photo";
        //文件路径+文件名
        final String filepath = "/storage/emulated/0/pcs.zip";
        final URL[] url = {null};
        Thread th=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String content = null;
                    try {
                        content = "?command=" +"filename="+ URLEncoder.encode("pcs.zip", "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //第一步：访问网站，进行连接
                    url[0] =new URL(strUrl+content);
                    HttpURLConnection urlConn=(HttpURLConnection) url[0].openConnection();
                    urlConn.setDoInput(true);       //setting inputstream using bytestream
                    urlConn.setDoOutput(true);
                    urlConn.setRequestMethod("POST");
                    urlConn.setUseCaches(false);
                    urlConn.setRequestProperty("Content-Type","application/x-ww-form-urlencoded");  //
                    urlConn.setRequestProperty("Charset","utf-8");
                    urlConn.connect();
                    //第二步：打开数据通道
                    DataOutputStream dop=new DataOutputStream(urlConn.getOutputStream());
                    //第三步：准备好发送的数据
                    File f=new File(filepath);
                    FileInputStream fis=new FileInputStream(f);
                    if(f.isFile()){
                        byte[] buffer=new byte[1024];
                        int length=0;
                        while ((length=fis.read(buffer))!=-1){
                            dop.write(buffer,0,length);
                        }
                    }else {         //如果不是文件，直接返回，然后啥也不干
                        return;
                    }
                    //第四步：将准备的数据发送给服务器
                    dop.flush();
                    System.out.printf("上传成功");
                    dop.close();
                    //第五步：打开输入的数据通道，等待服务端回发数据！
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                    /*String jsonString = bufferedReader.readLine();
                    System.out.printf(jsonString);*/
                    String readLine=null;
                    StringBuilder response = new StringBuilder();
                    /*while (jsonString!=null){
                        System.out.printf(jsonString);
                        jsonJX(jsonString);}*/
                    while ((readLine=bufferedReader.readLine())!=null){
                        System.out.printf(readLine);
                        response.append(readLine);
                        String text=response.toString().toLowerCase();
                        jsonJX(text);
                        System.out.printf(text);

                    }
                    //最后一步：清理场地，主要是把打开的通道都关闭掉
                    bufferedReader.close();
                    urlConn.disconnect();

                }catch (MalformedURLException e){
                    System.out.println("出现了异常："+e.getMessage());
                } catch (IOException e) {
                    System.out.println("出现了异常常："+e.getMessage());
                    Looper.prepare();
                    Toast.makeText(Json.this, "服务器出小差了", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    e.printStackTrace();
                }
            }
        });
        th.start();

    }
    private void jsonJX(String text) {
        //判断数据是空
        if(text!=null){
            try {
                //将字符串转换成jsonObject对象
                JSONArray jsonArray = new JSONArray(text);
                {
                    //遍历
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        Map<String, Object> map=new HashMap<String, Object>();
                        try {
                            //获取到json数据中数组里的内容
                            String birdname = object.getString("name");
                            String birdpro=object.getString("accuracy");
                            //String birdname1 = object.getString("name_z");
                            //String birdpro1 = object.getString("accuracy_z");

                            //存入map
                            map.put("birdname", birdname);
                            map.put("birdpro", birdpro);
                            //map.put("birdname1", birdname1);
                            //map.put("birdpro1", birdpro1);
                            //ArrayList集合
                            list.add(map);
                            System.out.print("okok");
                            break;
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }

                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
    //Handler运行在主线程中(UI线程中)，  它与子线程可以通过Message对象来传递数据
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    BirdAdapter list_view=new BirdAdapter();
                    lv.setAdapter(list_view);
                    break;
            }


        }
    };
    //Listview适配器
    public class BirdAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();

        }
        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.json_item, null);
                viewHolder.birdname = (TextView)convertView.findViewById(R.id.bird_name);
                viewHolder.birdpro = (TextView)convertView.findViewById(R.id.bird_pro);
                //viewHolder.birdname1= (TextView) convertView.findViewById(R.id.bird_name1);
                //viewHolder.birdpro1 = (TextView) convertView.findViewById(R.id.bird_pro1);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.birdname.setText(list.get(position).get("birdname").toString());
            viewHolder.birdpro.setText(list.get(position).get("birdpro").toString());
            //viewHolder.birdname1.setText(list.get(position).get("birdname1").toString());
            //viewHolder.birdpro1.setText(list.get(position).get("birdpro1").toString());
            return convertView;
        }

    }

    final static class ViewHolder {
        TextView birdname;
        TextView birdpro;
        //TextView birdname1;
        //TextView birdpro1;

    }

}
