package com.junxu.androidaidllearn;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BookManager mBookManager;
    private boolean mBound = false;
    private List<Book> mBooks;

    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.btn_start_service);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBook();
            }
        });
    }

    public void addBook(){
        if (!mBound){
            attemptToBindService();
            Toast.makeText(this,"当前与服务器端处于未连接状态，正在尝试重连，请稍后再试",Toast.LENGTH_LONG).show();
            return;
        }
        if (mBookManager == null) return;

        Book book = new Book();
        book.setName("App研发录In");
        book.setPrice(30);
        try{
            mBookManager.addBookIn(book);
            Log.e(getLocalClassName(), "addBook: "+book.toString());
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

    private void attemptToBindService(){
        Intent intent = new Intent();
        intent.setAction("com.junxu.androidaidllearn");
        intent.setPackage("com.junxu.androidaidllearn");
        bindService(intent,mServiceConnection,BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBound){
            attemptToBindService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound){
            unbindService(mServiceConnection);
            mBound = false;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e(getLocalClassName(), "onServiceConnected: service connected" );
            mBookManager = BookManager.Stub.asInterface(iBinder);
            mBound = true;
            if (mBookManager != null){
                try{
                    mBooks = mBookManager.getBooks();
                    Log.e(getLocalClassName(), "onServiceConnected: "+mBooks.toString());
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(getLocalClassName(), "onServiceDisconnected: service disconnected" );
            mBound =false;
        }
    };
}
