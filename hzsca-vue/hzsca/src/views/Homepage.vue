<template>
  <div>
    <!-- 走马灯开始 -->
    <div class="carousel-container">
      <el-carousel height="500px" :interval="5000" arrow="always" :autoplay="true">
        <el-carousel-item v-for="item in banners" :key="item.id">
          <div class="carousel-item">
            <img
                :src="item.imageUrl"
                :alt="`Banner ${item.id}`"
                class="carousel-image"
                @load="handleImageLoad"
            />
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
    <!-- 走马灯结束 -->

    <div class="adoption-section">
      <div class="content-wrapper">
        <!-- 标题 -->
        <div class="section-title">
          <span>待领养的猫猫</span>
        </div>
        <!-- 加载状态 -->
        <div v-if="loading" class="loading">加载中...</div>
        <!-- 猫咪图片开始 -->
        <div v-else class="image-gallery">
          <div v-for="cat in displayCats" :key="cat.AnimalID" class="image-placeholder" @click="goToDetail(cat.AnimalID)">
            <img
                :src="getImageUrl(cat)"
                alt="Cat Image"
                class="cat-image"
                @error="handleImageError"
                @load="handleCatImageLoad"
            >
            <div class="cat-info">
              <h5>{{ cat.Name || '未知名字' }}</h5>
              <p>{{ cat.Age || '未知' }} 岁</p>
            </div>
          </div>
        </div>
        <!-- 猫咪图片结束 -->

        <!-- 统计区域开始 -->
        <div class="stats-box">
          <div class="stats-item">
            <div class="stats-number">56</div>
            <div class="stats-label">待领养猫</div>
          </div>
          <div class="stats-item">
            <div class="stats-number">4915</div>
            <div class="stats-label">已领养</div>
          </div>
        </div>
        <!-- 统计区域结束 -->
      </div>
    </div>

    <!--养猫须知开始-->
    <div class="knowledge-section">
      <div class="knowledge-wrapper">
        <div class="section-title" style="padding-top: 20px">
          <span class="mr-4" style="padding-left: 5px"> 养 猫 须 知 </span>
        </div>
      </div>

      <el-collapse :expand-icon-position="position">
        <el-collapse-item title="家居环境" name="1">
          <div class="collapse-content">
            <div class="text-content">
              <div>
                🐱 对猫咪来说，爬窗、跳高、磨爪子及啃咬植物等，都属于正常行为，故领养猫咪前必須在家居位置上面作出一点改变，以下事项可供参考：
              </div>
              <div>
                🐱 初养猫咪的时候，为了防止猫咪作出可能的破坏行为，尽可能将家具精简化，并且把摆饰品放在有门的饰柜内。
              </div>
              <div>
                🐱 猫有很强的跳跃能力，为免猫咪在跳跃时把家具弄翻，故家中的家具必须稳固。
              </div>
              <div>
                🐱 如家里有籐编、皮制或布艺沙发，可在表面盖上一块布，而墙角和木制家居脚可装上磨爪板，以防猫咪爪花，最重要是提供适当的磨爪板来满足猫咪的需求。如图示：
              </div>
            </div>
            <img src="/src/assets/sofa.jpg" alt="Sofa Protection" class="collapse-image"/>
          </div>
          <div class="attention">
            注：领养人士请明白动物有其天生的行为，请用爱心及耐性好好教导猫咪。
          </div>

        </el-collapse-item>
        <el-collapse-item title="领取猫咪回家" name="2">
          <div class="collapse-content">
            <div class="text-content">
              <div>
                🐱 预备一个宠物笼
              </div>
              <div class="attention">
                注：以防在运载猫咪途中意外让它逃脱，宠物笼必须用胶带索好。
              </div>
              <div>
                🐱 在领猫咪回家前，请预备好饲养铁笼（最小尺寸为3尺，双层笼更佳）。宠外需罩上布，并放置好砂盆、
                食物碗、水碗、舒适的小猫床、猫抓板及玩具。
              </div>
              <div>
                🐱 先把猫咪安置在笼中一段时间，待猫咪对你和新环境不再害怕便可离开饲养铁笼。若家中原本已有猫咪，两猫咪须先隔离
                14天方可接触。
              </div>
              <div>
                🐱 许多猫咪刚到新家时会感到非常害怕、没有安全感，因此有可能会误伤主人。
              </div>
              <div>
                🐱 你可预备手套，温柔的轻抚猫咪、跟它说话，待它适应了，自然会主动来亲近你。切忌在开始时遇上挫折便退还给送养人，否则只是错失了一只值得疼爱的好猫咪。
              </div>
              <div>
                <img src="/src/assets/home-gp.jpg" class="collapse-image"/>
              </div>
              <div class="attention">
                注 : 让猫咪主动亲近你是非常重要的，代表著它已经信任你。强迫猫咪只会让它感到紧张而抗拒，亦有可能导致攻击行为，
                或是影响日后与主人的相处。
              </div>
            </div>
          </div>
        </el-collapse-item>
        <el-collapse-item title="猫粮" name="3">
          <div class="collapse-content">
            <div class="text-content">
              <div>
                🐱 猫粮可以大致分为干粮、湿粮及鲜食（粮食必须健康优质，以成份天然、有机、少化学品较可取，猫主应多作资料搜集，亲自了解有关成份。），而一般在志愿机构领回来的猫咪都已懂得吃干粮。
              </div>
              <div>
                🐱 猫咪的消化系统与人和狗不同，所以切忌让猫咪进食人类食物和狗粮。
              </div>
              <div>
                🐱 猫咪严禁进食以下食品：
              </div>
              <div style="padding-left: 25px">
                <div>
                  巧克力 鲍鱼 未经烹调的蛋白 咖啡因 葡萄与葡萄干
                </div>
                <div>
                  蘑菇 味精 未经烹调的马铃薯 含酒精饮料
                </div>
                <div>
                  蕃茄的蒂／叶／茎
                </div>
                <div>
                  葱类：青葱、洋葱、韭菜、红葱头等
                </div>
                <div>
                  茶硷：啡、茶类、可乐、可可等
                </div>
                <div>
                  辛香料：胡椒、辣椒、芥末、七味粉、辣椒粉等
                </div>
                <div>
                  骨头：鸡、鸭、鹅、鱼等骨头
                </div>
                <img src="/src/assets/food-gp.jpg" class="collapse-image"/>
              </div>
              <div>
                🐱 以下植物对猫咪有害，不可给猫咪啃咬：
              </div>
              <div style="padding-left: 25px">
                <div>
                  圣诞红 杜鹃花 万年青 常春藤 菊花 水仙（球茎部份）
                </div>
                <div>
                  甜豌豆杏树 桃树 橡树 百合类 金针类
                </div>
              </div>
              <div>
                🐱 主人可因应猫咪的身体情况而考虑给猫咪服用补充食品，如L-Lysine可预防、舒缓因疱疹病毒引起之眼、鼻感染，减少眼睛分泌物产生；Osteosupport
                可减轻关节炎症状。
              </div>
              <div>
                🐱 零食：只可在亲子活动时以奖励方式给猫咪，只可当小食而不可作主粮。
              </div>
            </div>
          </div>
        </el-collapse-item>
        <el-collapse-item title="日常清洁" name="4">
          <div class="collapse-content">
            <div class="text-content">
              <div>
                🐱 猫是很爱干净的动物，一般唐猫每天都会把自己把毛和脸清洗得干干净净，主人为它们洗澡的次数不需太频密。但间中也要为它们梳理毛发，有助促进皮肤的新陈代谢
                。
              </div>
              <div class="attention">
                注：如湿洗的话，必须彻底过清皂液，及于浴后替猫咪吹干毛发，特别是肚上的毛，否则容易感冒。
              </div>
              <div>
                🐱 某些品种的猫鼻子内陷、泪腺较短、眼睛附近经常有分泌物出现、毛发长容易打结及耳朵常有污垢，主人需每天帮其清理。
              </div>
              <div>
                🐱 使用生理盐水洗脸（可在水里加点盐代替）以加强清洁作用，冬天要用温水，拿湿毛巾轻轻擦拭眼睛及鼻。
              </div>
              <div>
                🐱 清除耳朵表面污垢可用棉花棒蘸少许橄榄油一点点擦拭，但在耳道内的污垢则要请兽医将其清除。
              </div>
              <div>
                🐱 主人亦可购买洗眼水和洗耳水为猫咪清洁。
              </div>
              <div>
                🐱 若时间许可，最好每週为猫咪刷一次牙 （要用猫用牙膏），以防牙垢堆积，若有太大困难或可请教兽医相关做法。
              </div>
              <div>
                🐱 猫越早适应刷牙，这项工作就越容易完成。
              </div>
              <div>
                🐱 每週要为猫咪修剪趾甲，修剪时不能剪太多太深，一般只剪除爪的1/3左右，并应锉平整。
              </div>
              <div>
                🐱 要经常清理猫砂盘，最好能在猫咪如厕后立即清理。若环境许可，每只猫咪有独立砂盘为最佳。
              </div>
            </div>
          </div>
        </el-collapse-item>
        <el-collapse-item title="保健" name="5">
          <div class="collapse-content">
            <div class="text-content">
              <div>
                🐱 每年要为猫咪注射防疫针 (FVRCP (F3) 三合一防疫针)以预防三种疾病—白血球减少症、鼻气管炎及卡他性病毒。
              </div>
              <div>
                🐱 驱虫药：约 3 个月服一次，以防感染肠胃寄生虫，如有疑问请依兽医指示。
              </div>
              <div>
                🐱 驱虱药：每月一次，滴于颈背上之皮肤，可于兽医或宠物用品店购买。
              </div>
              <div>
                🐱 主人必须要为猫咪绝育，一般于五至六个月大，便可接受绝育手术。
              </div>
              <div>
                🐱 绝育后有效减少睾丸肿瘤、摄护腺肥大、泌尿系统闭塞，卵巢囊肿、子宫蓄脓、乳房肿瘤、卵巢肿瘤及子宫肌瘤等疾病。另外，也可减少打斗及随处撒尿的问题和阻止猫女发情时的声浪骚扰。
              </div>
              <div>
                🐱 如发现猫咪有身体不适，请尽快带猫咪到动物诊所求医。
              </div>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
    <!--养猫须知结束-->
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import {useRouter} from 'vue-router';
import request from '../utils/request';
import config from '../config/config';
import '/src/assets/AdoptionSection.css';

