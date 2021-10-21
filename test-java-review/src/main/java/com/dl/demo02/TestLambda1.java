package com.dl.demo02;

public class TestLambda1 {

    //静态内部类
    static class Like2 implements ILike{
        @Override
        public void lambda() {
            System.out.println("222222222222222");
        }
    }

    public static void main(String[] args) {
        ILike like = new Like();
        like.lambda();

        like = new Like2();
        like.lambda();
        //局部内部类
        class Like3 implements ILike{
            @Override
            public void lambda() {
                System.out.println("333333333333333333");
            }
        }
        like = new Like3();
        like.lambda();
        //匿名内部类
        like = new ILike() {
            @Override
            public void lambda() {
                System.out.println("4444444444444444444444444");
            }
        };
        like.lambda();


        //lambda实现
        like = ()->{
            System.out.println("55555555555555555");
        };
        like.lambda();
    }
}

interface ILike{
    void lambda();
}

//接口实现
class Like implements ILike{
    @Override
    public void lambda() {
        System.out.println("111111111111111");
    }
}
