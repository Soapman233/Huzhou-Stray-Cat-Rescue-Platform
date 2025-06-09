import axios from "axios";
import { ElMessage } from "element-plus";

const request = axios.create({
    baseURL: '',  // 保持为空，因为我们使用的是相对路径
    timeout: 30000,
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

    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    // 从 localStorage 获取 token（如果有的话）
    const token = localStorage.getItem('token');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config
}, error => {
    console.error('Request Error:', error);
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
        console.error('Response Error:', {
            status: error.response?.status,
            data: error.response?.data,
            message: error.message
        });

        if (error.response) {
            switch (error.response.status) {
                case 400:
                    ElMessage.error(error.response.data.message || '请求参数错误');
                    break;
                case 401:
                    ElMessage.error('未登录或登录已过期');
                    // 可以在这里处理登录过期的逻辑，比如跳转到登录页
                    localStorage.removeItem('token');
                    localStorage.removeItem('user');
                    localStorage.removeItem('role');
                    window.location.href = '/hzsca/login';
                    break;
                case 403:
                    ElMessage.error('没有权限访问该资源');
                    break;
                case 404:
                    ElMessage.error('请求的资源不存在');
                    break;
                case 500:
                    ElMessage.error('服务器内部错误');
                    break;
                default:
                    ElMessage.error(error.response.data.message || '请求失败');
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