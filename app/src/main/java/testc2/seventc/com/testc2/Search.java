package testc2.seventc.com.testc2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {
    private List<Fruit> fruitList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initFruits(); // 初始化水果数据
        FruitAdapter adapter = new FruitAdapter(Search.this,R.layout.fruit_item, fruitList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fruit fruit = fruitList.get(position);
                String bird = fruit.getName();
                System.out.println(bird);
                if (bird.equals("喜鹊")){
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("https://baike.baidu.com/item/%E5%96%9C%E9%B9%8A/528254?fr=aladdin");//此处填链接
                    intent.setData(content_url);
                    startActivity(intent);
                }else if (bird.equals("太阳鸟")){
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("https://baike.baidu.com/item/%E5%A4%AA%E9%98%B3%E9%B8%9F/34902?fr=aladdin");//此处填链接
                    intent.setData(content_url);
                    startActivity(intent);
                }
            }
        });
    }
    private void initFruits() {
        for (int i = 0; i < 1; i++) {
            Fruit apple = new Fruit("喜鹊", R.drawable.ic_xq);
            fruitList.add(apple);
            Fruit banana = new Fruit("太阳鸟", R.drawable.ic_ty);
            fruitList.add(banana);
            Fruit orange = new Fruit("鸳鸯", R.drawable.ic_yy);
            fruitList.add(orange);
            Fruit watermelon = new Fruit("戴胜鸟", R.drawable.ic_ds);
            fruitList.add(watermelon);
            Fruit pear = new Fruit("斑纹鸟", R.drawable.ic_bw);
            fruitList.add(pear);
            Fruit grape = new Fruit("猫头鹰", R.drawable.ic_mty);
            fruitList.add(grape);
            Fruit pineapple = new Fruit("画眉", R.drawable.ic_hm);
            fruitList.add(pineapple);
            Fruit strawberry = new Fruit("相思鸟", R.drawable.ic_xs);
            fruitList.add(strawberry);
            Fruit cherry = new Fruit("蓝知更鸟", R.drawable.ic_lzg);
            fruitList.add(cherry);
            Fruit mango = new Fruit("长尾阔嘴鸟", R.drawable.ic_cw);
            fruitList.add(mango);

        }
    }
    }



