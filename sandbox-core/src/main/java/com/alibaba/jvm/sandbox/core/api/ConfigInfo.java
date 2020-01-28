package com.alibaba.jvm.sandbox.core.api;

import com.alibaba.jvm.sandbox.core.CoreConfigure;

import java.net.InetSocketAddress;

/**
 * 沙箱配置信息
 *
 * @author luanjia@taobao.com
 */
public interface ConfigInfo {

    /**
     * 获取沙箱的命名空间
     *
     * @return 沙箱的命名空间
     * @since {@code sandbox-common-api:1.0.2}
     */
    String getNamespace();

    /**
     * 获取沙箱的加载模式
     *
     * @return 沙箱加载模式
     */
    CoreConfigure.Mode getMode();

    /**
     * 判断沙箱是否启用了unsafe
     * <p>unsafe功能启用之后，沙箱将能修改被BootstrapClassLoader所加载的类</p>
     * <p>在<b>${SANDBOX_HOME}/cfg/sandbox.properties#unsafe.enable</b>中进行开启关闭</p>
     *
     * @return true:功能启用;false:功能未启用
     */
    boolean isEnableUnsafe();

    /**
     * 获取沙箱的HOME目录(沙箱主程序目录)
     * 默认是在<b>${HOME}/.sandbox</b>
     *
     * @return 沙箱HOME目录
     */
    String getHome();

    /**
     * 获取沙箱版本号
     *
     * @return 沙箱版本号
     */
    String getVersion();

}
