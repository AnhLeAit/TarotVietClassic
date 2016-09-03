 Welcome to Tarot Việt!
===================
Hi guys!
By some policy reasons the app is now no longer available at Google Play. So I decided to OPEN this source for anyone who love Tarot and want to contribute more this project can do more interested thing base on this source.

![](SourceCode/ic_launcher-web.png)


##Code overview.

This app using JSON to store static data about the Tarot card and save at asset folder but it has encrypted, don't worry you can get password inside this source. 

Keep in mind that I have loaded card images with very big size into memory so I have to handle more about loading and manage bitmap that fit size when showing in the screen just using LruCache and DishLruCache - basic cache system provided by Android without any modem library like [Glide](https://github.com/bumptech/glide) or [Fresco](https://github.com/facebook/fresco)…

The UI I have tried to make it fit multiple screen size both Phone and Tablet. It compatible with Phone screen tablet 7-inch and even 10-inch 

##Download install APK
You can download a work apk [HERE](PushlishInfo/TarotViet.apk)

-----
Hope you have more fun when improvement and contribute to made it better. Thanks!


======================
## MIT License
Copyright (c) 2013 Le Ngoc Anh

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

======================
## Release notes
Version 1.0

Initial release