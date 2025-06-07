<template>
  <div>
    <!-- 走马灯开始 -->
    <div class="block text-center">
      <el-carousel height="500px" motion-blur>
        <el-carousel-item v-for="item in banners" :key="item.id">
          <img :src="item.imageUrl" alt="Banner" style="width: 100%; height: 100%; object-fit: cover;" />
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
        <div v-if="cats.length === 0" class="loading">加载中...</div>
        <!-- 猫咪图片开始 -->
        <div v-else class="image-gallery">
          <div v-for="cat in cats" :key="cat.id" class="image-placeholder">
            <img
                v-if="cat.imageUrl"
                :src="cat.imageUrl"
                alt="Cat Image"
                class="cat-image"
                @error="cat.imageUrl = 'https://placekitten.com/150/150'"
            />
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
        <div class="knowledge-wrapper" >
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
              <img src="/src/assets/sofa.jpg" alt="Sofa Protection" class="collapse-image" />
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
              🐱 在领猫咪回家前，请预备好饲养铁笼（最小尺寸为3尺，双层笼更佳）。宠外需罩上布，并放置好砂盆、 食物碗、水碗、舒适的小猫床、猫抓板及玩具。
            </div>
            <div>
              🐱 先把猫咪安置在笼中一段时间，待猫咪对你和新环境不再害怕便可离开饲养铁笼。若家中原本已有猫咪，两猫咪须先隔离 14天方可接触。
            </div>
            <div>
              🐱 许多猫咪刚到新家时会感到非常害怕、没有安全感，因此有可能会误伤主人。
            </div>
                <div>
                  🐱 你可预备手套，温柔的轻抚猫咪、跟它说话，待它适应了，自然会主动来亲近你。切忌在开始时遇上挫折便退还给送养人，否则只是错失了一只值得疼爱的好猫咪。
                </div>
            <div>
            <img src="/src/assets/home-gp.jpg" class="collapse-image" />
            </div>
            <div class="attention">
              注 : 让猫咪主动亲近你是非常重要的，代表著它已经信任你。强迫猫咪只会让它感到紧张而抗拒，亦有可能导致攻击行为， 或是影响日后与主人的相处。
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
                  <img src="/src/assets/food-gp.jpg" class="collapse-image" />
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
                  🐱 主人可因应猫咪的身体情况而考虑给猫咪服用补充食品，如L-Lysine可预防、舒缓因疱疹病毒引起之眼、鼻感染，减少眼睛分泌物产生；Osteosupport 可减轻关节炎症状。
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
                  🐱 猫是很爱干净的动物，一般唐猫每天都会把自己把毛和脸清洗得干干净净，主人为它们洗澡的次数不需太频密。但间中也要为它们梳理毛发，有助促进皮肤的新陈代谢 。
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
import { ref, onMounted } from 'vue';
import axios from 'axios';
import '/src/assets/AdoptionSection.css';

const banners = ref([
  { id: 1, imageUrl: new URL('/src/assets/banners/banner1.jpg', import.meta.url).href },
  { id: 2, imageUrl: new URL('/src/assets/banners/banner2.jpg', import.meta.url).href },
  { id: 3, imageUrl: new URL('/src/assets/banners/banner3.jpg', import.meta.url).href },
  { id: 4, imageUrl: new URL('/src/assets/banners/banner4.jpg', import.meta.url).href }
]);

// 猫咪数据（占位符，建议替换为后端接口）
const cats = ref([]);

// 模拟从后端获取数据（可替换为真实接口）
onMounted(async () => {
  try {
    // 假设后端接口为 https://your-api-endpoint/cats
    // const response = await axios.get('https://your-api-endpoint/cats');
    // cats.value = response.data.map(cat => ({
    //   id: cat.id,
    //   imageUrl: cat.imageUrl
    // }));

    // 临时使用占位符图片
    cats.value = Array(16).fill().map((_, index) => ({
      id: index,
      imageUrl: `https://placekitten.com/150/150?image=${index + 1}`
    }));
  } catch (error) {
    console.error('Failed to fetch cat data:', error);
    // 失败时使用占位符
    cats.value = Array(16).fill().map((_, index) => ({
      id: index,
      imageUrl: `https://placekitten.com/150/150?image=${index + 1}`
    }));
  }
});
</script>

<style scoped>

</style>