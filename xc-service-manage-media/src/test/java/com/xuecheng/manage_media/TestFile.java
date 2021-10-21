package com.xuecheng.manage_media;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TestFile {
    //测试文件的分块
    @Test
    public void testChunk() throws IOException {
        //源文件
        File sourceFile = new File("F:\\ffpegtest\\lucene.avi");
        //块文件目录
        String chunkFileFolder = "F:\\ffpegtest\\chunks\\";
        //定义块的大小
        long chunkFileSize = 1*1024*1024;
        //块数
        long chunkFileNum = (long) Math.ceil(sourceFile.length()*1.0/chunkFileSize);

        //创建读文件的对象
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile,"r");

        //缓冲区
        byte[] bytes = new byte[1024];
        //读取块文件
        for (int i = 0; i < chunkFileNum; i++) {
            File chunkFile = new File(chunkFileFolder + i);
            RandomAccessFile raf_write = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len= raf_read.read(bytes))!=-1){
                //创块文件的写对象
                raf_write.write(bytes,0,len);
                if (chunkFile.length()>=chunkFileSize){
                    //如果块文件的大小达到1M开始写下一块儿
                    break;
                }
            }
            raf_write.close();
        }
        raf_read.close();
    }
    //测试文件合并
    @Test
    public void testMergeFile() throws IOException {
        //块文件目录
        String chunkFileFolderPath = "F:\\ffpegtest\\chunks\\";
        //快文件目录对象
        File chunkFileFolder = new File(chunkFileFolderPath);
        //快文件列表
        File[] files = chunkFileFolder.listFiles();

        //将文件排序，考虑到乱序的情况
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName())>Integer.parseInt(o2.getName())){
                    return 1;
                }
                return -1;
            }
        });

        //合并文件
        File mergeFile = new File("F:\\ffpegtest\\lucene_merge.avi");

        //创建新文件
        boolean newFile = mergeFile.createNewFile();
        byte[] bytes = new byte[1024];
        //创建写对象
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile,     "rw");
        for (File chunkFile : fileList) {
            //创建读文件的对象
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile,"r");
            int len = -1;
            while ((len = raf_read.read(bytes))!=-1){
                raf_write.write(bytes,0,len);

            }
            raf_read.close();
        }
        raf_write.close();
    }
}
