Component({
  data: {
    selected: 0
  },
  methods: {
    onSwitch(e) {
      const index = Number(e.currentTarget.dataset.index)
      const path = e.currentTarget.dataset.path
      if (!path) {
        return
      }
      this.setData({ selected: index })
      wx.switchTab({ url: `/${path}` })
    }
  }
})

