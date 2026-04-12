import{_ as w,p as g,q as y,v as u,d as n,b as i,w as t,e as l,a0 as f,aa as b,ab as D,x as S,ac as _,ad as B,o as r,a as M,t as Z,f as j,ae as A,af as N,ag as h}from"./index-DQTIZrVM.js";const z="/assets/sofa-DfBuLhX9.jpg",H="/assets/home-gp-D-zsMd2N.jpg",V="/assets/food-gp-CCPIxhGV.jpg",G={class:"carousel-container"},k={class:"carousel-item"},J=["src","alt"],Q={class:"adoption-section"},T={class:"content-wrapper"},E={key:0,class:"loading"},F={key:1,class:"image-gallery"},O=["onClick"],$=["src"],X={class:"cat-info"},Y={class:"knowledge-section"},q={__name:"Homepage",setup(K){const C=j(),p=g([{id:1,imageUrl:new URL("/assets/banner1-LAlP_OOu.jpg",import.meta.url).href,loaded:!1},{id:2,imageUrl:new URL("/assets/banner2-CSU4oRTI.jpg",import.meta.url).href,loaded:!1},{id:3,imageUrl:new URL("/assets/banner3-DQiucPTw.jpg",import.meta.url).href,loaded:!1},{id:4,imageUrl:new URL("/assets/banner4-HRcE02KC.jpg",import.meta.url).href,loaded:!1}]),c=g([]),I=g(!1),m=g([]),L=s=>{C.push(`/hzsca/adopt/${s}`)},R=s=>{try{if(console.log("Processing cat image:",s),!s||!s.ImageURL)return console.log("No image URL provided, using default image"),"data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjVmNWY1Ii8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPuWbvueJh+WbvueJhzwvdGV4dD48L3N2Zz4=";if(s.ImageURL.startsWith("http://")||s.ImageURL.startsWith("https://"))return console.log("Using full URL:",s.ImageURL),s.ImageURL;if(s.ImageURL.startsWith("data:image"))return console.log("Using Base64 image data"),s.ImageURL;const a=A.imageBaseURL||"http://localhost:8080",o=s.ImageURL.startsWith("/")?s.ImageURL.substring(1):s.ImageURL,d=`${a}/${o}`;return console.log("Generated full URL:",d),d}catch(a){return console.error("Error processing image URL:",a),"data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjVmNWY1Ii8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPuWbvueJh+WbvueJhzwvdGV4dD48L3N2Zz4="}},U=s=>{console.log("Image load error, using default image"),s.target.src="data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjVmNWY1Ii8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPuWbvueJh+WbvueJhzwvdGV4dD48L3N2Zz4="},x=s=>{const a=s.target,o=p.value.find(d=>d.imageUrl===a.src);o&&(o.loaded=!0)},P=s=>{console.log("Cat image loaded successfully:",s.target.src)};return y(async()=>{I.value=!0;try{const s=await S.get("/api/cats");console.log("API Response:",s),s.data&&s.data.code==="200"&&Array.isArray(s.data.data)?(c.value=s.data.data,m.value=s.data.data.slice(0,16)):(c.value=[],m.value=[],console.warn("Invalid data format or empty data"))}catch(s){console.error("Failed to fetch cats:",s),c.value=[],m.value=[]}finally{I.value=!1}}),(s,a)=>{const o=N,d=_,v=h,W=B;return r(),u("div",null,[n("div",G,[i(d,{height:"500px",interval:5e3,arrow:"always",autoplay:!0},{default:t(()=>[(r(!0),u(f,null,b(p.value,e=>(r(),M(o,{key:e.id},{default:t(()=>[n("div",k,[n("img",{src:e.imageUrl,alt:`Banner ${e.id}`,class:"carousel-image",onLoad:x},null,40,J)])]),_:2},1024))),128))]),_:1})]),a[16]||(a[16]=l()),n("div",Q,[n("div",T,[a[2]||(a[2]=n("div",{class:"section-title"},[n("span",null,"待领养的猫猫")],-1)),a[3]||(a[3]=l()),I.value?(r(),u("div",E,"加载中...")):(r(),u("div",F,[(r(!0),u(f,null,b(m.value,e=>(r(),u("div",{key:e.AnimalID,class:"image-placeholder",onClick:ln=>L(e.AnimalID)},[n("img",{src:R(e),alt:"Cat Image",class:"cat-image",onError:U,onLoad:P},null,40,$),a[1]||(a[1]=l()),n("div",X,[n("h5",null,Z(e.Name||"未知名字"),1),a[0]||(a[0]=l()),n("p",null,Z(e.Age||"未知")+" 岁",1)])],8,O))),128))])),a[4]||(a[4]=D(' <div class="stats-box" data-v-7bfc8494><div class="stats-item" data-v-7bfc8494><div class="stats-number" data-v-7bfc8494>56</div> <div class="stats-label" data-v-7bfc8494>待领养猫</div></div> <div class="stats-item" data-v-7bfc8494><div class="stats-number" data-v-7bfc8494>4915</div> <div class="stats-label" data-v-7bfc8494>已领养</div></div></div>',2))])]),a[17]||(a[17]=l()),n("div",Y,[a[14]||(a[14]=n("div",{class:"knowledge-wrapper"},[n("div",{class:"section-title",style:{"padding-top":"20px"}},[n("span",{class:"mr-4",style:{"padding-left":"5px"}}," 养 猫 须 知 ")])],-1)),a[15]||(a[15]=l()),i(W,{"expand-icon-position":s.position},{default:t(()=>[i(v,{title:"家居环境",name:"1"},{default:t(()=>a[5]||(a[5]=[n("div",{class:"collapse-content"},[n("div",{class:"text-content"},[n("div",null,`\r
                🐱 对猫咪来说，爬窗、跳高、磨爪子及啃咬植物等，都属于正常行为，故领养猫咪前必須在家居位置上面作出一点改变，以下事项可供参考：\r
              `),l(),n("div",null,`\r
                🐱 初养猫咪的时候，为了防止猫咪作出可能的破坏行为，尽可能将家具精简化，并且把摆饰品放在有门的饰柜内。\r
              `),l(),n("div",null,`\r
                🐱 猫有很强的跳跃能力，为免猫咪在跳跃时把家具弄翻，故家中的家具必须稳固。\r
              `),l(),n("div",null,`\r
                🐱 如家里有籐编、皮制或布艺沙发，可在表面盖上一块布，而墙角和木制家居脚可装上磨爪板，以防猫咪爪花，最重要是提供适当的磨爪板来满足猫咪的需求。如图示：\r
              `)]),l(),n("img",{src:z,alt:"Sofa Protection",class:"collapse-image"})],-1),l(),n("div",{class:"attention"},`\r
            注：领养人士请明白动物有其天生的行为，请用爱心及耐性好好教导猫咪。\r
          `,-1)])),_:1}),a[10]||(a[10]=l()),i(v,{title:"领取猫咪回家",name:"2"},{default:t(()=>a[6]||(a[6]=[n("div",{class:"collapse-content"},[n("div",{class:"text-content"},[n("div",null,`\r
                🐱 预备一个宠物笼\r
              `),l(),n("div",{class:"attention"},`\r
                注：以防在运载猫咪途中意外让它逃脱，宠物笼必须用胶带索好。\r
              `),l(),n("div",null,`\r
                🐱 在领猫咪回家前，请预备好饲养铁笼（最小尺寸为3尺，双层笼更佳）。宠外需罩上布，并放置好砂盆、\r
                食物碗、水碗、舒适的小猫床、猫抓板及玩具。\r
              `),l(),n("div",null,`\r
                🐱 先把猫咪安置在笼中一段时间，待猫咪对你和新环境不再害怕便可离开饲养铁笼。若家中原本已有猫咪，两猫咪须先隔离\r
                14天方可接触。\r
              `),l(),n("div",null,`\r
                🐱 许多猫咪刚到新家时会感到非常害怕、没有安全感，因此有可能会误伤主人。\r
              `),l(),n("div",null,`\r
                🐱 你可预备手套，温柔的轻抚猫咪、跟它说话，待它适应了，自然会主动来亲近你。切忌在开始时遇上挫折便退还给送养人，否则只是错失了一只值得疼爱的好猫咪。\r
              `),l(),n("div",null,[n("img",{src:H,class:"collapse-image"})]),l(),n("div",{class:"attention"},`\r
                注 : 让猫咪主动亲近你是非常重要的，代表著它已经信任你。强迫猫咪只会让它感到紧张而抗拒，亦有可能导致攻击行为，\r
                或是影响日后与主人的相处。\r
              `)])],-1)])),_:1}),a[11]||(a[11]=l()),i(v,{title:"猫粮",name:"3"},{default:t(()=>a[7]||(a[7]=[n("div",{class:"collapse-content"},[n("div",{class:"text-content"},[n("div",null,`\r
                🐱 猫粮可以大致分为干粮、湿粮及鲜食（粮食必须健康优质，以成份天然、有机、少化学品较可取，猫主应多作资料搜集，亲自了解有关成份。），而一般在志愿机构领回来的猫咪都已懂得吃干粮。\r
              `),l(),n("div",null,`\r
                🐱 猫咪的消化系统与人和狗不同，所以切忌让猫咪进食人类食物和狗粮。\r
              `),l(),n("div",null,`\r
                🐱 猫咪严禁进食以下食品：\r
              `),l(),n("div",{style:{"padding-left":"25px"}},[n("div",null,`\r
                  巧克力 鲍鱼 未经烹调的蛋白 咖啡因 葡萄与葡萄干\r
                `),l(),n("div",null,`\r
                  蘑菇 味精 未经烹调的马铃薯 含酒精饮料\r
                `),l(),n("div",null,`\r
                  蕃茄的蒂／叶／茎\r
                `),l(),n("div",null,`\r
                  葱类：青葱、洋葱、韭菜、红葱头等\r
                `),l(),n("div",null,`\r
                  茶硷：啡、茶类、可乐、可可等\r
                `),l(),n("div",null,`\r
                  辛香料：胡椒、辣椒、芥末、七味粉、辣椒粉等\r
                `),l(),n("div",null,`\r
                  骨头：鸡、鸭、鹅、鱼等骨头\r
                `),l(),n("img",{src:V,class:"collapse-image"})]),l(),n("div",null,`\r
                🐱 以下植物对猫咪有害，不可给猫咪啃咬：\r
              `),l(),n("div",{style:{"padding-left":"25px"}},[n("div",null,`\r
                  圣诞红 杜鹃花 万年青 常春藤 菊花 水仙（球茎部份）\r
                `),l(),n("div",null,`\r
                  甜豌豆杏树 桃树 橡树 百合类 金针类\r
                `)]),l(),n("div",null,`\r
                🐱 主人可因应猫咪的身体情况而考虑给猫咪服用补充食品，如L-Lysine可预防、舒缓因疱疹病毒引起之眼、鼻感染，减少眼睛分泌物产生；Osteosupport\r
                可减轻关节炎症状。\r
              `),l(),n("div",null,`\r
                🐱 零食：只可在亲子活动时以奖励方式给猫咪，只可当小食而不可作主粮。\r
              `)])],-1)])),_:1}),a[12]||(a[12]=l()),i(v,{title:"日常清洁",name:"4"},{default:t(()=>a[8]||(a[8]=[n("div",{class:"collapse-content"},[n("div",{class:"text-content"},[n("div",null,`\r
                🐱 猫是很爱干净的动物，一般唐猫每天都会把自己把毛和脸清洗得干干净净，主人为它们洗澡的次数不需太频密。但间中也要为它们梳理毛发，有助促进皮肤的新陈代谢\r
                。\r
              `),l(),n("div",{class:"attention"},`\r
                注：如湿洗的话，必须彻底过清皂液，及于浴后替猫咪吹干毛发，特别是肚上的毛，否则容易感冒。\r
              `),l(),n("div",null,`\r
                🐱 某些品种的猫鼻子内陷、泪腺较短、眼睛附近经常有分泌物出现、毛发长容易打结及耳朵常有污垢，主人需每天帮其清理。\r
              `),l(),n("div",null,`\r
                🐱 使用生理盐水洗脸（可在水里加点盐代替）以加强清洁作用，冬天要用温水，拿湿毛巾轻轻擦拭眼睛及鼻。\r
              `),l(),n("div",null,`\r
                🐱 清除耳朵表面污垢可用棉花棒蘸少许橄榄油一点点擦拭，但在耳道内的污垢则要请兽医将其清除。\r
              `),l(),n("div",null,`\r
                🐱 主人亦可购买洗眼水和洗耳水为猫咪清洁。\r
              `),l(),n("div",null,`\r
                🐱 若时间许可，最好每週为猫咪刷一次牙 （要用猫用牙膏），以防牙垢堆积，若有太大困难或可请教兽医相关做法。\r
              `),l(),n("div",null,`\r
                🐱 猫越早适应刷牙，这项工作就越容易完成。\r
              `),l(),n("div",null,`\r
                🐱 每週要为猫咪修剪趾甲，修剪时不能剪太多太深，一般只剪除爪的1/3左右，并应锉平整。\r
              `),l(),n("div",null,`\r
                🐱 要经常清理猫砂盘，最好能在猫咪如厕后立即清理。若环境许可，每只猫咪有独立砂盘为最佳。\r
              `)])],-1)])),_:1}),a[13]||(a[13]=l()),i(v,{title:"保健",name:"5"},{default:t(()=>a[9]||(a[9]=[n("div",{class:"collapse-content"},[n("div",{class:"text-content"},[n("div",null,`\r
                🐱 每年要为猫咪注射防疫针 (FVRCP (F3) 三合一防疫针)以预防三种疾病—白血球减少症、鼻气管炎及卡他性病毒。\r
              `),l(),n("div",null,`\r
                🐱 驱虫药：约 3 个月服一次，以防感染肠胃寄生虫，如有疑问请依兽医指示。\r
              `),l(),n("div",null,`\r
                🐱 驱虱药：每月一次，滴于颈背上之皮肤，可于兽医或宠物用品店购买。\r
              `),l(),n("div",null,`\r
                🐱 主人必须要为猫咪绝育，一般于五至六个月大，便可接受绝育手术。\r
              `),l(),n("div",null,`\r
                🐱 绝育后有效减少睾丸肿瘤、摄护腺肥大、泌尿系统闭塞，卵巢囊肿、子宫蓄脓、乳房肿瘤、卵巢肿瘤及子宫肌瘤等疾病。另外，也可减少打斗及随处撒尿的问题和阻止猫女发情时的声浪骚扰。\r
              `),l(),n("div",null,`\r
                🐱 如发现猫咪有身体不适，请尽快带猫咪到动物诊所求医。\r
              `)])],-1)])),_:1})]),_:1},8,["expand-icon-position"])])])}}},sn=w(q,[["__scopeId","data-v-7bfc8494"]]);export{sn as default};
