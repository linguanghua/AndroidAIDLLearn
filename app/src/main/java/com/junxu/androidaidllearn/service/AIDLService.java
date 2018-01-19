package com.junxu.androidaidllearn.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.junxu.androidaidllearn.Book;
import com.junxu.androidaidllearn.BookManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Linxu on 2018-1-16.
 */

public class AIDLService extends Service {
    private static final String TAG = "AIDLService";
    private List<Book> mBooks = new ArrayList<>();
    private BookManager.Stub mBookManager = new BookManager.Stub() {
        @Override
        public List<Book> getBooks() throws RemoteException {
            synchronized (this){
                Log.d(TAG,"invoking getBooks() method,now the list is:"+mBooks.toString());
                if (mBooks != null){
                    return mBooks;
                }
                return new ArrayList<>();
            }
        }

        @Override
        public Book getBook() throws RemoteException {
            return null;
        }

        @Override
        public int getBookCount() throws RemoteException {
            return 0;
        }

        @Override
        public void setBookPrice(Book book, int price) throws RemoteException {

        }

        @Override
        public void setBookName(Book book, String name) throws RemoteException {

        }

        @Override
        public void addBookIn(Book book) throws RemoteException {
            synchronized (this){
                if (mBooks == null){
                    mBooks = new ArrayList<>();
                }
                if (book == null){
                    Log.d(TAG,"book is null in In");
                    book = new Book();
                }
                book.setPrice(1232);
                if (!mBooks.contains(book)){
                    mBooks.add(book);
                }
                Log.d(TAG, "addBookIn: invoking addBooks() method,new list is:"+mBooks.toString());
            }
        }

        @Override
        public void addBookOut(Book book) throws RemoteException {

        }

        @Override
        public void addBookInOut(Book book) throws RemoteException {

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Book book = new Book();
        book.setName("Android开发艺术探索");
        book.setPrice(50);
        mBooks.add(book);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(getClass().getSimpleName(), String.format("on bind,intent = %s", intent.toString()));
        return mBookManager;
    }
}
