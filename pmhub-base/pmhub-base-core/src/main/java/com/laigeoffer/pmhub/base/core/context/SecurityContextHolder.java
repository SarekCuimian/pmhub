package com.laigeoffer.pmhub.base.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.laigeoffer.pmhub.base.core.constant.SecurityConstants;
import com.laigeoffer.pmhub.base.core.core.text.Convert;
import com.laigeoffer.pmhub.base.core.utils.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 获取当前线程变量中的 用户id、用户名称、Token等信息 
 * 注意： 必须在网关通过请求头的方法传入，同时在HeaderInterceptor拦截器设置值。 否则这里无法获取
 *
 * @author canghe
 */
public class SecurityContextHolder {

    // 使用 TransmittableThreadLocal 来存储和传递线程局部变量。
    // TransmittableThreadLocal 允许在多个线程中传递变量，特别是在异步线程中，仍然能够获取到主线程中的值。
    private static final TransmittableThreadLocal<Map<String, Object>> THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * 设置当前线程中的用户信息
     *
     * @param key   键，标识用户信息的类型（如 USER_ID，USER_NAME）
     * @param value 用户信息的值
     */
    public static void set(String key, Object value) {
        // 获取当前线程的局部 Map
        Map<String, Object> map = getLocalMap();
        // 将用户信息存入 Map 中，如果值为空则存入空字符串
        map.put(key, value == null ? StringUtils.EMPTY : value);
    }

    /**
     * 获取当前线程中的用户信息（返回 String 类型）
     *
     * @param key 键，标识用户信息的类型
     * @return 当前线程中存储的值，如果没有找到则返回空字符串
     */
    public static String get(String key) {
        Map<String, Object> map = getLocalMap();
        // 将获取到的值转换成字符串，如果没有值则返回空字符串
        return Convert.toStr(map.getOrDefault(key, StringUtils.EMPTY));
    }

    /**
     * 获取当前线程中的用户信息（返回指定类型）
     *
     * @param key   键，标识用户信息的类型
     * @param clazz 要转换成的目标类型
     * @param <T>   泛型，返回值的类型
     * @return 当前线程中存储的值，转换为指定类型
     */
    public static <T> T get(String key, Class<T> clazz) {
        Map<String, Object> map = getLocalMap();
        // 将获取到的值转换成指定类型
        return StringUtils.cast(map.getOrDefault(key, null));
    }

    /**
     * 获取当前线程中的 Map，若没有则初始化一个空的 Map
     *
     * @return 当前线程的局部 Map
     */
    public static Map<String, Object> getLocalMap() {
        // 从 TransmittableThreadLocal 中获取当前线程的局部变量
        Map<String, Object> map = THREAD_LOCAL.get();
        // 如果没有值，则初始化一个空的 ConcurrentHashMap
        if (map == null) {
            map = new ConcurrentHashMap<String, Object>();
            THREAD_LOCAL.set(map); // 设置当前线程的局部变量
        }
        return map;
    }

    /**
     * 设置当前线程的局部变量 Map
     *
     * @param threadLocalMap 要设置的 Map
     */
    public static void setLocalMap(Map<String, Object> threadLocalMap) {
        THREAD_LOCAL.set(threadLocalMap);
    }

    /**
     * 获取当前线程中的用户 ID
     *
     * @return 用户 ID，如果没有则返回默认值 0L
     */
    public static Long getUserId() {
        // 获取当前线程中存储的用户 ID（通过 SECURITY_CONSTANTS.DETAILS_USER_ID）
        return Convert.toLong(get(SecurityConstants.DETAILS_USER_ID), 0L);
    }

    /**
     * 设置当前线程中的用户 ID
     *
     * @param account 要设置的用户 ID
     */
    public static void setUserId(String account) {
        // 将用户 ID 设置到当前线程的局部变量中
        set(SecurityConstants.DETAILS_USER_ID, account);
    }

    /**
     * 获取当前线程中的用户名
     *
     * @return 用户名
     */
    public static String getUserName() {
        return get(SecurityConstants.DETAILS_USERNAME);
    }

    /**
     * 设置当前线程中的用户名
     *
     * @param username 要设置的用户名
     */
    public static void setUserName(String username) {
        set(SecurityConstants.DETAILS_USERNAME, username);
    }

    /**
     * 获取当前线程中的用户 Key
     *
     * @return 用户 Key
     */
    public static String getUserKey() {
        return get(SecurityConstants.USER_KEY);
    }

    /**
     * 设置当前线程中的用户 Key
     *
     * @param userKey 要设置的用户 Key
     */
    public static void setUserKey(String userKey) {
        set(SecurityConstants.USER_KEY, userKey);
    }

    /**
     * 获取当前线程中的用户权限信息
     *
     * @return 用户权限
     */
    public static String getPermission() {
        return get(SecurityConstants.ROLE_PERMISSION);
    }

    /**
     * 设置当前线程中的用户权限信息
     *
     * @param permissions 要设置的权限信息
     */
    public static void setPermission(String permissions) {
        set(SecurityConstants.ROLE_PERMISSION, permissions);
    }

    /**
     * 清除当前线程的所有局部变量
     *
     * 清除线程中的 `THREAD_LOCAL`，即移除当前线程的所有用户信息。
     */
    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
