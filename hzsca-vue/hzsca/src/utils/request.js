import axios from "axios";
import { ElMessage } from "element-plus";
import router from '../router'

const request = axios.create({
    baseURL: '/api',  // API 的基础URL
    timeout: 5000,  // 请求超时时间
    headers: {
        'Content-Type': 'application/json'
    },
    withCredentials: true  // 允许跨域请求携带凭证
})

// request 拦截器
// 可以自请求发送前对请求做一些处理
request.interceptors.request.use(config => {
    console.log('Request Config:', {
        url: config.url,
        method: config.method,
        data: config.data
    });

    // 从 localStorage 获取 token（如果有的话）
    const token = localStorage.getItem('token');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config
}, error => {
    console.error('Request error:', error);
    return Promise.reject(error)
});

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
    response => {
        console.log('Response:', {
            status: response.status,
            data: response.data
        });
        return response;
    },
    error => {
        console.error('Response error:', error);

        if (error.response) {
            switch (error.response.status) {
                case 400:
                    ElMessage.error(error.response.data.message || '请求参数错误');
                    break;
                case 401:
                    // 未授权，清除 token 并跳转到登录页
                    localStorage.removeItem('token');
                    localStorage.removeItem('userInfo');
                    ElMessage.error('登录已过期，请重新登录');
                    router.push('/login');
                    break;
                case 403:
                    ElMessage.error('没有权限访问该资源');
                    break;
                case 404:
                    ElMessage.error('请求的资源不存在');
                    break;
                case 500:
                    ElMessage.error('服务器错误，请稍后重试');
                    break;
                default:
                    ElMessage.error(error.response.data.message || '请求失败');
            }
        } else if (error.request) {
            // 请求已发出但没有收到响应
            ElMessage.error('网络错误，请检查您的网络连接');
        } else {
            // 请求配置出错
            ElMessage.error('请求配置错误');
        }
        return Promise.reject(error);
    }
)

export default request