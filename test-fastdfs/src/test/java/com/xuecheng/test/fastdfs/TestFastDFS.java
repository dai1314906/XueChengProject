package com.xuecheng.test.fastdfs;

import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {

    //长传文件
    @Test
    public void testUpload(){
        //加载fastdfs-client.properties
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //定义TrackerClient ,用于请求TrackerServer
            TrackerClient trackerClient = new TrackerClient();
            //连接tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取storage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);

            //创建storageClient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storeStorage);
            //往storage服务器上传文件
            //本地文件路径
            String filePath = "D:/1.jpg";
            //上传文件成功后获得到文件id
            String fileId = storageClient1.upload_file1(filePath, "jpg", null);
            System.out.println(fileId);

        } catch (   Exception e) {
            e.printStackTrace();
        }


    }
    //下载文件


}
