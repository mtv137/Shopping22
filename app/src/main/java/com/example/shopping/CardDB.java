package com.example.shopping;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CardDB  extends AppCompatActivity implements View.OnClickListener {
    private Button buttonFind;
    private Button buttonDel;
    private EditText textCardFind;
    private EditText textCardDel;
    private DBCards dbCards; //база данных
    private ListView mListView; //лист для вывода записей таблицы
    private List<String> arrayList;//лист для записей базы данных
    private ListView listView;
    private ArrayAdapter<String> adapter; //адаптер для загрузки листа

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_db);                  //связь с соответствующим xml-файлом
        buttonFind=findViewById(R.id.buttonFindDB);          //инициализация кнопки поиска
        buttonDel=findViewById(R.id.buttonDel);            //инициализация кнопки удаления
        buttonFind.setOnClickListener(this);               //добавление слушателя поиска
        buttonDel.setOnClickListener(this);                //добавление слушателя удаления
        textCardFind = findViewById(R.id.editTextFind);      //инициализация текстовых полей
        textCardDel= findViewById(R.id.editTextNumber);
        dbCards=new DBCards(this);                  //инициализируем БД
        String name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        String hyperlink= getIntent().getStringExtra("hyperlink");
        Log.d("MyLog", "perehod:" +name+price+hyperlink);
        if(name!=null){ dbCards.insert(name,price,hyperlink);}

        arrayList=new ArrayList<>();
        arrayList=dbCards.selectAll();
        for (int i=0; i<arrayList.size();i++){
            Log.d("MyLog", "arrayList:" + arrayList.get(i));
        }
        listView=findViewById(R.id.list);
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayList);//затем используем адаптер
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {                //переход на сайт с описанием предмета одежды
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String stroka= (String) parent.getAdapter().getItem(position);
                String [] stroki=stroka.split("% ");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(stroki[4])));//
                startActivity(browserIntent);
            }
        });
    }

    @Override
    public void onClick(View v) {
          switch (v.getId()){ //по id нажатых кнопок определяем что должно происходить при нажатии на кнопку
          case R.id.buttonDel:
              dbCards.delete(Integer.parseInt(String.valueOf(textCardDel.getText())));
              arrayList.clear();
              arrayList=dbCards.selectAll();
              Log.d("MyLog", "new_arrayList:" + arrayList.get(0));
              Log.d("MyLog", "new_arrayList:" + arrayList.get(1));
              adapter.notifyDataSetChanged(); //не работает
               break;
              case R.id.buttonFindDB:
            //    dbCards.find(Integer.parseInt(String.valueOf(textCardFind.getText())));
                break;
        }
    }
}