const router = useRouter();
const banners = ref([
  {id: 1, imageUrl: new URL('/src/assets/banners/banner1.jpg', import.meta.url).href, loaded: false},
  {id: 2, imageUrl: new URL('/src/assets/banners/banner2.jpg', import.meta.url).href, loaded: false},
  {id: 3, imageUrl: new URL('/src/assets/banners/banner3.jpg', import.meta.url).href, loaded: false},
  {id: 4, imageUrl: new URL('/src/assets/banners/banner4.jpg', import.meta.url).href, loaded: false}
]);

// 猫咪数据
const cats = ref([]);
const loading = ref(false);
const displayCats = ref([]);

const goToDetail = (id) => {
  router.push(`/hzsca/adopt/${id}`);
};

const getImageUrl = (cat) => {
  try {
    console.log('Processing cat image:', cat);
    if (!cat || !cat.ImageURL) {
      console.log('No image URL provided, using default image');
      // 使用一个简单的占位符图片
      return 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjVmNWY1Ii8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPuWbvueJh+WbvueJhzwvdGV4dD48L3N2Zz4=';
    }

    // 检查是否是完整的 URL
    if (cat.ImageURL.startsWith('http://') || cat.ImageURL.startsWith('https://')) {
      console.log('Using full URL:', cat.ImageURL);
      return cat.ImageURL;
    }

    // 检查是否是 Base64 数据
    if (cat.ImageURL.startsWith('data:image')) {
      console.log('Using Base64 image data');
      return cat.ImageURL;
    }

    // 处理相对路径
    const baseUrl = config.imageBaseURL || 'http://localhost:8080';
    const cleanImageUrl = cat.ImageURL.startsWith('/') ? cat.ImageURL.substring(1) : cat.ImageURL;
    const fullUrl = `${baseUrl}/${cleanImageUrl}`;
    console.log('Generated full URL:', fullUrl);
    return fullUrl;
  } catch (err) {
    console.error('Error processing image URL:', err);
    return 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjVmNWY1Ii8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPuWbvueJh+WbvueJhzwvdGV4dD48L3N2Zz4=';
  }
};

