# ============================================================
# HZSCA 后端服务镜像（多阶段构建）
# ------------------------------------------------------------
# 构建（在仓库根目录执行）：
#   docker build -f hzsca-springboot/hzsca/Dockerfile -t hzsca-backend ./hzsca-springboot/hzsca
#
# 运行：
#   docker run -d --name hzsca-app -p 8080:8080 \
#     -e DB_HOST=host.docker.internal -e DB_PORT=3306 \
#     -e DB_USERNAME=root -e DB_PASSWORD=1234 \
#     -e JWT_SECRET=不少于32字节的随机字符串 \
#     hzsca-backend
# ============================================================

# ---------- 阶段一：构建 ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# 先只复制 pom 并预拉依赖，利用 Docker 层缓存加速后续构建
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline || true

# 再复制源码进行编译打包
COPY src ./src
RUN mvn -B -q -DskipTests clean package

# ---------- 阶段二：运行 ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# 以非 root 用户运行，降低容器逃逸风险
RUN groupadd -r appuser && useradd -r -g appuser -u 1001 appuser

COPY --from=build /build/target/*.jar app.jar

# 上传目录（与 application.yml 的 file.upload.path=uploads 对应）
RUN mkdir -p /app/uploads && chown -R appuser:appuser /app
USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m -Dfile.encoding=UTF-8" \
    TZ=Asia/Shanghai

# 健康检查：容器内探测后端是否已就绪
HEALTHCHECK --interval=15s --timeout=5s --start-period=60s --retries=5 \
    CMD wget -q -O /dev/null http://127.0.0.1:8080/api/payment/config || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
