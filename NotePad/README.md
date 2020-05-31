## 期中实验：Notepad笔记

#### 一：时间戳显示

1.NoteList中显示条目增加时间戳显示修改noteslist_item.xml中的样式，增加要显示时间戳的TextView。**

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    
    <TextView xmlns:android="http://schemas.android.com/apk/res/android"
        android:id="@android:id/text1"
        android:layout_width="match_parent"
        android:layout_height="40dp"
        android:textAppearance="?android:attr/textAppearanceLarge"
        android:gravity="center_vertical"
        android:paddingLeft="5dip"
        android:textColor="@color/colorPrimaryDark"
        android:singleLine="true"
        android:textSize="25dp"
        />
    <!--添加 显示时间 的TextView-->
    <TextView
        android:id="@+id/times"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textAppearance="?android:attr/textAppearanceSmall"
        android:paddingLeft="5dip"
        android:textColor="@color/OrangeRed"
        android:textSize="15dp"/>d

</LinearLayout>
```

##### 2.NotesList.java PROJECTION 契约类的变量值加一列，显示时间。

```
private static final String[] PROJECTION = new String[] {
            NotePad.Notes._ID, 
            NotePad.Notes.COLUMN_NAME_TITLE,
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, //  时间
    };
```

##### 3.修改SimpleCursorAdapter的dataColumns和viewIDs的相关值。

```
    private String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE ,  NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE } ;
    private int[] viewIDs = { android.R.id.text1 , R.id.times };
```

##### 4.NotePadProvider和NoteEditor中改动时间戳格式。

```
		//修改时间
        Long now = Long.valueOf(System.currentTimeMillis());
        Date date = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String dateTime = format.format(date);
        values.put(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, dateTime)
```

![1590846088909](../image/26.png)

#### 二、添加笔记查询功能（根据标题查询）

##### 1.list_options_menu.xml文件，添加一个搜索的item

```
      <item
        android:id="@+id/menu_search"
        android:title="Search"
        android:icon="@android:drawable/ic_search_category_default"
        android:showAsAction="always"
       />
```



#### ![1590846170468](../image/27.png)

##### 2.onOptionsItemSelected中在switch中添加搜索的case语句

```
            //添加搜索
            case R.id.menu_search:
                Intent intent = new Intent();
                intent.setClass(NotesList.this,NoteSearch.class);
                NotesList.this.startActivity(intent);
                return true;
```

##### 3.新建一个activity用来跳转的搜索界面

note_search_list.xml

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent">

    <SearchView
        android:id="@+id/search_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:iconifiedByDefault="false"
        android:queryHint="输入搜索内容..."
        android:layout_alignParentTop="true">
    </SearchView>

    <ListView
        android:id="@android:id/list"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
    </ListView>

</LinearLayout>
```

NoteSearch.java

```
package com.example.mynotepad;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

public class NoteSearch extends ListActivity  implements SearchView.OnQueryTextListener {

    private static final String[] PROJECTION = new String[]{
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, // 1
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, // 时间
            NotePad.Notes.COLUMN_NAME_BACK_COLOR //颜色
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_search_list);
        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(NotePad.Notes.CONTENT_URI);
        }
        SearchView searchview = (SearchView) findViewById(R.id.search_view);
        searchview.setOnQueryTextListener(NoteSearch.this);  //为查询文本框注册监听器
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        String selection = NotePad.Notes.COLUMN_NAME_TITLE + " Like ? ";

        String[] selectionArgs = {"%" + newText + "%"};

        Cursor cursor = managedQuery(
                getIntent().getData(),            // Use the default content URI for the provider.
                PROJECTION,                       // Return the note ID and title for each note. and modifcation date
                selection,                        // 条件左边
                selectionArgs,                    // 条件右边
                NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
        );

        String[] dataColumns = {NotePad.Notes.COLUMN_NAME_TITLE, NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE};
        int[] viewIDs = {android.R.id.text1, R.id.text2};

        MyCursorAdapter adapter = new MyCursorAdapter(
                this,
                R.layout.noteslist_item,
                cursor,
                dataColumns,
                viewIDs
        );
        setListAdapter(adapter);
        return true;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        // Constructs a new URI from the incoming URI and the row ID
        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);

        // Gets the action from the incoming Intent
        String action = getIntent().getAction();

        // Handles requests for note data
        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {

            // Sets the result to return to the component that called this Activity. The
            // result contains the new URI
            setResult(RESULT_OK, new Intent().setData(uri));
        } else {

            // Sends out an Intent to start an Activity that can handle ACTION_EDIT. The
            // Intent's data is the note ID URI. The effect is to call NoteEdit.
            startActivity(new Intent(Intent.ACTION_EDIT, uri));
        }
    }
```

### 

三、笔记界面

```
<LinearLayout 
xmlns:android="http://schemas.android.com/apk/res/android"    android:layout_width="wrap_content"     
android:layout_height="wrap_content"    
android:orientation="vertical"    
android:paddingLeft="6dip"    
android:paddingRight="6dip"    
android:paddingBottom="3dip"    
android:background="@color/colorBlue">                         
<EditText android:id="@+id/title"         
android:maxLines="1"         
android:layout_marginTop="2dp"        
android:layout_marginBottom="15dp"        
android:layout_width="wrap_content"       
android:ems="25"        
android:layout_height="wrap_content"         
android:autoText="true"        
android:capitalize="sentences"        
android:scrollHorizontally="true"        android:background="@color/colorBlue"/>    
<Button android:id="@+id/ok"        
android:layout_width="wrap_content"         android:layout_height="wrap_content"         
android:layout_gravity="right"        
android:text="@string/button_ok"        
android:onClick="onClickOk"        /></LinearLayout>
```



![1590934979658](../image/28.png)