const handleImageError = (e) => {
  console.log('Image load error, using default image');
  e.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjVmNWY1Ii8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPuWbvueJh+WbvueJhzwvdGV4dD48L3N2Zz4=';
};

const handleImageLoad = (e) => {
  const img = e.target;
  const banner = banners.value.find(b => b.imageUrl === img.src);
  if (banner) {
    banner.loaded = true;
  }
};

const handleCatImageLoad = (e) => {
  console.log('Cat image loaded successfully:', e.target.src);
  // 可以在这里添加图片加载成功的逻辑
};

const fetchCats = async () => {
  loading.value = true;
  try {
    const response = await request.get('/api/cats');
    console.log('API Response:', response);

    if (response.data && response.data.code === '200' && Array.isArray(response.data.data)) {
      cats.value = response.data.data;
      // 显示16只猫
      displayCats.value = response.data.data.slice(0, 16);
    } else {
      cats.value = [];
      displayCats.value = [];
      console.warn('Invalid data format or empty data');
    }
  } catch (error) {
    console.error('Failed to fetch cats:', error);
    cats.value = [];
    displayCats.value = [];
  } finally {
    loading.value = false;
  }
};

onMounted(fetchCats);
</script>

<style scoped>
.carousel-container {
  width: 100%;
  margin: 0 auto;
  overflow: hidden;
}

