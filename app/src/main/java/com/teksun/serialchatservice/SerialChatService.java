package com.teksun.serialchatservice;

import android.app.Instrumentation;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

public class SerialChatService extends Service {

     protected SerialPort mSerialPort;
     protected OutputStream mOutputStream;
     private InputStream mInputStream;
     private ReadThread mReadThread;

     private class ReadThread extends Thread {
         byte[] buffer = new byte[1024];

         @Override
         public void run() {
             super.run();
             while (!isInterrupted()) {
                 int size;
                 try {

                     if (mInputStream == null)
                         return;
                     size = mInputStream.read(buffer);
                     if (size > 0) {
                         onDataReceived(buffer, size);
                     }
                 } catch (IOException e) {
                     e.printStackTrace();
                     return;
                 }
             }
         }
     }


    /**
     * 绑定服务时才会调用
     * 必须要实现的方法
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    /**
     * 首次创建服务时，系统将调用此方法来执行一次性设置程序（在调用 onStartCommand() 或 onBind() 之前）。
     * 如果服务已在运行，则不会调用此方法。该方法只被调用一次
     */
    @Override
    public void onCreate() {
        System.out.println("onCreate invoke");

         try {
             mSerialPort = new SerialPort(new File("/dev/ttyMT0"), 9600, 0);
             mOutputStream = mSerialPort.getOutputStream();
             mInputStream = mSerialPort.getInputStream();

             mReadThread = new ReadThread();
             mReadThread.start();
         } catch (Exception e) {
             e.printStackTrace();
         }

        super.onCreate();
    }

    protected abstract void onDataReceived(final byte[] buffer, final int size);
    /**
     * 每次通过startService()方法启动Service时都会被回调。
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
     @Override
     public int onStartCommand(Intent intent, int flags, int startId) {
         System.out.println("onStartCommand invoke");
         return super.onStartCommand(intent, flags, startId);
     }
     /**
     * 服务销毁时的回调
     */
     @Override
     public void onDestroy() {
         System.out.println("onDestroy invoke");

         if (mReadThread != null){
             mReadThread.interrupt();
         }
         if (mSerialPort != null) {
             mSerialPort.close();
             mSerialPort = null;
         }
         mSerialPort = null;

         super.onDestroy();
     }





/*
    private void DispRecData(ComBean ComRecData) {
        StringBuilder sMsg = new StringBuilder();
        sMsg.append(ComRecData.sRecTime);
        sMsg.append("[");
        sMsg.append(ComRecData.sComPort);
        sMsg.append("]");
        sMsg.append("[Hex] ");
        sMsg.append(MyFunc.ByteArrToHex(ComRecData.bRec));
        sMsg.append("\r\n");

        //接收串口的数据返回值，并且做相应的处理

        //editTextRecDisp.append(sMsg);
        String[] temp = MyFunc.StrToStrArray(MyFunc.ByteArrToHex(ComRecData.bRec));

        if (temp.equals("55 AA 10 01 00 11")) {
            //开关机键
            sendKeyEvent(26);
        }else if (temp.equals("55 AA 16 02 00 18")) {
            //静音键
            sendKeyEvent(164);
        }else if (temp.equals("55 AA 16 05 00 1B")) {
            //上键
            sendKeyEvent(19);
        }else if (temp.equals("55 AA 16 06 00 1C")) {
            //下键
            sendKeyEvent(20);
        }else if (temp.equals("55 AA 16 07 00 1D")) {
            //左键
            sendKeyEvent(21);
        }else if (temp.equals("55 AA 16 08 00 1E")) {
            //右键
            sendKeyEvent(22);
        }else if (temp.equals("55 AA 16 09 00 1F")) {
            //OK键
            sendKeyEvent(66);
        }else if (temp.equals("55 AA 16 0A 00 20")) {
            //返回键
            sendKeyEvent(4);
        }else if (temp.equals("55 AA 16 0B 00 21")) {
            //home键
            sendKeyEvent(3);
        }else if (temp.equals("55 AA 16 0C 00 22")) {
            //menu键
            sendKeyEvent(82);
        }else if (temp.equals("55 AA 16 04 00 1A")) {
            //音量-键
            sendKeyEvent(25);
        }else if (temp.equals("55 AA 16 03 00 19")) {
            //音量+键
            sendKeyEvent(24);
        }

    }*/


    public static void sendKeyEvent(final int KeyCode) {
        new Thread() { //不可在主线程中调用,所以这里使用线程发送
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


}
