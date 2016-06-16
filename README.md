# JavaCV
该项目基于JavaCV开发，实现了目标实时追踪。
JavaCV是对各种常用计算机视觉库的封装后的一组jar包，其中封装了OpenCV、libdc1394、OpenKinect、videoInput和ARToolKitPlus
等计算机视觉编程人员常用库的接口，可以通过其中的utility类方便的在包括Android在内的Java平台上调用这些接口。
#Dependencies
* [JavaCV for Windows](http://www.softpedia.com/get/Programming/Other-Programming-Files/JavaCV.shtml)
* [OpenCV for windowS](http://www.softpedia.com/get/Programming/Other-Programming-Files/JavaCV.shtml)  

#Starting the code
直接下载代码并在Eclipse导入项目，或者通过在Eclipse中clone项目。项目需要依赖Javacpp、Javacv、
javacv-window-86、opencv-2.4.4-window-x86四个jar包。需在项目build path中配置好。  
**该项目需要在支持摄像头的电脑中运行**。  

#How to perform tracking
在Eclipse中运行程序，启动摄像头后，用鼠标框住所要追踪的物体，松开鼠标后，摄像头就会实时追踪所选的物体。  
![tracker](http://7xrn7f.com1.z0.glb.clouddn.com/16-6-16/39941553.jpg)