.carousel-item {
  height: 100%;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
}

:deep(.el-carousel__container) {
  height: 500px !important;
}

:deep(.el-carousel__item) {
  overflow: hidden;
}

:deep(.el-carousel__arrow) {
  background-color: rgba(31, 45, 61, 0.5);
  border-radius: 50%;
  transition: all 0.3s;
}

:deep(.el-carousel__arrow:hover) {
  background-color: rgba(31, 45, 61, 0.8);
}

/* 猫咪图片和信息样式 */
.cat-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  color: white;
  padding: 10px;
  transform: translateY(100%);
  transition: transform 0.3s ease;
}

.image-placeholder:hover .cat-info {
  transform: translateY(0);
}

.cat-info h5 {
  margin: 0 0 5px 0;
  font-size: 14px;
  font-weight: 600;
}

.cat-info p {
  margin: 0;
  font-size: 12px;
  opacity: 0.9;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.image-placeholder {
  position: relative;
  cursor: pointer;
}

.cat-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.image-placeholder:hover .cat-image {
  transform: scale(1.05);
}

/* 修改图片网格布局为2x8 */
.image-gallery {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 1rem;
  padding: 1rem;
  width: 100%;
}

.image-placeholder {
  position: relative;
  cursor: pointer;
  aspect-ratio: 1;
  overflow: hidden;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.cat-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .image-gallery {
    grid-template-columns: repeat(6, 1fr);
    grid-template-rows: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .image-gallery {
    grid-template-columns: repeat(4, 1fr);
    grid-template-rows: repeat(4, 1fr);
  }
}

@media (max-width: 768px) {
  .image-gallery {
    grid-template-columns: repeat(3, 1fr);
    grid-template-rows: repeat(6, 1fr);
  }
}

@media (max-width: 576px) {
  .image-gallery {
    grid-template-columns: repeat(2, 1fr);
    grid-template-rows: repeat(8, 1fr);
  }
}
</style>