package csc436.trevor.csc436.Screens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import csc436.trevor.csc436.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import csc436.trevor.csc436.R;

public class SearchScreen extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;

    public class Item{
        boolean checked;
        String ItemString;
        Item(String t, boolean b){
            ItemString = t;
            checked = b;
        }

        public boolean isChecked(){
            return checked;
        }

        public String getString(){
            return ItemString;
        }
    }

    static class ViewHolder{
        CheckBox checkBox;
        TextView text;
    }

    public class ItemsListAdapter extends BaseAdapter {
        private Context context;
        private List<Item> list;
        ItemsListAdapter(Context c, List<Item> l){
            context = c;
            list = l;
        }

        @Override
        public int getCount(){
            return list.size();
        }

        @Override
        public Object getItem(int postion){
            return  list.get(postion);
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        public boolean isChecked(int position){
            return list.get(position).checked;
        }

        public String getString(int position) {
            return list.get(position).ItemString;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            View rowView = convertView;

            //reuse views
            ViewHolder viewHolder = new ViewHolder();
            if(rowView == null){
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                rowView = inflater.inflate(R.layout.check_box_list, null);

                viewHolder.checkBox = (CheckBox) rowView.findViewById(R.id.rowCheckBox);
                viewHolder.text = (TextView) rowView.findViewById(R.id.rowTextView);
                rowView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) rowView.getTag();
            }

            viewHolder.checkBox.setChecked(list.get(position).checked);

            final String itemStr = list.get(position).ItemString;
            viewHolder.text.setText(itemStr);

            viewHolder.checkBox.setTag(position);

            viewHolder.checkBox.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    boolean newState = !list.get(position).isChecked();
                    list.get(position).checked = newState;
                }
            });

            viewHolder.checkBox.setChecked(isChecked(position));

            return rowView;
        }
    }

    Button btnSearch;
    List<Item> ingredients;
    ListView listView;
    ItemsListAdapter myItemsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);
        mDatabaseHelper = new DatabaseHelper(this);

        listView = (ListView)findViewById(R.id.ingredientList);
        btnSearch = (Button)findViewById(R.id.button3);

        initItems();
        myItemsListAdapter = new ItemsListAdapter(this, ingredients);
        listView.setAdapter(myItemsListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> ing = new ArrayList<String>();
                String choices = "";
                Bundle extras = new Bundle();
                for(int i=0; i<ingredients.size(); i++){
                    if(ingredients.get(i).isChecked()){
                        choices = choices + ingredients.get(i).getString() + ",";
                    }
                }
                extras.putString("choices",choices);
                Intent i = new Intent(SearchScreen.this,SearchResultsScreen.class);
                i.putExtras(extras);
                startActivity(i);
            }
        });
    }

    private void initItems(){
        ingredients = new ArrayList<Item>();

        Cursor data = mDatabaseHelper.getData();
        // Each item in Inventory Database is added to a String ArrayList called listData
        //ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            String s = data.getString(1);
            boolean b = false;
            Item item = new Item(s,b);
            ingredients.add(item);
        }

    }

    public void onBackPressed()
    {
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
    }
}