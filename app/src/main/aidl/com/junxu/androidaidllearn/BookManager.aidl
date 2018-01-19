// BookManager.aidl
package com.junxu.androidaidllearn;

import com.junxu.androidaidllearn.Book;
// Declare any non-default types here with import statements

interface BookManager {
    List<Book> getBooks();
    Book getBook();

    int getBookCount();

    void setBookPrice(in Book book,int price);
    void setBookName(in Book book,String name);
    void addBookIn(in Book book);
    void addBookOut(out Book book);
    void addBookInOut(inout Book book);
}
