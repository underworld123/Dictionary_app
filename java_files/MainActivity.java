package com.example.dictionary_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper mydb; EditText ed;
    Button btn; ListView lst;
    ArrayList<String> array=new ArrayList<>();
    ArrayList<String> array1=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydb = new DatabaseHelper(this);
        btn = findViewById(R.id.Search);
        ed= findViewById(R.id.edittext);
        lst= findViewById(R.id.lstvw);
        lst.setVisibility(View.GONE);

        Cursor res =mydb.getsuggestions();
        array.clear();
        while(res.moveToNext()){
            String x=res.getString(1);
            array.add(x);
        }

        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int  ount, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str=ed.getText().toString();
                if(str.length()==0){
                    lst.setVisibility(View.GONE);
                }
                else {
                    array1.clear();
                    lst.setVisibility(View.VISIBLE);
                    for(int i=0;i<array.size();i++)
                    {
                        if(array.get(i).toLowerCase().startsWith(str.toLowerCase()))
                        {
                            array1.add(array.get(i));
                        }
                    }
                    if((ArrayAdapter<String> )lst.getAdapter()!=null){
                        ((ArrayAdapter<String> )lst.getAdapter()).notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                String value = (String)adapter.getItemAtPosition(position);
                ed.setText(value);
                lst.setVisibility(View.GONE);
            }
        });

        ArrayAdapter<String> ad=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,array1);
        ad.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        lst.setAdapter(ad);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor res =mydb.getdata(ed.getText().toString());
                Log.i("main",Integer.toString(res.getCount()));
                if(res.getCount()==0){
                    showMsg("Error","Nothing found");
                }
                else{
                    StringBuffer buffer = new StringBuffer();
                    while(res.moveToNext()){
                        buffer.append("ID : "+res.getString(0)+"\n");
                        buffer.append("Word : "+res.getString(1)+"\n");
                        buffer.append("Meaning : "+res.getString(2)+"\n");
                    }
                    showMsg("Data",buffer.toString());
                }
            }
        });
    }

    public void showMsg(String title,String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.show();
    }
}
