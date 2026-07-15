package com.zhumengmiao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class 请直接帮我修改当前项目中相关页面文件的全部代码，无需在聊天框中生成完整代码块，直接应用修改到文件上，帮我重构出类似高清壁纸网站那种清透、无缝衔接的高级感视觉：

        1. 实现全屏无缝衔接与去遮罩化（参考必应首页效果）：
        - 目前右侧内容区（首页、视频页、相册页）整体被包裹在一层很大的生硬白底或高斯模糊图层里，与背景断层严重。
        - 请去掉这层大面积的遮罩容器背景，让项目的 123.jpg 背景图真正在右侧“全屏全量透出来”。
        - 将“实践视频”、“活动相册”等页面的主体文字、分类标签（全部、实践记录等）直接作为“悬浮元素”放置在全屏背景图上。

        2. 首页标题与布局微调（图2）：
        - 请将“助苗·助梦·护苗”大标题的颜色修改为更显眼、有温度的暖色（如温暖的阳光橙 #FF9F43 或温暖深金 #D35400），并在文字后方加上一层极淡的白色或深色文字阴影（text-shadow），确保在合照背景下清晰可读。
        - 在图2的数字统计区（1 1 0 实践视频/活动图片/用户留言），请为它们整体加上一层轻薄的半透明白边毛玻璃卡片框（如 background: rgba(255, 255, 255, 0.15); border: 1px solid rgba(255, 255, 255, 0.25);），让数字有精致的边界感。

        3. 改造标签栏与按钮的高级感（图3、图4）：
        - 图4中“全部、实践记录...”这排白色标签和右侧纯黄色的“分享我的实践视频”按钮颜色太实、太深，在画面中显得非常生硬和突兀。
        - 请将这整排标签栏和按钮全部改成高定半透明质感：
        * 未选中的标签：改成完全透明背景，仅保留白色文字。
        * 选中的标签与右侧按钮：改成半透明的暖橙色/暖黄色（如 background: rgba(255, 159, 67, 0.4); 结合 backdrop-filter: blur(5px);），并且边框换成淡淡的半透明高光边线。

请直接开始自动修改并 Build 对应的全局样式与组件，给整个网页做一次高透玻璃态的视觉升级！ZhumengmiaoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZhumengmiaoApplication.class, args);
    }
}
