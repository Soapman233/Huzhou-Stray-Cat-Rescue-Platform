import axios from "axios";
import {ElMessage} from "element-plus";
import config from '../config/config';

const request = axios.create({
    baseURL: config.baseURL,
    timeout: 30000  // 后台接口超时时间
})

// request 拦截器
// 可以自请求发送前对请求做一些处理
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    return config
}, error => {
    return Promise.reject(error)
});

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
    response => {
        let res = response.data;
        // 兼容服务端返回的字符串数据
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }

        // 处理统一返回格式
        if (res.code === '200') {
            return res.data;
        } else {
            ElMessage.error(res.msg || '系统错误');
            return Promise.reject(new Error(res.msg || '系统错误'));
        }
    },
    error => {
        if (error.response) {
            if (error.response.status === 404) {
                ElMessage.error('未找到请求接口');
            } else if (error.response.status === 500) {
                ElMessage.error('系统异常，请查看后端控制台报错');
            } else {
                ElMessage.error(error.response.data?.msg || '请求失败');
            }
        } else if (error.request) {
            ElMessage.error('无法连接到服务器，请检查网络连接');
        } else {
            ElMessage.error(error.message);
        }
        return Promise.reject(error);
    }
)

export default request