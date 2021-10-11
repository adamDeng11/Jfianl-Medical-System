package com.maven.common;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;
import com.jfinal.upload.MultipartRequest;
import com.maven.appointment.AppAppointmentController;
import com.maven.appointment.AppointmentController;
import com.maven.common.handler.UrlHandler;
import com.maven.common.interceptor.MyInterceptor;
import com.maven.common.model._MappingKit;
import com.maven.common.upload.TimeStampFileRenamePolicy;
import com.maven.common.util.AdminMenuKit;
import com.maven.doctorManage.DoctorManageController;
import com.maven.login.AppLoginController;
import com.maven.login.IndexController;
import com.maven.login.LoginController;
import com.maven.user.AppUserInfoController;
import com.maven.user.UserInfoController;

import java.lang.reflect.Field;

/**
 * 本 demo 仅表达最为粗浅的 jfinal 用法，更为有价值的实用的企业级用法
 * 详见 JFinal 俱乐部: http://jfinal.com/club
 * <p>
 * API 引导式配置
 */
public class _JFinalConfig extends JFinalConfig {

    static Prop p;

    /**
     * 启动入口，运行此 main 方法可以启动项目，此 main 方法可以放置在任意的 Class 类定义中，不一定要放于此
     */
    public static void main(String[] args) {
        UndertowServer.start(_JFinalConfig.class,8080,true);
    }

    /**
     * PropKit.useFirstFound(...) 使用参数中从左到右最先被找到的配置文件
     * 从左到右依次去找配置，找到则立即加载并立即返回，后续配置将被忽略
     */
    static void loadConfig() {
        if (p == null) {
            p = PropKit.useFirstFound("config.txt");
        }
    }

    /**
     * 配置常量
     */
    public void configConstant(Constants me) {
        loadConfig();

        me.setDevMode(p.getBoolean("devMode", true));

        /**
         * 支持 Controller、Interceptor、Validator 之中使用 @Inject 注入业务层，并且自动实现 AOP
         * 注入动作支持任意深度并自动处理循环注入
         */
        me.setInjectDependency(true);

        // 配置对超类中的属性进行注入
        me.setInjectSuperClass(true);
        me.setBaseUploadPath(p.get("uploadPath"));
        me.setBaseDownloadPath(p.get("uploadPath"));

        try {
            //上传文件重名
            Field field = MultipartRequest.class.getDeclaredField("fileRenamePolicy");
            field.setAccessible(true);
            //使用反射设置静态变量
            field.set(null, new TimeStampFileRenamePolicy());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * 配置路由
     */
    public void configRoute(Routes me) {

        //前端app登录相关
        me.add("/appLogin", AppLoginController.class);
        //前端app预约挂号相关
        me.add("/appAppointment", AppAppointmentController.class);
        //前端app用户管理相关
        me.add("/appUserInfo", AppUserInfoController.class);

        //后端管理员登录相关
        me.add("/index", IndexController.class, "/_admin/index");
        //后端登录相关
        me.add("/login", LoginController.class, "/login");
        //后端用户登录相关
        me.add("/userInfo", UserInfoController.class, "/_admin/userInfo");
        //后端医生管理相关
        me.add("/doctorManage", DoctorManageController.class, "/_admin/doctorManage");
        //后端预约挂号相关
        me.add("/appointment", AppointmentController.class, "/_admin/appointment");



    }

    public void configEngine(Engine me) {
        me.addSharedMethod(new AdminMenuKit());
        me.addSharedFunction("/base/base.html");
    }

    /**
     * 配置插件
     */
    public void configPlugin(Plugins me) {
        // 配置 druid 数据库连接池插件
        DruidPlugin druidPlugin = new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password").trim());
        me.add(druidPlugin);

        // 配置ActiveRecord插件
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        // 所有映射在 MappingKit 中自动化搞定
        _MappingKit.mapping(arp);
        me.add(arp);
    }

    public static DruidPlugin createDruidPlugin() {
        loadConfig();

        return new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password").trim());
    }

    /**
     * 配置全局拦截器
     */
    public void configInterceptor(Interceptors me) {
        me.add(new MyInterceptor());
    }

    /**
     * 配置处理器
     */
    public void configHandler(Handlers me) {
        me.add(new UrlHandler());
    }
}
