# Android进程间通信之AIDL
这是一个例子工程，主要介绍AIDL的使用和原理浅析。

AIDL是Android Interface Definition Language的缩写，也是Android接口定义语言。简单点说，AIDL是一种语言。设计这门语言的目的是为了更好的实现进程间通信，尤其是在涉及多进程并发情况下的进程间通信。还有一个目的是，简化Android开发人员的工作。
Android中一个应用（APP）一般只运行在一个进程中，但有时候由于达到某种目的也会开启多个进程。每个进程都有自己独立的虚拟机和独立的内存。如果一个应用开启了多个进程，那么进程间通信是必须的，同时，如果两个APP间通信，也属于进程间的通信。

##AIDL语法

    AIDL文件的后缀是.aidl而不是.java
    AID了默认支持一些数据类型，在使用这些数据类型的时候是不需要导包的，但是除了这些类型之外的数据类型都要导包，就算目标文件和.aidl文件在同一个包下。
      
    AIDL默认支持的数据类型有：
        Java中的八种基本数据类型，包括byte，short，int，long，float，double，boolean，char.
        String类型
        CharSequence类型
        List类型：List中的所有元素必须是AIDL支持的类型之一，或者是一个其他AIDL生成的接口，或者是定义的parcelable。可以使用泛型
        Map类型：Map中所有元素必须是AIDL支持的类型之一，或者是一个其他AIDL生成的接口，或者是定义的parcelable。Map是不支持泛型的。
        所有的AIDL接口本身
   定向tag: AIDL中的定向tag用来表示在跨进程通信中数据的流向，其中in表示数据只能由客户端流向服务端，out表示只能由服务端流向客户端，而inout则表示               既可以由客户端流向服务端，也可以从服务端流向客户端。定向tag是针对在客户端中那个传入方法的对象而言的。in为定向tag的时候表现为服务端接收一个           那个对象的完整数据，但是客户端那个对象不会因为服务端对传参的修改而发生变动；out的话，表现为服务端将接收那个对象的空对象，但是服务端对接收           到的空对象有任何的修改客户端都会同步变动；inout为定向tag的情况下，服务端会接收到客户端传来对象的完整信息，并且客户端会同步服务端对这个对象           的任何变动。     
    
    Java中的String和CharSequence的定向tag默认且只能是in。
##AIDL创建    
  AIDL的创建需要涉及两种文件，一种是aidl使用类声明文件，另一种是aidl接口文件。
    第一种类声明文件是自定义的类（例子种的Book类）需要在进程间通信使用时，需要创建一个aidl文件声明这个类，表明AIDL可以使用这个类，同时这个类应该实现Parcelable接口。
    第二类aidl接口文件，这个文件是一个接口（例子中的BookManager接口）声明在进程间通信的时候需要使用的方法。
    
    注意：AIDL文件的包名需要和类的包名完全一致。同时在AIDL中使用类时，导入类需要写完整的包名。
    
    例子：
    AIDL类声明文件：
      // Book.aidl
      package com.junxu.androidaidllearn;

      parcelable Book;//声明类
    AIDL接口文件：
      // BookManager.aidl
      package com.junxu.androidaidllearn;

      import com.junxu.androidaidllearn.Book;//类的导入需要写全包名
      // Declare any non-default types here with import statements

      interface BookManager {
          //声明方法...
      }
      
当AIDL文件编写完成之后，点击AS(Android studio)的Rebuild Project，这是，AS会自动根据AIDL文件生成一个Java接口文件，它与AIDL接口文件同名，进程间通信主要使用到这个文件。
    
  注意：在这个例子中，因为我是在一个应用中开启了多进程，所以客户端和服务端在同一个进程中。若是需要两个应用的进程间通信，则两个应用项目的工程下，它们的AIDL文件需要完全一致。
    
