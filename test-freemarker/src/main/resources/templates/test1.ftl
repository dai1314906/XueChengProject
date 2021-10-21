<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Hello World</title>
</head>
<body>
    Hello ${name}!
    <br>
    遍历数据模型中的list学生信息（数据模型中的名称为stus)
    <table>
        <tr>
            <td>序号</td>
            <td>姓名</td>
            <td>年龄</td>
            <td>金额</td>
            <td>出生日期</td>
        </tr>
        <#if stus??>
        <#list stus as stu>
            <tr>
            <td>${stu_index+1}</td>
            <td <#if stu.name == '小明'>style="background: gainsboro;" </#if>>${stu.name}</td>
            <td>${stu.age}</td>
            <td>${stu.money}</td>
            <td>${stu.birthday?string("YYYY年MM月dd日")}</td>
            </tr>
        </#list>
            <br>
            list的大小:${stus?size}
            <br>
            ${point}
        </#if>
    </table>
    <br>
    <#assign text="{'bank':'工商银行','account':'20218172256'}" />
    <#assign data = text?eval />
    开户行: ${data.bank} 帐号:${data.account}
    <br>
    遍历数据模型中的stuMap(map数据) 第一种方法:在中扩号中填写map的key，第二种方法:在map后边直接加“点key "
    <br/>
    姓名:${(stuMap['stu1'].name)!''}<br>
    年龄:${(stuMap['stu1'].age)!''}<br>
    姓名:${(stuMap.stu1.name)!''}<br>
    年龄:${(stuMap.stu1.age)!''}<br>
    <br>
    遍历map中的key.stuMap?keys就是key列表（是一个1ist)
    <#list stuMap?keys as k>
        姓名:${stuMap[k].name!''}<br>
        年龄:${stuMap[k].age!''}<br>
    </#list>
</body>
</html>