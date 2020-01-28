package com.alibaba.jvm.sandbox.core.manager.impl;

import com.alibaba.jvm.sandbox.core.CoreConfigure;
import com.alibaba.jvm.sandbox.core.api.ConfigInfo;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 默认配置信息实现
 *
 * @author luanjia@taobao.com
 */
class DefaultConfigInfo implements ConfigInfo {

    private final CoreConfigure cfg;

    public DefaultConfigInfo(CoreConfigure cfg) {
        this.cfg = cfg;
    }

    @Override
    public String getNamespace() {
        return cfg.getNamespace();
    }

    @Override
    public CoreConfigure.Mode getMode() {
        return cfg.getLaunchMode();
    }

    @Override
    public boolean isEnableUnsafe() {
        return cfg.isEnableUnsafe();
    }

    @Override
    public String getHome() {
        return cfg.getJvmSandboxHome();
    }

    @Override
    public String getVersion() {
        final InputStream is = getClass().getResourceAsStream("/com/alibaba/jvm/sandbox/version");
        try {
            return IOUtils.toString(is);
        } catch (IOException e) {
            // impossible
            return "UNKNOW_VERSION";
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}
