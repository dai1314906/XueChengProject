package com.dl.demo01;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class TestThread1 implements Runnable{
    private String url;
    private String name;
    public TestThread1(String url,String name){
        this.url = url;
        this.name = name;
    }
    @Override
    public void run() {
        WebDownloader webDownloader = new WebDownloader();
        webDownloader.downloader(url,name);
        System.out.println("下载文件名为:"+name);
    }
    public static void main(String[] args) {
        TestThread1 test1 = new TestThread1("https://img14.360buyimg.com/n1/jfs/t1/194008/31/18182/44281/61167a01E97b26411/7aee3b26f8010bf8.jpg","小米手机1.jpg");
        TestThread1 test2 = new TestThread1("https://img14.360buyimg.com/n1/jfs/t1/194008/31/18182/44281/61167a01E97b26411/7aee3b26f8010bf8.jpg","小米手机2.jpg");
        TestThread1 test3 = new TestThread1("https://img14.360buyimg.com/n1/jfs/t1/194008/31/18182/44281/61167a01E97b26411/7aee3b26f8010bf8.jpg","小米手机3.jpg");

        /*test1.start();
        test2.start();
        test3.start();*/
        new Thread(test1).start();
        new Thread(test2).start();
        new Thread(test3).start();
    }
}
//下载器
class WebDownloader{
    //下载方法
    public void downloader(String url,String name){
        try {
            FileUtils.copyURLToFile(new URL(url),new File(name));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO异常");
        }
    }
}
