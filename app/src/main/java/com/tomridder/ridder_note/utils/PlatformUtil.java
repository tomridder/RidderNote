package com.tomridder.ridder_note.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.List;

public class PlatformUtil
{
    public static final String PACKAGE_WECHAT = "com.tencent.mm";
    public static final String PACKAGE_MOBILE_QQ = "com.tencent.mobileqq";
    public static final String PACKAGE_QZONE = "com.qzone";
    public static final String PACKAGE_SINA = "com.sina.weibo";

    // 判断是否安装指定app
    public static boolean isInstallApp(Context context, String app_package)
    {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
        if (pInfo != null)
        {
            for (int i = 0; i < pInfo.size(); i++)
            {
                String pn = pInfo.get(i).packageName;
                if (app_package.equals(pn))
                {
                    return true;
                }
            }
        }
        return false;
    }
    //
    public final static void shareQQ(Context mContext, String content)
    {
        if (PlatformUtil.isInstallApp(mContext,PlatformUtil.PACKAGE_MOBILE_QQ))
        {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
            intent.putExtra(Intent.EXTRA_TEXT, content);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity"));
            mContext.startActivity(Intent.createChooser(intent, "Share"));
        } else
        {
            Toast.makeText(mContext, "您需要安装QQ客户端", Toast.LENGTH_LONG).show();
        }
    }

    public final static void shareWechatFriend(Context mContext, String content)
    {
        if (PlatformUtil.isInstallApp(mContext,PlatformUtil.PACKAGE_MOBILE_QQ))
        {
            Intent intent = new Intent();
            ComponentName cop = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
            intent.setComponent(cop);
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra("android.intent.extra.TEXT", content);
//            intent.putExtra("sms_body", content);
            intent.putExtra("Kdescription", !TextUtils.isEmpty(content) ? content : "");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(Intent.createChooser(intent, "Share"));
        } else
        {
            Toast.makeText(mContext, "您需要安装微信客户端", Toast.LENGTH_LONG).show();
        }
    }
}

