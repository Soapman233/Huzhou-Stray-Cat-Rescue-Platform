/**
 * ECharts 按需引入。
 *
 * 直接 `import * as echarts from 'echarts'` 会把全部图表类型与组件打包进来（约 1MB）。
 * 本项目只用到「饼图 + 柱状图」及 tooltip / legend / grid / title，因此按需注册，
 * 可显著减小管理端与工作报告页的加载体积。
 *
 * 如需新增图表类型，请在此处补充对应的 `echarts/charts` 或 `echarts/components`。
 */
import * as echarts from 'echarts/core'
import { BarChart, PieChart } from 'echarts/charts'
import {
  GridComponent,
  LegendComponent,
  TitleComponent,
  TooltipComponent
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

echarts.use([
  BarChart,
  PieChart,
  GridComponent,
  LegendComponent,
  TitleComponent,
  TooltipComponent,
  CanvasRenderer
])

export default echarts
