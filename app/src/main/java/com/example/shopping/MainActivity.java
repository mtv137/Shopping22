package com.example.shopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Document doc;
    private Thread secThread;
    private Runnable runnable;

    private ListView listView;
    private EditText findSubString;
    private Button buttonDb;
    private Button buttonFind;
    private CustomArrayAdapter adapter; //адаптер для отображения// листа

    private List<ListItemDress>  arrayList; //лист загруженных с сайта вещей
    private List<ListItemDress> arrayListNew; //???


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        buttonFind=findViewById(R.id.buttonFind);           //инициализация кнопки поиска
        buttonDb=findViewById(R.id.buttonBd);           //инициализация кнопки перехода к БД
        buttonFind.setOnClickListener(this);                //добавление слушателя
        buttonDb.setOnClickListener(this);                //добавление слушателя
        findSubString=findViewById(R.id.findSubStr);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//обработка события нажатия на строку списка
                ListItemDress stroka= (ListItemDress) parent.getAdapter().getItem(position);
                Log.d("MyLog", "position + "+position + ", id = " + id+"ListItemDress:"+stroka.getTitleDress()+stroka.getPriceDress()+stroka.getHyperlinkDress());
                Intent intent=new Intent(parent.getContext(),CardDB.class);
                intent.putExtra("name", stroka.getTitleDress()); // указываем первым параметром ключ, а второе значение
                intent.putExtra("price", stroka.getPriceDress());// по ключу мы будем получать значение с Intent
                intent.putExtra("hyperlink", stroka.getHyperlinkDress());
                startActivity(intent);// запускаем новое Activity
            }
        });
    }

    @Override
    public void onClick(View v) { //метод обработки события нажатия на кнопку
        switch (v.getId()){ //по id нажатых кнопок определяем что должно происходить при нажатии на кнопку
            case R.id.buttonBd:
                Intent intent1=new Intent(this,CardDB.class); //формируем намерение перейти в другой Activity
                startActivity(intent1);
                break;
            case R.id.buttonFind:
                arrayListNew=search(arrayList, String.valueOf(findSubString.getText()));
                arrayList.clear();
                arrayList=arrayListNew; // корень зла тут
               /* arrayListNew.clear();
                ListItemDress a=new ListItemDress();
                a.setTitleDress("aaa");
                a.setPriceDress("111");
                a.setHyperlinkDress("hhh");
                arrayListNew.add(a);
                arrayList=arrayListNew;*/
                adapter.notifyDataSetChanged();
                for (int i=0; i<arrayList.size();i++){
                    Log.d("MyLog", "cearchArrayList:" + arrayList.get(i).getTitleDress() + arrayList.get(i).getPriceDress() + arrayList.get(i).getHyperlinkDress());
                }
                break;
        }
    }

    private void init(){//выгрузка данных новом потоке
        listView=findViewById(R.id.listview);
        arrayList=new ArrayList<>();
        adapter =new CustomArrayAdapter(this,R.layout.list_item_1,arrayList,getLayoutInflater());
        listView.setAdapter(adapter);
        runnable=new Runnable() {
            @Override
            public void run() {
                getWeb();
            }
        };
        secThread=new Thread(runnable);//создаем новый поток, чтобы загрузить данные из сайта
        secThread.start();//запускаем поок
        //adapter.notifyDataSetChanged();//обновление     //лишнее
    }
    private void getWeb(){//выгрузка данных из сайта
        try {
            doc= Jsoup.connect("https://happywear.ru/zhenshchinam/odezhda").get();
            Log.d("MyLog","Title:"+doc.title());
            Elements dressCatalogs=doc.getElementsByClass("catalog__center");//считывание данных
            Element dressCatalog=dressCatalogs.get(0);
            Elements dressCards=dressCatalog.children(); //карточки каталога
            for (int i=0; i<dressCatalog.childrenSize()-4;i++) {
                Element dressCard = dressCards.get(i);//1-я карточка каталога
                Log.d("MyLog", "Card:" + dressCard.text());
                Elements inDressCards = dressCard.children(); //внутренние карточки
                Element inDressCard = inDressCards.get(0);//1-я внутренняя карточка каталога
                Log.d("MyLog", "InCard:" + inDressCard.text());
                Elements elementsInDressCards = inDressCard.children(); //внутренние карточки
                /////Название
                Element titleDress = elementsInDressCards.get(2);//элемент с названием
                String basicTitleDress = titleDress.text();// название платья
                Log.d("MyLog", "TitleDress:" + basicTitleDress);
                /////Ссылка
                String hyperlinkDress = titleDress.attributes().toString().substring(7,titleDress.attributes().toString().length()-29);
                Log.d("MyLog", "TitleDress:" + hyperlinkDress);
                /////Цена
                Element price = elementsInDressCards.get(3);//контейнер с ценой
                Log.d("MyLog", "Prices:" + price.text());
                Elements priceElement = price.children(); //цены контейнера с ценой
                Element priceRetail = priceElement.get(1);//цена в розницу с пояснением
                Log.d("MyLog", "PriceRetail:" + priceRetail.text());
                Elements priceRetailElement = priceRetail.children(); //элементы контейнера с розничной ценой
                Element priceRetailValue = priceRetailElement.get(0);//цена в розницу с пояснением
                String priceDress=priceRetailValue.text();
                Log.d("MyLog", "PriceRetailValue:" + priceDress);
                Log.d("MyLog", "i:" + i);
                Log.d("MyLog", "size:" + dressCatalog.childrenSize());
                /////Фото
                // Element photoDress=elementsInDressCards.get(0);//элемент с фотографией
                // Elements basicPhotoDress_s=photoDress.children();//элементы с фотографией
                // Element basicPhotoDress=basicPhotoDress_s.get(0);//блок с основной фотографией, но до самой фотографии не дошла, так как нужно перейти еще на одну страницу.
                ListItemDress itemDress=new ListItemDress();
                itemDress.setTitleDress(basicTitleDress);
                itemDress.setPriceDress(priceDress);
                itemDress.setHyperlinkDress(hyperlinkDress);

                arrayList.add(itemDress);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String DresstoString(ListItemDress dress){
        String s="";
        s="% "+dress.getTitleDress()+"% "+dress.getPriceDress()+"% "+dress.getHyperlinkDress();
        return s;
    }

    private static List<ListItemDress> search(List<ListItemDress> list, String substr) {//поиск
        ArrayList arr=new ArrayList();

        for (int index = 0; index < list.size(); index++) {
            String strDress="";
            strDress=strDress+list.get(index).getTitleDress()+list.get(index).getPriceDress()+list.get(index).getHyperlinkDress();
            for(int i=0;i<strDress.length()-substr.length();i++) {
                if (strDress.substring(i,i+substr.length()).equals(substr)){
                    arr.add(list.get(index));}
            }
        }
        return arr;
    }
}