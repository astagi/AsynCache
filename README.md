AsynCache
=========
A light asynchronous callback-based cache manager for Android

Usage
-----
Note: all write/read operations are made outside of your app’s main UI thread and any callback logic will be executed on the same thread.

An example to write an entry in your cache:
```
    AsynCache.getInstance().write(context, "hello", "Hello!", 
        new AsynCache.WriteResponseHandler() {

            @Override
            public void onSuccess() {
                //Do Something
            }

            @Override
            public void onFailure(Throwable t) {
                
            }
            
        });
```
Write accepts a String or an array of bytes.

An example to read an entry in your cache:
```
    AsynCache.getInstance().read(context, "hello", 
        new AsynCache.ReadResponseHandler() {

            @Override
            public void onSuccess(byte[] data) {
                //Do Something
            }

            @Override
            public void onFailure(Throwable t) {
                
            }
            
        });
```

-------
Relased under MIT license, Copyright (c) 2013 Andrea Stagi <stagi.andrea@gmail.com>