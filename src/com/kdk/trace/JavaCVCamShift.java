package com.kdk.trace;

import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.cpp.opencv_highgui.CvCapture;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_video.*;


public class JavaCVCamShift{
	IplImage frame, image , hsv , hue , mask , backproject , histimg ;
	IplImage[] imageArray;
	//��HSV�е�Hue�������и���
	CvHistogram hist ;
	//ֱ��ͼ��
	int x1=0,y1=0,x2=0,y2=0;//ѡȡ���������
	int backproject_mode = 0;
	int select_object = 0;
	int track_object = 0;
	int show_hist = 1;
	CvPoint origin;
	CvPoint  cp1,cp2;
	CvRect selection;
	CvRect track_window;
	CvBox2D track_box;
	float[] max_val=new float[1];
	int[] hdims = {16};
	//����ֱ��ͼbins�ĸ�����Խ��Խ��ȷ
	float[][] hranges_arr = {{0,180}};
	//����ֵ�ķ�Χ
	float[][] hranges = hranges_arr;
	//���ڳ�ʼ��CvHistogram��
	
	CvConnectedComp track_comp;
	
	public JavaCVCamShift()
	{    
		 imageArray=new IplImage[1];
		 CvCapture capture= cvCreateCameraCapture(0);
		 cvNamedWindow("imageName",CV_WINDOW_AUTOSIZE); 
		 Pointer pointer=null;
		 cvSetMouseCallback("imageName",new mouseClike(),pointer);
		 track_comp=new CvConnectedComp();
		 while(true)
		 {
			 frame=cvQueryFrame(capture);
			 if(frame==null)break;
			 
			 if( image==null )
			 //imageΪ0,�����տ�ʼ��δ��image������,�Ƚ���һЩ������
			   {
			      image = cvCreateImage( cvGetSize(frame), 8, 3 );
			      image.origin(frame.origin());
			      hsv = cvCreateImage( cvGetSize(frame), 8, 3 );
			      hue = cvCreateImage( cvGetSize(frame), 8, 1 );
			      mask =cvCreateImage( cvGetSize(frame), 8, 1);
			      //������Ĥͼ��ռ�
			      backproject = cvCreateImage( cvGetSize(frame), 8, 1 );
			      //���䷴��ͶӰͼ�ռ�,��Сһ��,��ͨ��
			      hist = cvCreateHist( 1, hdims, CV_HIST_ARRAY, hranges, 1 );
			      //����ֱ��ͼ�ռ�	 
			    } 
			   cvCopy(frame,image);
			   cvCvtColor( image, hsv, CV_BGR2HSV );  
			   if( track_object !=0)
			        //track_object����,��ʾ����Ҫ���ٵ�����
			        {
				   double _vmin = 10.0, _vmax = 256.0,smin=30.0;
	               
		            cvInRangeS( hsv, cvScalar(0.0,smin,Math.min(_vmin,_vmax),0.0), cvScalar(180.0,256.0,Math.max(_vmin,_vmax),0.0), mask );
		            //��ֻ��������ֵΪH��0~180��S��smin~256��V��vmin~vmax֮��Ĳ���������Ĥ��
		            cvSplit( hsv, hue, null, null, null );
					//����H���� 
		            imageArray[0]=hue;           
			   if( track_object < 0 )
		        //�����Ҫ���ٵ����廹û�н���������ȡ�������ѡȡ�����ͼ��������ȡ
		            {   
		                cvSetImageROI( imageArray[0],selection );
		                //����ԭѡ���ΪROI
		                cvSetImageROI( mask,selection );
		                //������Ĥ��ѡ���ΪROI
		                cvCalcHist( imageArray,hist,0,mask );
		                //�õ�ѡ�������������Ĥ���ڵ�ֱ��ͼ 
		                cvGetMinMaxHistValue( hist, null, max_val, null, null );
		                cvConvertScale( hist.bins(), hist.bins(),max_val[0]>0 ? (double)255/ max_val[0]:0.0,0 );
		                // ��ֱ��ͼ����ֵתΪ0~255
		                cvResetImageROI( imageArray[0] );
		                //ȥ��ROI
		                cvResetImageROI( mask );
		                //ȥ��ROI
                         track_window = selection;
		                track_object = 1;
						//��track_objectΪ1,����������ȡ���  
		            }  
			   cvCalcBackProject( imageArray, backproject, hist );
	            //����hue�ķ���ͶӰͼ
	            cvAnd( backproject, mask, backproject, null );
	            //�õ���Ĥ�ڵķ���ͶӰ                 
	            cvCamShift(backproject, track_window,
	                        cvTermCriteria( CV_TERMCRIT_EPS | CV_TERMCRIT_ITER, 10, 1 ),
	                        track_comp,track_box);
	            //ʹ��MeanShift�㷨��backproject�е����ݽ�������,���ظ��ٽ��   
	            track_window = track_comp.rect();
	            //�õ����ٽ���ľ��ο�
				cp1=cvPoint(track_window.x(),track_window.y());
				cp2=cvPoint(track_window.x()+track_window.width(),track_window.y()+track_window.height());			
				if( image.origin()>0 )
		        track_box.angle(-track_box.angle());
				cvRectangle(frame,cp1,cp2, CV_RGB(0,255,0),3,CV_AA,0);	
				
			        } 	
			  if( select_object==1 && selection.width() > 0 && selection.height() > 0 )
			        //�������������ѡ�񣬻���ѡ���
			        {
			            cvSetImageROI( frame, selection );
			            cvXorS(frame,cvScalarAll(255),frame,null );
			            cvResetImageROI( frame );
			        }	 
			 cvShowImage("imageName",frame);
			 int c=cvWaitKey(33);
			 if(c==27) break;		 
		 }
		 cvReleaseCapture(capture);
		 cvDestroyWindow("imageName"); 	
	}
	public static void main(String[] args) {
		//System.out.println(System.getProperty("java.library.path"));
        new JavaCVCamShift();
	}
	
	
class mouseClike extends CvMouseCallback
{
	 public void call(int event,int x, int y,int flags, Pointer param)
	//���ص�����,�ú����������и���Ŀ���ѡ��
	{
	    if( image==null )
	        return;	     
	    if( image.origin()!=0 )
	        y = image.height() - y;
	    //���ͼ��ԭ������������,�����Ϊ����
		
	     if( select_object==1 )
	    //select_objectΪ1,��ʾ����������Ŀ��ѡ��
	    //��ʱ�Ծ�����selection�õ�ǰ�����λ�ý�������
	    {
	        selection.x(Math.min(x,origin.x()));
	        selection.y(Math.min(y,origin.y()));  
	        selection.width(selection.x() + Math.abs(x - origin.x()));
	        selection.height(selection.y() + Math.abs(y - origin.y()));  
	        selection.x(Math.max(selection.x(),0));
	        selection.y(Math.max(selection.y(),0 )); 
	        selection.width(Math.min( selection.width(), image.width() ));
	        selection.height(Math.min( selection.height(), image.height()));
	        selection.width(selection.width()-selection.x());
	        selection.height( selection.height()-selection.y());   
	    }
	    switch( event )
	    {
	    case CV_EVENT_LBUTTONDOWN:
	    	//��갴��,��ʼ���ѡ���������
	        origin = cvPoint(x,y);
	        selection = cvRect(0,0,0,0);
	        select_object = 1;
	        break;
	    case CV_EVENT_LBUTTONUP:
	    	//����ɿ�,���ѡ���������
	        select_object = 0;
	        if( selection.width() > 0 && selection.height() > 0 )
	        //���ѡ��������Ч����򿪸��ٹ���
	        track_object = -1;
	       // System.out.println("x:"+selection.x()+"y:"+selection.y()+"width:"+selection.width()+"heigth:"+selection.height());
	        break;
	    }
	}
	
}


}