下面时具体示例代码分析：
     
    服务端：服务端时一个service，通过它给客户端提供服务。AIDLService继承自Service，在类里面定义一个IBinder对象，它的具体类时AIDL生成的java接口文件的内部类，这个内部类实现了IBinder接口以及。在创建这个对象的同时，重写了在AIDL接口文件中声明的方法。最后这个对象在onBind()方法中返回。
           这个Service完成之后需要在AndroidManifest文件中注册，注册时须将它的属性android:process，值不能时包名。这样这个Service将运行在新进程中。
            
    客户端：客户端时一个Activity，它通过bindService和服务端Service绑定，绑定时需要一个ServiceConnection接口对象。这个对象在创建的时候很关键，下面时它创建的代码：
     
           private ServiceConnection mServiceConnection = new ServiceConnection() {

              @Override
              public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                  Log.e(getLocalClassName(), "onServiceConnected: service connected" );
                  mBookManager = BookManager.Stub.asInterface(iBinder);//接收服务端返回的IBinder对象，也就是服务端onBind()方法返回来的那个对象
                  mBound = true;
                  if (mBookManager != null){
                      try{
                          mBooks = mBookManager.getBooks();//调用服务端的方法，获取返回结果
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
          
    在客户端，通过ServiceConnection接口对象的onServiceConnected()方法里面，通过BookManager.Stub.asInterface，就获取了服务端onBind()方法返回的IBinder对象，客户端就可以与服务端通信了。
     
    至此，客户端和服务端就可以开始通信了。
      
##AIDL原理浅析
    下面分析通信原理：其中主要使用到了AIDL文件自动生成的Java接口文件BookManager，路径是在\app\build\generated\source\aidl\debug\com\junxu\androidaidllearn。
          
    先看服务端，服务端时定义了BookManager.Stub类的对象，然后在onBind()方法返回，到这，服务端等待者客户端来绑定连接。
    再看客户端，定义了BookManager对象，用于在绑定Service的时候，接收BookManager.Stub.asInterface()方法返回的BookManager.Stub类的对象。
          
    那么通信在BookManager.java文件中是怎样的呢？
          
    在这个BookManager中，BookManager是一个接口，它继承了IInterface类。同时它还声明了AIDL接口文件BookManager.aidl中声明的方法。
          
    然后定义了一个静态抽象类Stub，它继承了Binder，实现了BookManager接口（也就是上面所说的那个BookManager接口），所以它可以以Binder的身份返回，然后在强转成BookManager。
          
    在这个Stub类里面，先是定义了一个静态不可变量DESCRIPTOR，用它标识一个Binder,直接用包名和类名赋值。
          
    它实现了BookManager接口，并且给BookManager接口中声明的方法都分配了一个常量，与之一一对应。但没有定义这些方法。
          
    然后是asInterface()方法，这个就是在客户端的时候，用来获取BookManager对象的方法。
          
          /**
           * Cast an IBinder object into an com.junxu.androidaidllearn.BookManager interface, //把IBinder对象转换成BookManager对象
           * generating a proxy if needed. //必要的时候创建一个代理对象
           */
          public static com.junxu.androidaidllearn.BookManager asInterface(android.os.IBinder obj)
          {
              if ((obj==null)) {
                  return null;
              }
              android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
              if (((iin!=null)&&(iin instanceof com.junxu.androidaidllearn.BookManager))) {
                  return ((com.junxu.androidaidllearn.BookManager)iin);
              }
              return new com.junxu.androidaidllearn.BookManager.Stub.Proxy(obj);
          }
          
          上面的asInterface()方法代码很明显，它先是进行验空，然后查找本地是否有可用的IBinder对象，如果有,转换之后返回，如果没有则创建一个代理类对象。
          
    然后是asBinder()方法，返回当前Binder对象。
          
    然后是onTransact方法，它的原型是：public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags)，在进入这个方法之前先讲Proxy这个类。
         
    Proxy类是Stub的内部类，它实现了BookManager接口，并且实现了接口的所有方法。它里面定义了一个IBinder对象mRemote，看变量名，大概知道这个变量是远程IBinder对象,它的赋值是在构造函数中，而目前接触到创建代理类对象就是在asInterface方法中，而调用这个asInterface方法是在客户端，同时传进来了一个IBinder对象，所以这里面将变量名定义为远程也就好理解了。
          
    然后在Proxy实现的接口方法中：
          android.os.Parcel _data = android.os.Parcel.obtain();
          android.os.Parcel _reply = android.os.Parcel.obtain();
        这两行代码定义了两个变量_data和_reply，_data是用来接收客户端调用服务端方法时候传过来的参数；_reply是用来接收服务端返回给客户端的数据。之所以用android.os.Parcel.obtain()获取，是因为数据存储在底层容器Parcel中。
        下面是Proxy实现BookManager接口的其中一个方法代码：
          
          public com.junxu.androidaidllearn.Book getBook() throws android.os.RemoteException
          {
              android.os.Parcel _data = android.os.Parcel.obtain();
              android.os.Parcel _reply = android.os.Parcel.obtain();
              com.junxu.androidaidllearn.Book _result;    //创建一个返回结果对象
                try {
                  _data.writeInterfaceToken(DESCRIPTOR);//看到Token，以及DESCRIPTOR，这个Binder唯一标识，感觉应该是校验对应类型的Binder。
                  mRemote.transact(Stub.TRANSACTION_getBook, _data, _reply, 0);//客户端发送请求给客户端，这个方法的参数和之前的onTransact方法，一摸一样，说明这是对应的。
                  _reply.readException(); //应该是记录异常
                  if ((0!=_reply.readInt())) {
                      _result = com.junxu.androidaidllearn.Book.CREATOR.createFromParcel(_reply);//从底层Parcel容器获取数据构建返回对象。
                  }
                  else {
                      _result = null;
                  }
                }
                finally {
                  _reply.recycle();
                  _data.recycle();
                }
              return _result;
          }
          
       方法中，应该是先从底层容器Parcel中取出数据，然后在传入参数中，加入Binder的唯一标识供服务端校验Binder，接着由客户端远程IBinder对象调用transact方法像服务端发送请求。然后在_reply中记录返回的数据。因为是进程间通信，所以返回对象数据从底层Parcel中获取。
          
       再回来看一下刚才的onTransact方法，这个时候就可以知道这个方法是真没时候调用的了。当服务端接收到transact()方法的请求的时候就会调用这个方法了。
          
       它的原型是：public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags)。
              code:是一个整形，这个参数用以告知客户端想调用那个方法。onTransact方法是在Stub中定义的，而在Stub类中，给BookManager接口定义的每个方法都分配了一一对应的整形常量。所以可以通过这个code参数，告知服务端调用那个方法，这样更简单。
              data中:对应了transact方法中的_data参数。
              reply:对应了transact方法中的_reoly参数。
              flags:这个参数作用是设置进行 IPC 的模式，为 0 表示数据可以双向流通，即 _reply 流可以正常的携带数据回来，如果为 1 的话那么数据将只能单向流通，从服务端回来的 _reply 流将不携带任何数据。 
       下面是onTransact方法方法中客户端调用getBook()方法的响应部分：
            switch (code)
            {
               case ...
               case TRANSACTION_getBooks://TRANSACTION_getBooks这是Stub类定义的与getBook()方法一一对应的数值
               {
                  data.enforceInterface(DESCRIPTOR);//
                  java.util.List<com.junxu.androidaidllearn.Book> _result = this.getBooks();//调用方法获取返回值
                  reply.writeNoException();
                  reply.writeTypedList(_result);//结果写于reply返回给客端
                  return true;
                }
                ...
            }
       上面的代码将onTransact所做的工作已经大概的展示出来了。
       
       AIDL的原理分析到这。尽管不是很详细，底层的东西并没有讲到，但是整个流程算是走了一遍，对AIDL的工作原理算是有了跟进一步的了解。
             
     
